import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import edu.stanford.nlp.pipeline.CoreDocument
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import kotlin.io.path.Path
import kotlin.system.measureTimeMillis

val infoRecordExtractor = InfoRecordExtractor()
val questionPoolExtractor = QuestionPoolExtractor()
val questionGenerator = QuestionGenerator()

fun main(args: Array<String>) {
    if (args.isEmpty()) evaluate() else Answer2Question().main(args)
}

class Answer2Question: CliktCommand() {
    private val lang by argument("Language", "Language of the given text (currently only 'en')")
    private val filePath by argument("File path", "The path of the file with answers you want to convert in questions")

    override fun run() {
        try {
            val sentences = Files.readString(Path(filePath))
            echo("Analyzing text...")
            val doc = CoreDocument(sentences)
            pipeline.annotate(doc)
            doc.sentences().forEach { sentence ->
                val infoRecord = infoRecordExtractor.extractInfoRecord(sentence)
                val questionPool = questionPoolExtractor.extractQuestionPool(infoRecord)
                val questions = questionGenerator.generateQuestions(questionPool)
                echo(questions.displayQuestions(sentence.text()))
            }
        } catch (_: NoSuchFileException) {
            echo("The file \"$filePath\" doesn't exist.")
        }
    }
}

fun evaluate() {
    val requiredTime = measureTimeMillis {
        val document = CoreDocument(text)
        pipeline.annotate(document)
        document.sentences().forEach { sentence ->
            val infoRecord = infoRecordExtractor.extractInfoRecord(sentence)
            val questionPool = questionPoolExtractor.extractQuestionPool(infoRecord)
            val questions = questionGenerator.generateQuestions(questionPool)
//        println("${infoRecord.key.getString()} ;; ${infoRecord.verb.getString()} :: ${infoRecord.value.getString()}")
//        println("${questionPool.questionWord} ;; ${questionPool.infoRecord.sentence}")
            println(questions.displayQuestions(sentence.text()))
        }
    }
    println("Required time: $requiredTime ms")
}

fun List<String>.displayQuestions(sentence: String): String {
    val builder = StringBuilder("-".repeat(32) + " $sentence " + "-".repeat(32))
    forEach { builder.append("\n$it -> $sentence") }
    return builder.toString()
}