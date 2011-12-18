<!DOCTYPE html>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="products" value="${ it.products }" />

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

      <table>
        <thead>
          <tr>
            <th>Code</th>
            <th>Name</th>
            <th>Price</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="product" items="${ products }">
            <tr>
              <td><a href="<c:url value="/products/${ product.id }" />"><c:out value="${ product.code }" /></a></td>
              <td><c:out value="${ product.name }" /></td>
              <td><c:out value="${ product.price }" /></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

      <div>
        <a href="<c:url value="/products/new" />" class="btn">Create new</a>
      </div>

  </div>

  </body>
</html>
