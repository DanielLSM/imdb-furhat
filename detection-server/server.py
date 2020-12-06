import vlc
import zmq
import time
import json
import numpy as np
import speech_recognition as sr
from urllib.request import urlopen
import random

from sentiment_analysis import SentimentAnalysis

from reviewer import Reviwer


class ServerProcessor:
    def __init__(self, file_path='launch.json'):
        self.file_path = file_path
        self.config = self._load_config()
        #Load YOLO
        self.r, self.asocket, self.insocket, self.outsocket = self._load_network_properties(
        )

        self.behaviours = {
            "movie": self.process_movie,
            "repeat review": self.process_repeat_review,
            "sentiment": self.process_sentiment,
            "choose": self.process_choice,
            "opinion": self.process_opinion
        }

        self.senti_analysist = SentimentAnalysis()
        self.reviewer = Reviwer()

        self.language = {'language': 'english'}
        self.movie = ""
        self.movie_id = ""
        self.sentiment = None

    def _load_network_properties(self):
        audio_socket = 'http://' + self.config["Audio_IP"] + ':' + self.config["audio_port"] + '/audio.wav'
        recognizer = sr.Recognizer()

        # Output results using a PUB socket
        context = zmq.Context()
        insocket = context.socket(zmq.SUB)
        insocket.setsockopt_string(zmq.SUBSCRIBE, '')
        insocket.bind("tcp://" + self.config["Dev_IP"] + ":" +
                      self.config["furhat_port"])

        context1 = zmq.Context()
        outsocket = context1.socket(zmq.PUB)
        outsocket.bind("tcp://" + self.config["Dev_IP"] + ":" +
                       self.config["send_to_furhat_port"])
        return recognizer, audio_socket, insocket, outsocket

    def _load_config(self):
        # Load configuration
        with open(self.file_path) as f:
            config = json.load(f)
        print(config)
        return config

    def process_movie(self):
        recognized_word = self.get_mic_input()
        print("SERVER: recognized_word is {}".format(recognized_word))
        movies_objs = self.reviewer.get_all_movies_objs(recognized_word)
        if (movies_objs is not None) and (len(movies_objs) > 0):
            self.movie_id = movies_objs[0].getID()
            self.movie = movies_objs[0].get('title')

        print("SERVER: movie is {}".format(self.movie))
        self.outsocket.send_string(self.movie)

    def process_repeat_review(self):
        recognized_str = self.get_mic_input()
        self.sentiment = recognized_str
        print("SERVER: user feedback is {}".format(self.sentiment))
        self.outsocket.send_string(self.sentiment)

    def process_sentiment(self):
        sentiment_score = self.senti_analysist.predict(self.sentiment)
        print(sentiment_score[0][0])

        if 0 < sentiment_score[0][0] <= 0.15:
            preview_sentiment = 'bad'
        elif 0.15 < sentiment_score[0][0] <= 0.40:
            preview_sentiment = 'slightly bad'
        elif 0.40 < sentiment_score[0][0] <= 0.60:
            preview_sentiment = 'average'
        elif 0.60 < sentiment_score[0][0] <= 0.80:
            preview_sentiment = 'pretty good'
        elif 0.80 < sentiment_score[0][0] <= 1:
            preview_sentiment = 'almost perfect'

        rating = round(sentiment_score[0][0] * 10)
        preview_sentiment = preview_sentiment + " with a rating of {} out of 10".format(
            rating)

        print("SERVER: sentiment analysis is {} on {}".format(
            preview_sentiment, sentiment_score[0]))

        self.outsocket.send_string(preview_sentiment)

    def process_choice(self):
        try:
            movies_list = self.reviewer.get_all_movies_objs(self.movie)
        except:
            movies_list = []
        json_data = json.dumps(movies_list)
        print("SERVER: sending movies list!")

        self.outsocket.send_string(json_data)

    def process_opinion(self):
        reviews = self.reviewer.get_reviews_from_id(self.movie_id)
        if len(reviews)>0:
            random_review = self.reviewer.get_first_review(reviews)
        else:
            random_review = random.choice([
                "I am sorry, I haven't seen this movie",
                f"Sorry, I didn't see {self.movie}"]) 

        print("SERVER: sending a review!")

        self.outsocket.send_string(random_review)

    def get_mic_input(self):
        recognized_str = 'None'
        while recognized_str == 'None':
            audio = self.get_audio()
            recognized_str = str(self.recognize(audio))
            if recognized_str != 'None':
                print(recognized_str)
        return recognized_str

    def broadcast(self):
        while True:
            message_furat = self.insocket.recv_string()
            print("Furhat says: {}".format(message_furat))
            self.behaviours[message_furat]()

    def get_audio(self):
        with sr.WavFile(urlopen(self.asocket)) as source:
            print("Say something!")
            # self.r.adjust_for_ambient_noise(source, duration=1)
            audio = self.r.listen(source, phrase_time_limit=5)
        return audio

    def recognize(self, audio):
        word = None
        try:
            word = self.r.recognize_google(audio)
        except sr.UnknownValueError:
            pass
            # print("Google Speech Recognition could not understand audio")
        except sr.RequestError as e:
            print(
                "Could not request results from Google Speech Recognition service; {0}"
                .format(e))
        return word

    def broadcast_speech(self):
        while True:
            audio = self.get_audio()
            recognized_str = str(self.recognize(audio))
            if recognized_str != None and recognized_str[0:2].lower(
            ) in AVAILABLE_LANGUAGES:
                self.language['language'] = LANGUAGES_DICT[
                    recognized_str[0:2].lower()]

    def update_language(self):
        while True:
            self.outsocket.send_string(self.language['language'])


if __name__ == "__main__":
    sp = ServerProcessor()
    sp.broadcast()
