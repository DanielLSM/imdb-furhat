import vlc
import zmq
import time
import json
import numpy as np
import speech_recognition as sr
from urllib.request import urlopen

from recognizer import LANGUAGES_DICT, TRANSLATOR_DICT, AVAILABLE_LANGUAGES


class ServerProcessor:
    def __init__(self, file_path='launch.json'):
        self.file_path = file_path
        self.config = self._load_config()
        #Load YOLO
        self.r, self.asocket, self.insocket, self.outsocket = self._load_network_properties(
        )
        self.language = {'language': 'english'}

    def _load_network_properties(self):
        audio_socket = 'http://' + self.config["Audio_IP"] + ':8080/audio.wav'
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

    def broadcast(self):
        while True:
            message_furat = self.insocket.recv_string()
            print("Furhat says: {}".format(message_furat))
            # if message_furat == "hello":
            #     print(message_furat)
            recognized_str = 'None'
            while recognized_str == 'None':
                # print(message_furat)
                audio = self.get_audio()
                recognized_str = str(self.recognize(audio))
                if recognized_str != 'None':
                    print(recognized_str)
                    self.outsocket.send_string(recognized_str)
                # if recognized_str != None and recognized_str[0:2].lower(
                # ) in AVAILABLE_LANGUAGES:
                #     self.language['language'] = LANGUAGES_DICT[
                #         recognized_str[0:2].lower()]

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
