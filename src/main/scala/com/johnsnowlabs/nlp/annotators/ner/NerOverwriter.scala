package com.johnsnowlabs.nlp.annotators.ner

import java.util.Locale

import com.johnsnowlabs.nlp.annotators.common.SentenceSplit
import com.johnsnowlabs.nlp.{Annotation, AnnotatorModel, ParamsAndFeaturesReadable}
import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.ml.param.{BooleanParam, Param, ParamValidators, StringArrayParam}
import org.apache.spark.ml.util.Identifiable


class NerOverwriter(override val uid: String) extends AnnotatorModel[NerOverwriter] {

  import com.johnsnowlabs.nlp.AnnotatorType.NAMED_ENTITY

  override val outputAnnotatorType: AnnotatorType = NAMED_ENTITY

  override val inputAnnotatorTypes: Array[AnnotatorType] = Array(NAMED_ENTITY)

  def this() = this(Identifiable.randomUID("NER_OVERWRITER"))

  val stopWords: StringArrayParam =
    new StringArrayParam(this, "stopWords", "the words to be filtered out.")
  def setStopWords(value: Array[String]): this.type = set(stopWords, value)
  def getStopWords: Array[String] = $(stopWords)

  val newResult: Param[String] = new Param(this, "newResult", "New NER class to overwrite")
  def setNewResult(r: String): this.type = {set(newResult, r)}
  def getNewResult: String = $(newResult)

  setDefault(
    inputCols -> Array(NAMED_ENTITY),
    outputCol -> "fixed_rer",
    stopWords -> Array(),
    newResult -> "I-OVERWRITE"
  )

  override def annotate(annotations: Seq[Annotation]) : Seq[Annotation]= {

    var annotationsOverwritten = annotations

    annotationsOverwritten.map { tokenAnnotation =>
      val stopWordsSet = $(stopWords).toSet
      if (stopWordsSet.contains(tokenAnnotation.metadata("word"))) {
        Annotation(
          outputAnnotatorType,
          tokenAnnotation.begin,
          tokenAnnotation.end,
          $(newResult),
          tokenAnnotation.metadata
        )
      } else {
        Annotation(
          outputAnnotatorType,
          tokenAnnotation.begin,
          tokenAnnotation.end,
          tokenAnnotation.result,
          tokenAnnotation.metadata
        )
      }

    }

  }

}

object NerOverwriter extends ParamsAndFeaturesReadable[NerOverwriter]
