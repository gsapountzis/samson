
# Samson JAX-RS module

Utility module for handling form submissions and query parameters in JAX-RS resources:

* binder for
  [form](http://download.oracle.com/javaee/6/api/javax/ws/rs/FormParam.html) and
  [query](http://download.oracle.com/javaee/6/api/javax/ws/rs/QueryParam.html)
  parameters that supports composite types (beans, lists and maps).

* [entity provider](http://download.oracle.com/javaee/6/api/javax/ws/rs/ext/MessageBodyReader.html)
  for form beans.

* integration with [bean validation](http://beanvalidation.org).

* [facade](/gsapountzis/samson/blob/master/samson-core/src/main/java/samson/JForm.java)
  over the binding and validation results.

## Usage

Install the module and add the following dependency to your pom.xml:

	<dependency>
		<groupId>com.github.gsapountzis.samson</groupId>
		<artifactId>samson-jersey</artifactId>
		<version>0.3</version>
	</dependency>

The module has been tested on Jersey 1.10.

## Example

There are some simple examples [here](/gsapountzis/samson/tree/master/examples/).
You can also check the available
[unit](/gsapountzis/samson/tree/master/samson-core/src/test/java/samson/bind/) and
[integration](/gsapountzis/samson/tree/master/samson-jersey/src/test/java/samson/jersey/) tests.

## TODO

Check [here](/gsapountzis/samson/blob/master/TODO.md).

## Credit

This module was inspired by the [Xebia](http://blog.xebia.com/2011/04/20/posting-complex-forms-with-resteasy-part-2/)
extended form injector for RESTEasy and influenced by the [Play!](http://www.playframework.org/) framework.
