import edu.stanford.nlp.ling.StringLabel
import edu.stanford.nlp.ling.Word
import edu.stanford.nlp.pipeline.CoreSentence
import edu.stanford.nlp.trees.LabeledScoredTreeFactory
import edu.stanford.nlp.trees.Tree


data class InfoRecord(
    val key: Tree,
    val value: Tree,
    val verb: Tree,
    val tree: Tree,
    val lemmaMap: List<String>,
    val sentence: CoreSentence
)

class InfoRecordExtractor {
    fun extractInfoRecord(sentence: CoreSentence): InfoRecord {
        val tokens = sentence.tokens()
        val lemmas = tokens.map { it.lemma() }
        val tree = sentence.constituencyParse()
        val vp = findFirstVP(tree)
        val subject = tree.getChild(0).children().first { it.label().value() == "NP" }
        val fullVP = tree.getChild(0).first { it.value() == "VP" }
        val obj = getObject(fullVP, vp)
        return InfoRecord(subject, obj, vp, tree, lemmas, sentence)
    }

    private fun getObject(vp: Tree, verb: Tree): Tree {
        val trees = vp.getLeaves<Tree>()
        val obj = trees
            .dropWhile { it.getString() != verb.getString().substringAfterLast(" ") }
            .drop(1)
        return obj.toTree("OBJECT")
    }

    private fun findFirstVP(root: Tree): Tree {
        val sentence = root.children().first { it.value() == "S" }
        val sentenceChild = sentence.children().first { it.value() == "VP" }

        val vp = filterVP(sentenceChild) ?: sentenceChild.deepCopy()

        val vpLeaves = vp.getLeaves<Tree>()
        if (vpLeaves.isEmpty()) return vp.deepCopy()
        val sentenceLeaves = sentence.getLeaves<Tree>()

        var lastSuccessfulScannedWord = vpLeaves.first()
        val finalVPLeaves = mutableListOf<Tree>(lastSuccessfulScannedWord)

        for (i in 1 until vpLeaves.size) {
            val child = vpLeaves[i]

            val idxChild = sentenceLeaves.indexOf(child)
            val idxLast = sentenceLeaves.indexOf(lastSuccessfulScannedWord)
            if (idxChild != -1 && idxLast != -1 && idxChild - idxLast == 1) {
                lastSuccessfulScannedWord = child
                finalVPLeaves.add(child)
            } else {
                break
            }
        }

        val finalTree = vp.deepCopy()
        finalTree.setChildren(finalVPLeaves.map { it.deepCopy() })

        return finalTree
    }


    private fun filterVP(vp: Tree): Tree? {
        if (vp.isLeaf) return null
        val treeList = vp.children().mapNotNull {
            if ((it.value().startsWith("V") || it.value() == "MD" || ALLOWED_NEIGHBOR_ADVERBS.contains(it.getString()))
                && it.value() != "VP") {
                it
            } else {
                filterVP(it)
            }
        }
        if (treeList.isEmpty()) return null
        val deepCopy = vp.deepCopy()
        deepCopy.setChildren(treeList)
        return deepCopy
    }


}

fun Collection<Tree>.toTree(label: String = "-"): Tree {
    val tf = LabeledScoredTreeFactory()
    val children = this.toList()
    return if (children.isEmpty()) {
        tf.newLeaf(label)
    } else {
        tf.newTreeNode(StringLabel(label), children)
    }
}

fun List<Word>.combineTokens(): String {
    if (this.isEmpty()) return ""

    val builder = StringBuilder()
    forEachIndexed { index, text ->
        if (index > 0 && !PUNCTS.contains(text.word())) {
            builder.append(" ")
        }
        builder.append(text)
    }
    return builder.toString()
}

fun Tree.getString(): String {
    return this.yieldWords().combineTokens()
}

fun Collection<Tree>.getString(): String {
    return this.toTree().getString()
}