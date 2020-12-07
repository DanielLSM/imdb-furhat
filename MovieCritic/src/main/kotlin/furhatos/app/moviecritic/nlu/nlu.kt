package furhatos.app.moviecritic.nlu

import furhatos.app.moviecritic.movies
import furhatos.nlu.EnumEntity
import furhatos.nlu.Intent
import furhatos.util.Language

class PlayGame : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Let's play a game",
            "I want to play review quiz",
            "I would like to play a game",
            "Start a new game"
        )
    }
}

class CritiqueMovie : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "I want to critique a movie",
            "Let's critique a movie",
            "I would like to hear your opinion on a movie"
        )
    }
}

class AskForYear: Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "I would like to know the year",
            "What year is it from?",
            "Which year is it from?",
            "Tell me the year"
        )
    }
}

class AskForStoryline: Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "I would like to know the storyline",
            "What is the story line?",
            "What is the story line?",
            "Tell me the story line"
        )
    }
}

class MovieTitle: EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return movies.map { it.title }
    }
}

class Guess(var title: MovieTitle? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "@title",
            "My guess is @title",
            "The title is @title",
            "The movie is @title",
            "The movie is called @title"
        )
    }
}