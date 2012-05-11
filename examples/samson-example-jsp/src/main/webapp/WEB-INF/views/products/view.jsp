<!DOCTYPE html>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="id" value="${ it.id }" />
<c:set var="product" value="${ it.product }" />

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

      <form class="well form-horizontal">
        <fieldset>

          <div class="control-group">
            <label class="control-label">Code</label>
            <div class="controls">
              <span class="uneditable-input"><c:out value="${ product.code }" /></span>
            </div>
          </div>

          <div class="control-group">
            <label class="control-label">Name</label>
            <div class="controls">
              <span class="uneditable-input"><c:out value="${ product.name }" /></span>
            </div>
          </div>

          <div class="control-group">
            <label class="control-label">Price</label>
            <div class="controls">
              <span class="uneditable-input"><c:out value="${ product.price }" /></span>
            </div>
          </div>

        </fieldset>

        <div class="form-actions">
          <a href="<c:url value="/products/${ id }/edit" />" class="btn btn-primary">Edit</a>&nbsp;
          <a href="<c:url value="/products" />" class="btn">View list</a>
        </div>
      </form>

    </div>

  </body>
</html>
