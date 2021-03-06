
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="samson" prefix="sms"%>

<c:set var="id" value="${ it.id }" />
<c:set var="productForm" value="${ it.productForm }" />

<form class="well form-horizontal" action="<c:url value="/products/${sms:urlEncode( id )}" />" method="post">
  <fieldset>

    <c:set var="field" value="${ productForm.node.propertyPath('code') }" />

    <div class="control-group <c:if test="${ field.error }">error</c:if>" >
      <label class="control-label" for="code">Code</label>
      <div class="controls">
        <input id="code" name="product.code" type="text" value="${fn:escapeXml( field.value )}" />
        <span class="help-inline"><c:out value="${ sms:messages(field) }" /></span>
      </div>
    </div>

    <c:set var="field" value="${ productForm.node.propertyPath('name') }" />

    <div class="control-group <c:if test="${ field.error }">error</c:if>" >
      <label class="control-label" for="name">Name</label>
      <div class="controls">
        <input id="name" name="product.name" type="text" value="${fn:escapeXml( field.value )}" />
        <span class="help-inline"><c:out value="${ sms:messages(field) }" /></span>
      </div>
    </div>

    <c:set var="field" value="${ productForm.node.propertyPath('price') }" />

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
    <a href="<c:url value="/products/${sms:urlEncode( id )}" />" class="btn">Cancel</a>
  </div>
</form>
