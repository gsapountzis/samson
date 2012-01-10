package samson.example.scalate.views

import samson.JForm.Field

import scala.collection.JavaConverters._

object Functions {

  def block[A](a: A)(body: A => Unit) = body(a)

  def block2[A, B](a: A, b: B)(body: (A, B) => Unit) = body(a, b)

  def block3[A, B, C](a: A, b: B, c: C)(body: (A, B, C) => Unit) = body(a, b, c)

  def block4[A, B, C, D](a: A, b: B, c: C, d: D)(body: (A, B, C, D) => Unit) = body(a, b, c, d)

  def isNullOrEmpty(s: String) = (s == null || s.isEmpty)

  def infos(field: Field) = {
    val messages = field.getMessages()

    val conversionInfo = List(messages.getConversionInfo);
    val validationInfos = messages.getValidationInfos.asScala.toList;
    val infos = messages.getInfos.asScala.toList;

    (conversionInfo ++ validationInfos ++ infos).filter(!isNullOrEmpty(_)).mkString(", ")
  }

  def errors(field: Field) = {
    val messages = field.getMessages()

    val conversionError = Option(messages.getConversionError);
    val validationErrors = messages.getValidationErrors.asScala.toList;
    val errors = messages.getErrors.asScala.toList;

    conversionError.getOrElse((validationErrors ++ errors).filter(!isNullOrEmpty(_)).mkString(", "))
  }

  def messages(field: Field) = errors(field);

  def multiError(fields: Field*) = fields.map(_.isError).reduce(_ || _)

  def multiMessages(fields: Field*) = fields.map(messages(_)).filter(!isNullOrEmpty(_)).mkString(", ")

}