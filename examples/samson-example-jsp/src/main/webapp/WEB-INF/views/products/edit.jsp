<!DOCTYPE html>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="samson" prefix="sms"%>

<c:set var="id" value="${ it.id }" />
<c:set var="productForm" value="${ it.productForm }" />

<html>
  <head>
    <title>Samson JSP Example</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap.css" />" />
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/main.css" />" />
    <script type="text/javascript" src="<c:url value="/resources/js/jquery-1.6.2.js" />"></script>
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

          <c:set var="field" value="${ sms:path(productForm, 'product.code').field }" />

          <div class="clearfix <c:if test="${ field.error }">error</c:if>" >
            <label for="code">Code</label>
            <div class="input">
              <input class="span4" id="code" name="product.code" type="text" value="${fn:escapeXml( field.value )}" />
              <span class="help-inline"><c:out value="${ sms:messages(field) }" /></span>
            </div>
          </div>

          <c:set var="field" value="${ sms:path(productForm, 'product.name').field }" />

          <div class="clearfix <c:if test="${ field.error }">error</c:if>" >
            <label for="name">Name</label>
            <div class="input">
              <input class="span4" id="name" name="product.name" type="text" value="${fn:escapeXml( field.value )}" />
              <span class="help-inline"><c:out value="${ sms:messages(field) }" /></span>
            </div>
          </div>

          <c:set var="field" value="${ sms:path(productForm, 'product.price').field }" />

          <div class="clearfix <c:if test="${ field.error }">error</c:if>" >
            <label for="price">Price</label>
            <div class="input">
              <input class="span4" id="price" name="product.price" type="text" value="${fn:escapeXml( field.value )}" />
              <span class="help-inline"><c:out value="${ sms:messages(field) }" /></span>
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
