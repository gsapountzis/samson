<!DOCTYPE html>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="id" value="${ it.id }" />
<c:set var="product" value="${ it.productForm.fields }" />

<html>
  <head>
    <title>Samson JSP Example</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap.min.css" />" />
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/main.css" />" />
    <script type="text/javascript" src="<c:url value="/resources/js/jquery-1.6.2.min.js" />"></script>
  </head>

  <body>

    <div class="topbar">
      <div class="fill">
        <div class="container">
          <a class="brand" href="<c:url value="/" />">Samson JSP</a>
        </div>
      </div>
    </div>

    <div class="container">

      <form action="<c:url value="/products/${ id }" />" method="post">
        <fieldset>

          <c:set var="field" value="${ product['code'] }" />

          <div class="clearfix <c:if test="${ field.error }">error</c:if>" >
            <label for="code">Code</label>
            <div class="input">
              <input class="span4" id="code" name="product.code" size="30" type="text" value="${fn:escapeXml( field.value )}" />
              <span class="help-inline"><c:out value="messages" /></span>
            </div>
          </div>

          <c:set var="field" value="${ product['name'] }" />

          <div class="clearfix <c:if test="${ field.error }">error</c:if>" >
            <label for="name">Name</label>
            <div class="input">
              <input class="span4" id="name" name="product.name" size="30" type="text" value="${fn:escapeXml( field.value )}" />
              <span class="help-inline"><c:out value="messages" /></span>
            </div>
          </div>

          <c:set var="field" value="${ product['price'] }" />

          <div class="clearfix <c:if test="${ field.error }">error</c:if>" >
            <label for="price">Price</label>
            <div class="input">
              <input class="span4" id="price" name="product.price" size="30" type="text" value="${fn:escapeXml( field.value )}" />
              <span class="help-inline"><c:out value="messages" /></span>
            </div>
          </div>

        </fieldset>

        <div class="actions">
          <input type="submit" class="btn primary" value="Update">&nbsp;
          <a href="<c:url value="/products/${ id }" />" class="btn">Cancel</a>
        </div>
      </form>

    </div>

  </body>
</html>
