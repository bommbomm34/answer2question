const val text = """
Write here the text you want to generate questions of
"""

val PUNCTS = setOf(",", ".", ";", ":", "!", "?")
val LOCATION_TYPES = setOf("LOCATION", "CITY", "COUNTRY")
val DATE_TYPES = setOf("DATE")
val ORGANIZATION_TYPES = setOf("ORGANIZATION")
val TIME_TYPES = setOf("TIME")
val PERSON_TYPES = setOf("PERSON")
val DAYS = setOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
val PERSON_EXCEPTIONS = setOf("I", "you", "he", "she", "they", "we")
val PLURAL_PERSONAL_PRONOUNS = setOf("we", "they", "you")
val ENHANCED_HOW_LAST_TYPES = setOf(Regex("JJ+"))
val TO_BE_FORMS = setOf("am", "is", "are")
val AUX_VERBS = setOf("have", "has")
val ALLOWED_NEIGHBOR_ADVERBS = setOf("as", "to")