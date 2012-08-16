
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="id" value="${ it.id }" />
<c:set var="product" value="${ it.product }" />

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
