package furhatos.app.moviecritic.nlu

import furhatos.nlu.EnumEntity
import furhatos.nlu.ComplexEnumEntity
import furhatos.nlu.EnumItem
import furhatos.nlu.Intent
import furhatos.util.Language
import furhatos.nlu.TextBuilder
import com.google.gson.Gson

class AnswerOption : EnumEntity {

    var id : String = ""

    // Every entity and intent needs an empty constructor.
    constructor() {
    }

    // Since we are overwriting the value, we need to use this custom constructor
    constructor(id : String, value : String) {
        this.id = id
        this.value = value
    }

    //override fun getEnumItems(lang: Language): List<EnumItem> {
    //    return QuestionSet.current.options;
    //}

}

class RequestRepeatOptions : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
                "what are the options",
                "can you repeat the options",
                "what were the options",
                "what are the movies",
                "can you repeat the movies",
                "what were the movies"
        )
    }
}

class DontKnow : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
                "I don't know",
                "don't know",
                "no idea",
                "I have no idea"
        )
    }
}

data class MovieData (
        var id: String,
        var name: String
)
class MovieList: ArrayList<MovieData>()

object MovieChoiceHolder {

    var movie_list: MovieChoice? = null

    init {
        /// ???
    }

    fun setValues(response:String) {
        movie_list = MovieChoice(Gson().fromJson(response, MovieList::class.java))
        MovieOption().forget()
    }
}

class MovieChoice(val alternatives: MovieList) {
    var options : MutableList<EnumItem> = mutableListOf()

    init {
        alternatives.forEach {
            options.add(EnumItem(MovieOption(it.id, it.name), it.name))
        }
    }

    //Returns the well formatted answer options
    fun getOptionsString() : String {
        var text = TextBuilder()
        text.appendList(options.map { it.wordString }, "or")
        return text.toString()
    }

    //Returns the well formatted answer options
    val speechPhrases : List<String>
        get() = options.map { it.wordString ?: "" }
}

class MovieOption : EnumEntity {

    var id : String = "xxx"

    // Every entity and intent needs an empty constructor.
    constructor() {
    }

    // Since we are overwriting the value, we need to use this custom constructor
    constructor(id : String, name : String) {
        this.id = id
        this.value = name
    }

    override fun getEnumItems(lang: Language): List<EnumItem> {
        return MovieChoiceHolder.movie_list!!.options;
    }
}

// Intent with examples fetched from an API. Note that the entity needs to be nullable
//class ChoiceIntent(film: MovieChoice? = null) : Intent() {
//    override fun getExamples(lang: Language): List<String> {
//        return listOf("@film",
//                "@film, please.",
//                "I mean @film",
//                "I meant @film",
//                "I said @film")
//    }
//}

