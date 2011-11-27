<!DOCTYPE html>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="products" value="${ it }" />

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

      <div class="actions">
        <a href="<c:url value="/products/new" />" class="btn">Create new</a>
      </div>
    </div>
  </section>

</body>
</html>
