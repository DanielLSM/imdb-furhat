package furhatos.app.moviecritic

import furhatos.app.moviecritic.flow.Idle
import furhatos.skills.Skill
import furhatos.flow.kotlin.*

class MovieCriticSkill : Skill() {
    override fun start() {
        Flow().run(Idle)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
