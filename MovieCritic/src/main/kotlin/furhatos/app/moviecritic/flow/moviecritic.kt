package furhatos.app.moviecritic.flow
// package furhatos.app.moviecritic

import furhatos.app.moviecritic.getConnectedSocketSUB
import furhatos.app.moviecritic.getConnectedSocketPUB
import furhatos.flow.kotlin.*
import furhatos.nlu.common.*
import furhatos.util.Language
import furhatos.util.CommonUtils

import org.zeromq.ZMQ
import zmq.ZMQ.ZMQ_SUB
import zmq.ZMQ.ZMQ_PUB

val logger = CommonUtils.getRootLogger()
val inserv = "tcp://127.0.0.1:9998" //The TCP socket of the movie critic server
val outserv = "tcp://127.0.0.1:9999" //The TCP socket of the movie critic server


val inSocket: ZMQ.Socket = getConnectedSocketSUB(ZMQ_SUB, inserv) //Makes a socket of the object server
val outSocket: ZMQ.Socket = getConnectedSocketPUB(ZMQ_PUB, outserv) //Makes a socket of the object server
var message_in: String = "hello"
var movie_in: String = "movie"

val Start = state(Interaction) {
    onEntry {

        random(
            {   furhat.say("Hello! Can you name a movie?") },
            {   furhat.say("What is your favourite movie?") }
        )
        outSocket.send("movie")
        movie_in = inSocket.recvStr()
        goto(QuestionAboutMovie)

    }
}

val QuestionAboutMovie = state(Interaction) {
    onEntry {
        random(
            { furhat.say("What do you think about " + movie_in + " ?") },
            { furhat.say("Give me your review about the movie "+ movie_in) }
        )
        outSocket.send("repeat review")
        message_in = inSocket.recvStr()
        furhat.say(message_in) //just repeat
        goto(ReviewSentiment)
    }
}

val ReviewSentiment = state(Interaction) {
    onEntry {
        outSocket.send("sentiment")
        message_in = inSocket.recvStr()
        furhat.say(message_in) //just sentiment

        random(
            { furhat.say("I see, you consider the " +movie_in+ " movie to be " + message_in) },
            { furhat.say("So you think "+ movie_in+ " as a " + message_in)}
        )
        goto(MyOpinion)
    }
}

val MyOpinion = state(Interaction) {
    onEntry {

        random(
            { furhat.ask("Would you like to know my opinion?") },
            { furhat.ask("Can I give my opinion?")}
        )
    }
    onResponse<No> {
        furhat.say("Okay, that's a shame. Have a splendid day!")
        goto(Idle)
    }
    onResponse {
        outSocket.send("opinion")
        message_in = inSocket.recvStr()
        furhat.say(message_in) //just repeat
        goto(FinishOrStart)
    }

}

val FinishOrStart = state(Interaction) {
    onEntry {
        random(
            { furhat.ask("Would you like to talk about another movie or not?") },
            { furhat.ask("Do you wish to continue taking?")}
        )
    }

    onResponse<No> {
        furhat.say("Okay, that's a shame. Have a splendid day!")
        goto(Idle)
    }

    onResponse {
        goto(ReStart)
    }
}

