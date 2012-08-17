
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="id" value="${ it.id }" />
<c:set var="order" value="${ it.order }" />

<form class="well form-horizontal">
  <fieldset>

    <div class="control-group">
      <label class="control-label">Customer</label>
      <div class="controls">
        <span class="uneditable-input"><c:out value="${ order.customer.name }" /></span>
      </div>
    </div>

    <div class="control-group">
      <label class="control-label">Code</label>
      <div class="controls">
        <span class="uneditable-input"><c:out value="${ order.code }" /></span>
      </div>
    </div>

    <div class="control-group">
      <label class="control-label">Order date</label>
      <div class="controls">
        <span class="uneditable-input"><fmt:formatDate value="${ order.orderDate }" /></span>
      </div>
    </div>

    <div class="control-group">
      <label class="control-label">Ship date</label>
      <div class="controls">
        <span class="uneditable-input"><fmt:formatDate value="${ order.shipDate }" /></span>
      </div>
    </div>

    <div class="control-group">
      <label class="control-label">Status</label>
      <div class="controls">
        <span class="uneditable-input"><c:out value="${ order.status }" /></span>
      </div>
    </div>

    <div class="control-group">
      <div class="controls">
        <span class="help-inline"></span>
      </div>
    </div>

    <c:forEach var="item" items="${ order.items }">

      <div class="control-group">
        <div class="controls">
          <span class="uneditable-input"><c:out value="${ item.product.name }" /></span> <span>&nbsp;&times;&nbsp;</span>
          <span class="uneditable-input span1"><c:out value="${ item.qty }" /></span>
        </div>
      </div>

    </c:forEach>
  </fieldset>

  <div class="form-actions">
    <a href="<c:url value="/orders/${ id }/edit" />" class="btn btn-primary">Edit order</a>&nbsp;
    <a href="<c:url value="/orders" />" class="btn">View list</a>
  </div>
</form>
