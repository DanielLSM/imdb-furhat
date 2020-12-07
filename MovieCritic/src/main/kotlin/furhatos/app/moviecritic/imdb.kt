package furhatos.app.moviecritic

val movies = listOf(
    Movie(
        "Happiest Season",
        year = 2020,
        summary = "A holiday romantic comedy that captures the range of emotions tied to wanting your family's acceptance, being true to yourself, and trying not to ruin Christmas.",
        storyline = "Meeting your girlfriend's family for the first time can be tough. Planning to propose at her family's annual Christmas dinner - until you realize that they don't even know she's gay - is even harder. When Abby (Kristen Stewart) learns that Harper (Mackenzie Davis) has kept their relationship a secret from her family, she begins to question the girlfriend she thought she knew."
    ),
    Movie(
        "Hillbilly Elegy",
        year = 2020,
        summary = "An urgent phone call pulls a Yale Law student back to his Ohio hometown, where he reflects on three generations of family history and his own future.",
        storyline = "Based on the bestselling memoir by J.D. Vance, HILLBILLY ELEGY is a modern exploration of the American Dream and three generations of an Appalachian family as told by its youngest member, a Yale Law student forced to return to his hometown"
    ),
    Movie(
        "Chaos Walking",
        year = 2021,
        summary = "A dystopian world where there are no women and all living creatures can hear each others' thoughts in a stream of images, words, and sounds called Noise.",
        storyline = "In the not too distant future, Todd Hewitt (Tom Holland) discovers Viola (Daisy Ridley), a mysterious girl who crash lands on his planet, where all the women have disappeared and the men are afflicted by \"the Noise\" - a force that puts all their thoughts on display. In this dangerous landscape, Viola's life is threatened - and as Todd vows to protect her, he will have to discover his own inner power and unlock the planet's dark secrets. From the director of The Bourne Identity and Edge of Tomorrow and based on the best-selling novel The Knife of Never Letting Go, Daisy Ridley and Tom Holland star with Mads Mikkelsen, Demián Bichir, Cynthia Erivo, Nick Jonas, Kurt Sutter, and David Oyelowo in Chaos Walking."
    ),
    Movie(
        "Harry Potter and the Sorcerer's Stone",
        year = 2001,
        summary = "An orphaned boy enrolls in a school of wizardry, where he learns the truth about himself, his family and the terrible evil that haunts the magical world.",
        storyline = "This is the tale of Harry Potter (Daniel Radcliffe), an ordinary eleven-year-old boy serving as a sort of slave for his aunt and uncle who learns that he is actually a wizard and has been invited to attend the Hogwarts School for Witchcraft and Wizardry. Harry is snatched away from his mundane existence by Rubeus Hagrid (Robbie Coltrane), the groundskeeper for Hogwarts, and quickly thrown into a world completely foreign to both him and the viewer. Famous for an incident that happened at his birth, Harry makes friends easily at his new school. He soon finds, however, that the wizarding world is far more dangerous for him than he would have imagined, and he quickly learns that not all wizards are ones to be trusted"
    ),
    Movie(
        "Inception",
        year = 2010,
        summary = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
        storyline = "Dom Cobb is a skilled thief, the absolute best in the dangerous art of extraction, stealing valuable secrets from deep within the subconscious during the dream state, when the mind is at its most vulnerable. Cobb's rare ability has made him a coveted player in this treacherous new world of corporate espionage, but it has also made him an international fugitive and cost him everything he has ever loved. Now Cobb is being offered a chance at redemption. One last job could give him his life back but only if he can accomplish the impossible, inception. Instead of the perfect heist, Cobb and his team of specialists have to pull off the reverse: their task is not to steal an idea, but to plant one. If they succeed, it could be the perfect crime. But no amount of careful planning or expertise can prepare the team for the dangerous enemy that seems to predict their every move. An enemy that only Cobb could have seen coming."
    ),
    Movie(
        "The Shawshank Redemption",
        year = 1994,
        summary = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
        storyline = "Chronicles the experiences of a formerly successful banker as a prisoner in the gloomy jailhouse of Shawshank after being found guilty of a crime he did not commit. The film portrays the man's unique way of dealing with his new, torturous life; along the way he befriends a number of fellow prisoners, most notably a wise long-term inmate named Red."
    ),
    Movie(
        "The Godfather",
        year = 1972,
        summary = "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
        storyline = "The Godfather \"Don\" Vito Corleone is the head of the Corleone mafia family in New York. He is at the event of his daughter's wedding. Michael, Vito's youngest son and a decorated WW II Marine is also present at the wedding. Michael seems to be uninterested in being a part of the family business. Vito is a powerful man, and is kind to all those who give him respect but is ruthless against those who do not. But when a powerful and treacherous rival wants to sell drugs and needs the Don's influence for the same, Vito refuses to do it. What follows is a clash between Vito's fading old values and the new ways which may cause Michael to do the thing he was most reluctant in doing and wage a mob war against all the other mafia families which could tear the Corleone family apart."
    )
)

class Movie(val title: String, val year: Int, val summary: String, val storyline: String) {
    fun censoredSummary(): String {
        return summary.replace(oldValue = title, newValue = "Censored", ignoreCase = true)
    }
    fun censoredStoryline(): String {
        return storyline.replace(oldValue = title, newValue = "Censored", ignoreCase = true)
    }
}
