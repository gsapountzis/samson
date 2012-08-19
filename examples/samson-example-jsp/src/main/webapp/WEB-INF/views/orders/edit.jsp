
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="samson" prefix="sms"%>

<c:set var="id" value="${ it.id }" />
<c:set var="orderForm" value="${ it.orderForm }" />
<c:set var="customerOptions" value="${ it.customerOptions }" />
<c:set var="productOptions" value="${ it.productOptions }" />

<form id="orderForm" class="well form-horizontal" action="<c:url value="/orders/${sms:urlEncode( id )}" />" method="post">
  <fieldset>

    <c:set var="field" value="${ orderForm.node.propertyPath('customerId') }" />

    <div class="control-group <c:if test="${ field.error }">error</c:if>" >
      <label class="control-label" for="customerId">Customer</label>
      <div class="controls">
        <select id="customer" name="customerId">
          <!-- option value="">-- Select customer --</option -->
          <c:forEach var="option" items="${ customerOptions }">
            <option value="${fn:escapeXml( option.key )}" <c:if test="${ option.key == field.value }">selected="selected"</c:if>><c:out value="${ option.value }" /></option>
          </c:forEach>
        </select>
        <span class="help-inline"><c:out value="${ sms:messages(field) }" /></span>
      </div>
    </div>

    <c:set var="field" value="${ orderForm.node.propertyPath('code') }" />

    <div class="control-group <c:if test="${ field.error }">error</c:if>" >
      <label class="control-label" for="code">Code</label>
      <div class="controls">
        <input id="code" name="code" type="text" value="${fn:escapeXml( field.value )}" />
        <span class="help-inline"><c:out value="${ sms:messages(field) }" /></span>
      </div>
    </div>

    <c:set var="field" value="${ orderForm.node.propertyPath('orderDate') }" />

    <div class="control-group <c:if test="${ field.error }">error</c:if>" >
      <label class="control-label" for="orderDate">Order date</label>
      <div class="controls">
        <input id="orderDate" name="orderDate" type="text" value="${fn:escapeXml( field.value )}" />
        <span class="help-inline"><c:out value="${ sms:messages(field) }" /></span>
      </div>
    </div>

    <c:set var="field" value="${ orderForm.node.propertyPath('shipDate') }" />

    <div class="control-group <c:if test="${ field.error }">error</c:if>" >
      <label class="control-label" for="shipDate">Ship date</label>
      <div class="controls">
        <input id="shipDate" name="shipDate" type="text" value="${fn:escapeXml( field.value )}" />
        <span class="help-inline"><c:out value="${ sms:messages(field) }" /></span>
      </div>
    </div>

    <c:set var="field" value="${ orderForm.node.propertyPath('status') }" />

    <div class="control-group <c:if test="${ field.error }">error</c:if>" >
      <label class="control-label" for="status">Status</label>
      <div class="controls">
        <input id="status" name="status" type="text" value="${fn:escapeXml( field.value )}" />
        <span class="help-inline"><c:out value="${ sms:messages(field) }" /></span>
      </div>
    </div>

    <c:set var="itemsNode" value="${ orderForm.node.propertyPath('items') }" />

    <!-- errors for all items -->
    <div class="control-group <c:if test="${ itemsNode.error }">error</c:if>" >
      <div class="controls">
        <span class="help-inline"><c:out value="${ sms:messages(itemsNode) }" /></span>
      </div>
    </div>

    <div class="order-items">

      <c:set var="order" value="${ orderForm.value }" />
      <c:set var="items" value="${ order.items }" />

      <c:forEach items="${ items }" var="item" varStatus="itemStatus">

        <c:set var="itemNode" value="${ itemsNode.indexPath( itemStatus.index ) }" />
        <c:set var="productNode" value="${ itemNode.propertyPath( 'product' ) }" />
        <c:set var="qtyNode" value="${ itemNode.propertyPath( 'qty' ) }" />

        <!-- edit item, "name" attr is filled at submission time -->
        <div class="order-item-edit control-group <c:if test="${ sms:multiError2(productNode, qtyNode) }">error</c:if>" >
          <div class="controls">
            <input id="id" type="hidden" value="${fn:escapeXml( item.productId )}" />
            <input id="name" type="hidden" value="${fn:escapeXml( item.product.name )}" />

            <input readonly="readonly" value="${fn:escapeXml( item.product.name )}" /> <span>&nbsp;&times;&nbsp;</span>
            <input id="qty" class="span1" type="text" value="${fn:escapeXml( qtyNode.value )}" />

            <button id="del" type="button" class="btn btn-small">
              <i class="icon-minus"></i>
            </button>
            <span class="help-inline"><c:out value="${ sms:multiMessages2(productNode, qtyNode) }" /></span>
          </div>
        </div>

      </c:forEach>

    </div>

    <!-- tmpl for new item, "value" attr is filled at addition time -->
    <div style="display:none">
      <div class="order-item-edit-tmpl control-group">
        <div class="controls">
          <input id="id" type="hidden" />
          <input id="name" type="hidden" />

          <input id="nameText" readonly="readonly" /> <span>&nbsp;&times;&nbsp;</span>
          <input id="qty" class="span1" type="text" />

          <button id="del" type="button" class="btn btn-small">
            <i class="icon-minus"></i>
          </button>
        </div>
      </div>
    </div>

    <!-- add new item, no "name" attr, not submitted with the form -->
    <div class="order-items-new control-group">
      <div class="controls">
        <select id="product">
          <option value="">-- Select product --</option>
          <c:forEach var="option" items="${ productOptions }">
            <option value="${fn:escapeXml( option.key )}"><c:out value="${ option.value }" /></option>
          </c:forEach>
        </select> <span>&nbsp;&times;&nbsp;</span>
        <input id="qty" class="span1" type="text" value="1" />

        <button id="add" type="button" class="btn btn-small">
          <i class="icon-plus"></i>
        </button>
      </div>
    </div>

  </fieldset>

  <div class="form-actions">
    <input type="submit" class="btn btn-primary" value="Update">&nbsp;
    <a href="<c:url value="/orders/${sms:urlEncode( id )}" />" class="btn">Cancel</a>
  </div>
</form>
