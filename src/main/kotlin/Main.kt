import edu.stanford.nlp.pipeline.CoreDocument

val startTime = System.nanoTime()
val infoRecordExtractor = InfoRecordExtractor()
val questionPoolExtractor = QuestionPoolExtractor()
val questionGenerator = QuestionGenerator()

fun main() {
    val document = CoreDocument(text)
    pipeline.annotate(document)
    document.sentences().forEach {
        val infoRecord = infoRecordExtractor.extractInfoRecord(it)
        val questionPool = questionPoolExtractor.extractQuestionPool(infoRecord)
        val questions = questionGenerator.generateQuestions(questionPool)
//        println("${infoRecord.key.getString()} ;; ${infoRecord.verb.getString()} :: ${infoRecord.value.getString()}")
//        println("${questionPool.questionWord} ;; ${questionPool.infoRecord.sentence}")
        println("-".repeat(32) + " ${it.text()} " + "-".repeat(32))
        questions.forEach { question -> println("$question -> $it") }
    }
    println("Required time: ${(System.nanoTime() - startTime) / 1000000L} ms")
}