<!DOCTYPE html>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="product" value="${ it.fields }" />

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
      <form action="<c:url value="/products" />" method="post">
        <fieldset>

          <div class="clearfix <c:if test="${ product['code'].error }">error</c:if>" >
            <label for="code">Code</label>
            <div class="input">
              <input class="xlarge" id="code" name="product.code" size="30" type="text" value="${fn:escapeXml( product['code'].value )}" />

              <span class="help-inline">
                <c:out value="${ product['code'].conversionMessage }" />

                <c:if test="${ empty product['code'].conversionMessage }">
                  <c:forEach var="message" items="${ product['code'].validationMessages }">
                    <c:out value="${ message }" />
                  </c:forEach>
                </c:if>
              </span>
            </div>
          </div>

          <div class="clearfix <c:if test="${ product['name'].error }">error</c:if>" >
            <label for="name">Name</label>
            <div class="input">
              <input class="xlarge" id="name" name="product.name" size="30" type="text" value="${fn:escapeXml( product['name'].value )}" />

              <span class="help-inline">
                <c:out value="${ product['name'].conversionMessage }" />

                <c:if test="${ empty product['name'].conversionMessage }">
                  <c:forEach var="message" items="${ product['name'].validationMessages }">
                    <c:out value="${ message }" />
                  </c:forEach>
                </c:if>
              </span>
            </div>
          </div>

          <div class="clearfix <c:if test="${ product['price'].error }">error</c:if>" >
            <label for="price">Price</label>
            <div class="input">
              <input class="xlarge" id="price" name="product.price" size="30" type="text" value="${fn:escapeXml( product['price'].value )}" />

              <span class="help-inline">
                <c:out value="${ product['price'].conversionMessage }" />

                <c:if test="${ empty product['price'].conversionMessage }">
                  <c:forEach var="message" items="${ product['price'].validationMessages }">
                    <c:out value="${ message }" />
                  </c:forEach>
                </c:if>
              </span>
            </div>
          </div>

        </fieldset>

        <div class="actions">
          <input type="submit" class="btn primary" value="Create">&nbsp;
          <a href="<c:url value="/products" />" class="btn">Cancel</a>
        </div>
      </form>
    </div>
  </section>

</body>
</html>
