package furhatos.app.moviecritic.flow
// package furhatos.app.moviecritic

import furhatos.app.moviecritic.nlu.DontKnow
import furhatos.app.moviecritic.nlu.MovieOption
import furhatos.app.moviecritic.nlu.MovieData
import furhatos.app.moviecritic.nlu.ChooseFirst
import furhatos.app.moviecritic.nlu.ChooseLast
import furhatos.app.moviecritic.nlu.MovieChoiceHolder
import furhatos.app.moviecritic.getConnectedSocketSUB
import furhatos.app.moviecritic.getConnectedSocketPUB
import furhatos.flow.kotlin.*
import furhatos.nlu.common.*
import furhatos.nlu.common.Number
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
        MovieChoiceHolder.resetChoice()
        random(
            {   furhat.say("Hello! Can you name a movie?") },
            {   furhat.say("What is your favourite movie?") }
        )
        outSocket.send("movie")
        movie_in = inSocket.recvStr()
        goto(ChooseMovie)
    }

    // The users answers that they don't know
    //onResponse<DontKnow> {
    //    furhat.say("Me neither") //just repeat
    //    reentry()
    //}
    //onResponse<Yes> {
    //    furhat.say("great!")
    //    reentry()
    //}
    //
    //onResponse<No> {
    //    furhat.say("oh well")
    //    reentry()
    //}
}

val ReStart : State = state {
    onEntry {
        MovieChoiceHolder.resetChoice()
        random(
            {   furhat.say("Which movie do youwant to talk about now?") },
            {   furhat.say("Which movie do you want to discuss?") }
        )
        outSocket.send("movie")
        movie_in = inSocket.recvStr()

        goto(ChooseMovie)
    }   
}

val ChooseMovie = state(Interaction) {
    onEntry {
        outSocket.send("choose")
        message_in = inSocket.recvStr()
        MovieChoiceHolder.setValues(message_in)
        var num_movies : Int = MovieChoiceHolder.movie_list!!.options.size
        if (num_movies > 1) {
            furhat.ask("I can think of ${num_movies} movies with that name" +
            "Do you mean " + MovieChoiceHolder.movie_list!!.getOptionsString()+ "?") //just repeat
        } else {
            var movie : MovieData = MovieChoiceHolder.raw_list!![0]
            MovieChoiceHolder.setChoice(movie)
            
            random(
                { furhat.say("Allright!") },
                { furhat.say("Got it!")},
                { furhat.say("OK!")}
            )

            goto(QuestionAboutMovie)
        }
    }

    onResponse<MovieOption> {
        val answer = it.intent
        MovieChoiceHolder.setChoice(answer.movie_data)
        random(
            { furhat.say("Allright!") },
            { furhat.say("Got it!")},
            { furhat.say("OK!")}
        )

        goto(QuestionAboutMovie)
    }

    onResponse<ChooseFirst> {
        var movie = MovieChoiceHolder.raw_list!![0]
        MovieChoiceHolder.setChoice(movie)
        furhat.say("Allright! The first one is " + movie.name)

        goto(QuestionAboutMovie)
    }
    onResponse<ChooseLast> {
        var last = MovieChoiceHolder.raw_list!!.size - 1
        var movie = MovieChoiceHolder.raw_list!![last]
        MovieChoiceHolder.setChoice(movie)
        furhat.say("Allright! The last one is " + movie.name)

        goto(QuestionAboutMovie)
    }

    onResponse<Number> {
        var answer = it.intent.value
        if (answer != null) {
            var size = MovieChoiceHolder.raw_list!!.size
            if ((answer <= size) && (answer > 0)) {
                var movie = MovieChoiceHolder.raw_list!![answer-1]    
                MovieChoiceHolder.setChoice(movie)
                furhat.say("Allright! Number " + answer + " is " + movie.name)

                goto(QuestionAboutMovie)
            } else {
                reentry()
            }
        }
    }
}

val QuestionAboutMovie = state(Interaction) {
    onEntry {
        if (MovieChoiceHolder.chosen_movie!!.seen) {
            random(
                { furhat.say("What do you think about " + MovieChoiceHolder.chosen_movie!!.name + " ?") },
                { furhat.say("The ${MovieChoiceHolder.chosen_movie!!.year} movie by  ${MovieChoiceHolder.chosen_movie!!.directors[0]}? What did you think about it?") },
                { furhat.say("Hmmm... The one with ${MovieChoiceHolder.chosen_movie!!.cast[0]}? What was your opinion?") }
            )
        } else {
            random(
                { furhat.say("I have not seen " + MovieChoiceHolder.chosen_movie!!.name + ", what do you think about it?") },
                { furhat.say("I have never heard of "+ MovieChoiceHolder.chosen_movie!!.name + ", what was it like?") },
                { furhat.say("I have never heard of "+ MovieChoiceHolder.chosen_movie!!.name + " but Hey! I'm just a professional movie critic... How was it?") }
            )
        }
        outSocket.send("repeat_review")
        message_in = inSocket.recvStr()
        furhat.say(message_in) //just repeat
        goto(ReviewSentiment)
    }
}

val ReviewSentiment = state(Interaction) {
    onEntry {
        outSocket.send("sentiment")
        message_in = inSocket.recvStr()
        // furhat.say(message_in) //just sentiment

        random(
            { furhat.say("After comparing againsts thousands of IMDB users, I think you consider the " + MovieChoiceHolder.chosen_movie!!.name + " movie to be " + message_in) },
            { furhat.say("So you think "+ MovieChoiceHolder.chosen_movie!!.name + " is " + message_in)}
        )
        if (MovieChoiceHolder.chosen_movie!!.seen) {
            goto(MyOpinion)
        } else {
            
            random(
                { furhat.say("I didn't see this movie, but I heard that the book was better...") },
                { furhat.say("I might have seen it, but it didn't leave any impression...")},
                { furhat.say("I don't have anything to say about " + MovieChoiceHolder.chosen_movie!!.name)}
            )

            goto(FinishOrStart)
        }

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
        furhat.say("Okay, that's a shame.")
        goto(FinishOrStart)
    }
    onResponse {
        outSocket.send("opinion" + "|" + MovieChoiceHolder.chosen_movie!!.id)
        message_in = inSocket.recvStr()
        furhat.say(message_in) //just repeat
        goto(FinishOrStart)
    }

}

val FinishOrStart = state(Interaction) {
    onEntry {
        random(
            { furhat.ask("Would you like to talk about another movie?") },
            { furhat.ask("Do you wish to continue talking?")}
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

