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
  <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap.min.css" />" />
  <script type="text/javascript" src="<c:url value="/resources/js/jquery-1.6.2.min.js" />"></script>
</head>
<body>

  <header>
    <div class="inner">
      <div class="container">
        <h2>Samson JSP</h2>
      </div>
    </div>
  </header>

  <section>
    <div class="container">
      <form>
        <fieldset>

          <div class="clearfix">
            <label>Code</label>
            <div class="input">
              <span class="uneditable-input"><c:out value="${ product.code }" /></span>
            </div>
          </div>

          <div class="clearfix">
            <label>Name</label>
            <div class="input">
              <span class="uneditable-input"><c:out value="${ product.name }" /></span>
            </div>
          </div>

          <div class="clearfix">
            <label>Price</label>
            <div class="input">
              <span class="uneditable-input"><c:out value="${ product.price }" /></span>
            </div>
          </div>

        </fieldset>

        <div class="actions">
          <a href="<c:url value="/products/${ id }/edit" />" class="btn">Edit</a>&nbsp;
          <a href="<c:url value="/products" />" class="btn">View list</a>
        </div>
      </form>
    </div>
  </section>

</body>
</html>
