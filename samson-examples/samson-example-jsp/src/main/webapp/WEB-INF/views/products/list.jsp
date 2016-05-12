
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="samson" prefix="sms"%>

<c:set var="products" value="${ it.products }" />

<table class="table">
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
        <td><a href="<c:url value="/products/${sms:urlEncode( product.id )}" />"><c:out value="${ product.code }" /></a></td>
        <td><c:out value="${ product.name }" /></td>
        <td><c:out value="${ product.price }" /></td>
      </tr>
    </c:forEach>
  </tbody>
</table>

<div>
  <a href="<c:url value="/products/new" />" class="btn">Create new</a>
</div>
