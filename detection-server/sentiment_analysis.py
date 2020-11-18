# Commented out IPython magic to ensure Python compatibility.
import keras
keras.__version__

import tensorflow as tf
gpus = tf.config.experimental.list_physical_devices('GPU')
if gpus:
    try:
        for gpu in gpus:
            tf.config.experimental.set_memory_growth(gpu, True)
    except RuntimeError as e:
        print(e)

from keras.datasets import imdb
from keras.models import Sequential
from keras.layers import Embedding, Dense, Conv1D, Dropout, GlobalMaxPooling1D, Bidirectional, LSTM
from keras.preprocessing.sequence import pad_sequences
from keras.callbacks import ModelCheckpoint
# trick 1 regularizer
from keras.regularizers import L1L2
regularizer = L1L2(l1=0.0, l2=0.0)

import os
from sklearn.metrics import roc_auc_score
import matplotlib.pyplot as plt

from encoder import Encoder


class SentimentAnalysis:
    def __init__(self):
        self.output_dir = './'
        """#### Load data"""
        # trick 2) keep 5000 words
        self.n_unique_words = 8192
        self.encoder = Encoder(limit_unique_words=self.n_unique_words)
        # output directory name:
        """#### Preprocess data"""
        # trick 3) lower review lenght to train the LSTM
        self.max_review_length = 512
        self.pad_type = self.trunc_type = 'pre'
        """#### Design neural network architecture
        You can build a convnet by  stacking of `Conv1D` 
        and `MaxPooling1D` layers, eventually ending in either a global pooling layer or a `Flatten` layer, turning the 3D outputs into 2D outputs, 
        allowing to add one or more `Dense` layers to the model, for classification.

        ##### Baseline model
        """
        # trick 4) higher epoch count
        epochs = 10  #25
        batch_size = 128
        # trick 5) higher embedded size
        n_embed = 512
        model = Sequential()
        #trick 6 embedded on a 1/16
        model.add(
            Embedding(self.n_unique_words,
                      n_embed,
                      input_length=self.max_review_length))

        # trick 7) Bidirectional LSTM and a regularizer
        model.add(
            Bidirectional(
                LSTM(256,
                     return_sequences=True,
                     kernel_regularizer=regularizer)))

        # model.add(Conv1D(16, 5, activation='relu'))
        model.add(GlobalMaxPooling1D())

        # trick 8) 2 Dense layer at the end to help the classification 64 and 32 with dropout and regularizer
        model.add(Dense(64, activation="relu", kernel_regularizer=regularizer))
        model.add(Dropout(0.05))
        model.add(Dense(32, activation="relu", kernel_regularizer=regularizer))
        model.add(Dropout(0.05))
        # trick 9) change last layer to be a sigmoid instead of just Dense
        model.add(Dense(1, activation="sigmoid"))

        ##
        model.summary()
        """#### Compile the model"""

        model.compile(optimizer='adam',
                      loss='binary_crossentropy',
                      metrics=['accuracy'])

        model.load_weights(self.output_dir +
                           "/weights.08.hdf5")  # zero-indexed
        """#### Calculate the area under the curve for the ROC curve to get a scalar value to express the performance of the network
        This code only runs if you ensure that the output is a probability like you would get with a sigmoid (see Jon's model)
        """

        self.model = model

    def predict(self, review: str):
        encoded_review = self.encoder.encode_text(review)
        x_example = pad_sequences([encoded_review],
                                  maxlen=self.max_review_length,
                                  padding=self.pad_type,
                                  truncating=self.trunc_type,
                                  value=0)
        print('x_example shape:', x_example.shape)
        y_hat = self.model.predict(x_example)
        return y_hat


if __name__ == "__main__":
    sa = SentimentAnalysis()
    text = "Good actors, good direction, amazing performance. Overall really amazing"
    preview = sa.predict(text)
    print(preview)