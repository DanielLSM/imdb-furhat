package furhatos.app.moviecritic

import furhatos.records.User

class ReviewGameState(val rounds: Int) {
    var currentMovie: Movie? = null
    var currentAnswer: Answer = Answer()
    var completedAnswers: MutableList<Answer> = ArrayList()

    val totalScore: Int
        get() {
            return completedAnswers.map { it.score!! }.sum()
        }

    fun takeGuess(title: String): Answer {
        val correct = currentMovie!!.title.equals(title, ignoreCase = true)
        val answer = currentAnswer
        answer.correct = correct
        completedAnswers.add(answer)
        if (!isFinished()) {
            currentAnswer = Answer()
            currentMovie = null
        }
        return answer
    }

    fun isFinished(): Boolean {
        return completedAnswers.size >= rounds
    }
}

class Answer {
    var didFurhatAsk = false
    var didAskForStoryline = false
    var didAskForYear = false
    var correct: Boolean? = null

    val score: Int?
        get() {
            return if (correct == null) {
                null
            } else if (!correct!!) {
                0
            } else {
                var score = 10
                if (didAskForStoryline) {
                    score -= 3
                }
                if (didAskForYear) {
                    score -= 2
                }
                score
            }
        }

    override fun toString(): String {
        return "Answer(didFurhatAsk=$didFurhatAsk, didAskForStoryline=$didAskForStoryline, didAskForYear=$didAskForYear, correct=$correct)"
    }
}

val User.gameState: ReviewGameState
    get() = data.getOrPut(ReviewGameState::class.qualifiedName, ReviewGameState(rounds = 5))