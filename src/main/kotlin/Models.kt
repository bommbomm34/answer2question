import edu.stanford.nlp.pipeline.StanfordCoreNLP
import java.util.Properties
val props = Properties().apply {
    setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,ner")
}

val pipeline = StanfordCoreNLP(props)