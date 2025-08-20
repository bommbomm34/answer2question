import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.ling.Label
import edu.stanford.nlp.trees.Tree

class QuestionGenerator {
    fun generateQuestions(questionPool: QuestionPool): List<String> {
        val questions = mutableListOf<String>()
        questions.add(generateYesNoQuestion(questionPool))
        questions.add(generateQuestion(questionPool))
        return questions
    }

    private fun generateYesNoQuestion(questionPool: QuestionPool): String {
        val leaves = questionPool.verb.getLeaves<Tree>()
        val keyLeaves = questionPool.infoRecord.key.getLeaves<Tree>()
        val lowercasedKey = keyLeaves.joinToString(" ") {
            it.label().lowercase()
        }
        val auxVerb = leaves.firstOrNull { it.label().isAUX() }?.getString() ?: (if (keyLeaves.last().label().tag()
                .endsWith("S")
        ) "do" else "does")
        val obj = questionPool.infoRecord.value.getString()
        return if (TO_BE_FORMS.contains(questionPool.verb.getString())) {
            "${
                questionPool.verb.getString().firstUpperCase()
            } $lowercasedKey $obj?"
        } else {
            "${auxVerb.firstUpperCase()} $lowercasedKey ${(leaves.filter { it.getString() != auxVerb }).removeS(auxVerb == "does")} $obj?"
        }
    }

    private fun generateQuestion(questionPool: QuestionPool): String {
        val leaves = questionPool.verb.getLeaves<Tree>()
        val questionWord = questionPool.questionWord
        val keyLeaves = if (questionPool.askForValue) questionPool.infoRecord.key.getLeaves<Tree>() else
            questionPool.infoRecord.value.getLeaves()
        val lowercasedKey = keyLeaves.joinToString(" ") { it.label().lowercase() }
        val auxVerb = leaves.firstOrNull { it.label().isAUX() }?.getString() ?: (if (keyLeaves.last().label().tag()
                .endsWith("S")
        ) "do" else "does")
        val mainVerbFiltered = (leaves.filter { it.getString() != auxVerb }).removeS(auxVerb == "does")
        return when (questionWord.text) {
            "Whose" -> {
                if (TO_BE_FORMS.contains(questionPool.verb.getString().thirdPerson())) "${questionWord.text} ${questionWord.customSubject} ${questionPool.verb.getString()} $lowercasedKey?"
                else "${questionWord.text} ${questionWord.customSubject} ${auxVerb.thirdPerson()} $mainVerbFiltered $lowercasedKey?"
            }

            "Why" -> {
                if (TO_BE_FORMS.contains(questionPool.verb.getString())) "${questionWord.text} ${questionPool.verb.getString().thirdPerson()} $lowercasedKey ${questionWord.customSubject}?"
                else "${questionWord.text} ${auxVerb.thirdPerson()} $lowercasedKey $mainVerbFiltered ${questionWord.customSubject}?"
            }

            else -> {
                if (TO_BE_FORMS.contains(questionPool.verb.getString())) "${questionWord.text} ${questionPool.verb.getString().thirdPerson()} $lowercasedKey?"
                else "${questionWord.text} ${auxVerb.thirdPerson()} $lowercasedKey $mainVerbFiltered?"
            }
        }
    }
}

fun String.firstUpperCase(): String {
    return this.replaceFirstChar { it.uppercase() }
}

fun Label.isAUX(): Boolean {
    val label = this as CoreLabel
    return AUX_VERBS.contains(label.word()) || label.tag() == "MD"
}

fun Label.lowercase(): String {
    val label = this as CoreLabel
    val isNamed = PERSON_TYPES.contains(label.ner()) || ORGANIZATION_TYPES.contains(label.ner()) ||
        LOCATION_TYPES.contains(label.ner()) || DAYS.contains(label.word())
    return if (isNamed || label.word() == "I") label.word() else label.word().lowercase()
}

fun Label.tag(): String {
    val label = this as CoreLabel
    return label.tag()
}

fun String.thirdPerson(): String { // Returns third-person singular of a verb (only for aux verbs)
    return if (TO_BE_FORMS.contains(this)) "is" else this
}

fun Collection<Tree>.removeS(hasAUXVerbS: Boolean): String {
    if (hasAUXVerbS) {
        val cleaned = this.map { if (it.label().tag() == "VBZ") it.getString().removeSuffix("s") else it.getString() }
        return cleaned.joinToString(" ")
    } else {
        return getString()
    }
}