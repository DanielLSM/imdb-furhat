package furhatos.app.moviecritic.flow

import furhatos.app.moviecritic.gameState
import furhatos.app.moviecritic.nlu.CritiqueMovie
import furhatos.app.moviecritic.nlu.PlayGame
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.*
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state

val PreStart : State = state {
    onEntry {
        random(
            { furhat.ask("Hi! Do you want to play a game or discuss a movie?") },
            { furhat.ask("What would you like to do? Discuss a movie, or play a game?") },
            { furhat.ask("Hello! Do you want to play a game, or to talk about a movie?") }
        )
    }

    onResponse<PlayGame> {
        gameState.reset()
        furhat.say("Great! Let the fun begin.")
        furhat.say("Here comes the first summary.")
        goto(AwaitingGuess)
    }

    onResponse<CritiqueMovie> {
        goto(CritiqueStart)
    }
}