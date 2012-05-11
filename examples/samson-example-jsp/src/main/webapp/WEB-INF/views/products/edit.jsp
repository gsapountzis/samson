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
    <script type="text/javascript" src="<c:url value="/resources/js/jquery-1.7.2.js" />"></script>
  </head>

  <body>

    <div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="<c:url value="/" />">Samson JSP</a>
        </div>
      </div>
    </div>

    <div class="container">

      <form class="well form-horizontal" action="<c:url value="/products/${ id }" />" method="post">
        <fieldset>

          <c:set var="field" value="${ sms:path(productForm, 'code') }" />

          <div class="control-group <c:if test="${ field.error }">error</c:if>" >
            <label class="control-label" for="code">Code</label>
            <div class="controls">
              <input id="code" name="product.code" type="text" value="${fn:escapeXml( field.value )}" />
              <span class="help-inline"><c:out value="${ sms:messages(field) }" /></span>
            </div>
          </div>

          <c:set var="field" value="${ sms:path(productForm, 'name') }" />

          <div class="control-group <c:if test="${ field.error }">error</c:if>" >
            <label class="control-label" for="name">Name</label>
            <div class="controls">
              <input id="name" name="product.name" type="text" value="${fn:escapeXml( field.value )}" />
              <span class="help-inline"><c:out value="${ sms:messages(field) }" /></span>
            </div>
          </div>

          <c:set var="field" value="${ sms:path(productForm, 'price') }" />

          <div class="control-group <c:if test="${ field.error }">error</c:if>" >
            <label class="control-label" for="price">Price</label>
            <div class="controls">
              <input id="price" name="product.price" type="text" value="${fn:escapeXml( field.value )}" />
              <span class="help-inline"><c:out value="${ sms:messages(field) }" /></span>
            </div>
          </div>

        </fieldset>

        <div class="form-actions">
          <input type="submit" class="btn btn-primary" value="Update">&nbsp;
          <a href="<c:url value="/products/${ id }" />" class="btn">Cancel</a>
        </div>
      </form>

    </div>

  </body>
</html>
