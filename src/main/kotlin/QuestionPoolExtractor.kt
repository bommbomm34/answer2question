import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.trees.Tree

data class QuestionWord(
    val text: String,
    val customSubject: String? = null
)

data class QuestionPool(
    val infoRecord: InfoRecord,
    val askForValue: Boolean,
    val questionWord: QuestionWord,
    val verb: Tree
)

class QuestionPoolExtractor {
    fun extractQuestionPool(infoRecord: InfoRecord, askForValue: Boolean = true): QuestionPool {
        val subject = if (askForValue) infoRecord.value else infoRecord.key
        val questionWord = determineQuestionWord(subject, infoRecord)
        return QuestionPool(infoRecord, askForValue, questionWord, infoRecord.verb)
    }

    private fun determineQuestionWord(tree: Tree, infoRecord: InfoRecord): QuestionWord {
        val tokens = tree.getLeaves<Tree>().map { it.label() as CoreLabel }
        val subject = tokens.filterIndexed { i, label ->
            val nextToken = tokens.getOrNull(i + 1)
            val prevToken = tokens.getOrNull(i - 1) // previous token
            (label.tag().startsWith("N") || label.tag() == "PRP" ||
                    ((prevToken?.tag() == "DT") && label.tag() == "VB")) &&
                    !(nextToken?.word()?.startsWith("'") ?: false)
        }.firstOrNull()
        val isName = subject?.isEntity(PERSON_TYPES) ?: false
        val isOrganization = subject?.isEntity(ORGANIZATION_TYPES) ?: false
        val isDate = subject?.isEntity(DATE_TYPES) ?: false
        val isLocation = subject?.isEntity(LOCATION_TYPES) ?: false
        val isTime = subject?.isEntity(TIME_TYPES) ?: false
        val isPersonException = PERSON_EXCEPTIONS.contains(subject?.lowercase())
        return if (isName || isOrganization || isPersonException) {
            QuestionWord("Who")
        } else if (isDate || isTime) {
            QuestionWord("When")
        } else if (isLocation) {
            QuestionWord("Where")
        } else if (subject == null && tokens.any { it.tag().startsWith("JJ") }) {
            QuestionWord("How")
        } else if (subject != null && ENHANCED_HOW_LAST_TYPES.any { tokens.last().tag().matches(it) }) {
            QuestionWord("How ${tokens.last().word()}")
        } else if (tokens.any { it.word() == "because" }) {
            QuestionWord(
                "Why",
                tokens.takeWhile { it.word() != "because" }.joinToString(" ") { it.word() })

        } else if (tokens.getOrNull(tokens.indexOf(subject) - 1)?.word() == "'s" &&
            !(setOf("have", "has").contains(infoRecord.verb.getString()))) {
            QuestionWord("Whose", subject?.word())
        } else {
            QuestionWord("What")
        }
    }


}

fun ner(token: CoreLabel): String {
    return if (PERSON_EXCEPTIONS.contains(token.word())) "PERSON" else token.ner()
}

fun CoreLabel.isEntity(entities: Set<String>): Boolean {
    return entities.contains(ner(this))
}