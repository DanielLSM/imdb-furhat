import numpy as np
from keras.datasets import imdb


class Encoder:
    def __init__(self, limit_unique_words=8192):
        self.limit_unique_words = limit_unique_words
        ib = imdb.get_word_index()
        self.ib_sorted = {
            k: v
            for k, v in sorted(ib.items(), key=lambda item: item[1])
        }

    def encode_text(self, text):
        split_text = text.split()
        enc_text = []
        for word in split_text:
            try:
                code = self.ib_sorted[word]
            except:
                code = 0

            if code <= self.limit_unique_words:
                enc_text.append(code)
        return np.array(enc_text)


if __name__ == "__main__":
    enc = Encoder()
    enc_txt = enc.encode_text("It was amazing")
    print(enc_txt)