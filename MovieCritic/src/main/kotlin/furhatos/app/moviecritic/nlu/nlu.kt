package furhatos.app.moviecritic.nlu

import furhatos.app.moviecritic.movies
import furhatos.nlu.EnumEntity
import furhatos.nlu.Intent
import furhatos.util.Language
import furhatos.nlu.EnumItem
import furhatos.nlu.TextBuilder
import com.google.gson.Gson

class PlayGame : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Let's play a game",
            "I want to play review quiz",
            "I want to play a game",
            "I would like to play a game",
            "Start a new game",
            "Play a game",
            "Play"
        )
    }
}

class CritiqueMovie : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Talk about a movie",
            "Discuss a movie",
            "I want to discuss a movie",
            "I want to talk about a movie",
            "I would like to discuss a movie",
            "I would like to talk about a movie",
            "Let's talk about a movie",
            "Let's discuss a movie",
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

data class MovieData (
        var id: String,
        var name: String,
        var year: String,
        var rating: String,
        var directors: List<String>,
        var cast: List<String>,
        var seen: Boolean
)
class MovieList: ArrayList<MovieData>()

object MovieChoiceHolder {

    var movie_list: MovieChoice? = null
    var raw_list: MovieList? = null
    var chosen_movie: MovieData? = null

    init {
    }

    fun setValues(response:String) {
        raw_list = Gson().fromJson(response, MovieList::class.java)
        movie_list = MovieChoice(raw_list!!)
        MovieOption().forget()
    }

    fun setChoice(movie:MovieData?) {
        chosen_movie = movie
    }

    fun resetChoice() {
        chosen_movie = null
    }
}

class MovieChoice(val alternatives: MovieList) {
    var options : MutableList<EnumItem> = mutableListOf()

    init {
        alternatives.forEach {
            options.add(EnumItem(MovieOption(it.id, it.name, it), it.name))
        }
    }

    fun getOptionsString() : String {
        var text = TextBuilder()
        text.appendList(options.map { it.wordString }, "or")
        return text.toString()
    }

    val speechPhrases : List<String>
        get() = options.map { it.wordString ?: "" }
}

class MovieOption : EnumEntity {

    var id : String = "xxx"
    var movie_data : MovieData? = null

    constructor() {
    }

    // Since we are overwriting the value, we need to use this custom constructor
    constructor(id : String, name : String, movie_data : MovieData) {
        this.id = id
        this.value = name
        this.movie_data = movie_data
    }

    override fun getEnumItems(lang: Language): List<EnumItem> {
        return MovieChoiceHolder.movie_list!!.options;
    }
}

// Intent with examples fetched from an API. Note that the entity needs to be nullable
class ChooseFirst() : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("The first one", "The first",
                "Number one")
    }
}

class ChooseLast() : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("The last one",
                "The last")
    }
}

