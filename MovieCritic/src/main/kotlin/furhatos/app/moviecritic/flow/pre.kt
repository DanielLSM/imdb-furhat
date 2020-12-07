package furhatos.app.moviecritic.flow

import furhatos.app.moviecritic.nlu.CritiqueMovie
import furhatos.app.moviecritic.nlu.PlayGame
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state

val PreStart = state(Interaction) {
    onEntry {
        random(
            { furhat.say("Hi there") },
            { furhat.say("Oh, hello there") }
        )
        random(
            { furhat.ask("How can I help you?") },
            { furhat.ask("What would you like to do?") }
        )
    }

    onResponse<PlayGame> {
        furhat.say("Great! Let the fun begin.")
        furhat.say("Here comes the first summary.")
        goto(AwaitingGuess)
    }

    onResponse<CritiqueMovie> {
        goto(Start)
    }
}