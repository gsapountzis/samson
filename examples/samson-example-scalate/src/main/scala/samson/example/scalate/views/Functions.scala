package samson.example.scalate.views

import samson.JForm.Field

import scala.collection.JavaConverters._

object Functions {

  def using[A](a: A)(body: A => Unit) = body(a)

  def using2[A, B](a: A, b: B)(body: (A, B) => Unit) = body(a, b)

  def using3[A, B, C](a: A, b: B, c: C)(body: (A, B, C) => Unit) = body(a, b, c)

  def using4[A, B, C, D](a: A, b: B, c: C, d: D)(body: (A, B, C, D) => Unit) = body(a, b, c, d)

  def messages(field: Field) = {
    val messages = field.getMessages()

    val conversionError = Option(messages.getConversionError);
    val validationErrors = messages.getValidationErrors.asScala.toList;
    val errors = messages.getErrors.asScala.toList;

    conversionError.getOrElse((validationErrors ++ errors).mkString(", "))
  }

}