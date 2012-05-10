package samson.example.scalate.views

import samson.form.FormNode;

import scala.collection.JavaConverters._

object Functions {

  def block[A](a: A)(body: A => Unit) = body(a)

  def block2[A, B](a: A, b: B)(body: (A, B) => Unit) = body(a, b)

  def block3[A, B, C](a: A, b: B, c: C)(body: (A, B, C) => Unit) = body(a, b, c)

  def block4[A, B, C, D](a: A, b: B, c: C, d: D)(body: (A, B, C, D) => Unit) = body(a, b, c, d)

  def isNullOrEmpty(s: String) = (s == null || s.isEmpty)

  def infos(node: FormNode) = {
    val conversionInfo = List(node.getConversionInfo);
    val validationInfos = node.getValidationInfos.asScala.toList;
    val infos = node.getInfos.asScala.toList;

    (conversionInfo ++ validationInfos ++ infos).filter(!isNullOrEmpty(_)).mkString(", ")
  }

  def errors(node: FormNode) = {
    val conversionError = Option(node.getConversionError);
    val validationErrors = node.getValidationErrors.asScala.toList;
    val errors = node.getErrors.asScala.toList;

    conversionError.getOrElse((validationErrors ++ errors).filter(!isNullOrEmpty(_)).mkString(", "))
  }

  def messages(node: FormNode) = errors(node);

  def multiError(nodes: FormNode*) = nodes.map(_.isError).reduce(_ || _)

  def multiMessages(nodes: FormNode*) = nodes.map(messages(_)).filter(!isNullOrEmpty(_)).mkString(", ")

}