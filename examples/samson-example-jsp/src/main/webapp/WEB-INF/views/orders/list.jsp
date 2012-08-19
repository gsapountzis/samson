
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="samson" prefix="sms"%>

<c:set var="orders" value="${ it.orders }" />

<table class="table">
  <thead>
    <tr>
      <th>Code</th>
      <th>Customer</th>
      <th>Order date</th>
      <th>Ship date</th>
      <th>Status</th>
    </tr>
  </thead>
  <tbody>
    <c:forEach var="order" items="${ orders }">
      <tr>
        <td><a href="<c:url value="/orders/${sms:urlEncode( order.id )}" />"><c:out value="${ order.code }" /></a></td>
        <td><c:out value="${ order.customer.name }" /></td>
        <td><fmt:formatDate value="${ order.orderDate }" /></td>
        <td><fmt:formatDate value="${ order.shipDate }" /></td>
        <td><c:out value="${ order.status }" /></td>
      </tr>
    </c:forEach>
  </tbody>
</table>
