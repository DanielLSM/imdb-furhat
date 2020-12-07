package furhatos.app.moviecritic.flow

import furhatos.app.moviecritic.gameState
import furhatos.app.moviecritic.movies
import furhatos.app.moviecritic.nlu.AskForStoryline
import furhatos.app.moviecritic.nlu.AskForYear
import furhatos.app.moviecritic.nlu.Guess
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.users

val AwaitingGuess = state(Interaction) {
    onEntry {
        if (!gameState.currentAnswer.didFurhatAsk) {
            furhat.say(gameState.currentMovie!!.summary)
            gameState.currentAnswer.didFurhatAsk = true
            furhat.say("Name the title")
        }
        furhat.listen()
    }

//    onTime(delay = 20_000) {
//        furhat.ask("If you would like a hint, you can always ask for the year or storyline")
//    }

    onResponse<AskForYear> {
        gameState.currentAnswer.didAskForYear = true
        furhat.ask("The movie is from the year ${gameState.currentMovie!!.year}")
    }

    onResponse<AskForStoryline> {
        gameState.currentAnswer.didAskForStoryline = true
        furhat.say("Storyline.")
        furhat.ask(gameState.currentMovie!!.storyline)
    }

    onResponse<Guess> {
        val guessedTitle = it.intent.title ?: ""
        val answer = gameState.takeGuess("$guessedTitle")
        val correctStr = if (answer.correct!!) "correct" else "incorrect"
        furhat.say("$guessedTitle. That is $correctStr. You earned ${answer.score} points!")
        if (gameState.completedAnswers.size > 1) {
            furhat.say("In total, you have ${gameState.totalScore} points from ${gameState.completedAnswers.size} questions.")
        }
        if (!gameState.isFinished()) {
            furhat.say("Here comes the next summary")
            reentry()
        } else {
            furhat.say("This concludes the game. I hope you enjoyed it.")
            goto(PreStart)
        }
    }
}