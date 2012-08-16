<!DOCTYPE html>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="body" value="${ it.body }" />

<html>
<head>
  <title>Samson JSP Example</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap.css" />" />
  <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/main.css" />" />
</head>

<body>

<div class="navbar navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container">
      <a class="brand" href="<c:url value="/" />">Samson JSP</a>
      <ul class="nav">
        <li class="">
          <a href="<c:url value="/products" />">Products</a>
        </li>
        <li class="">
          <a href="<c:url value="/orders" />">Orders</a>
        </li>
      </ul>
    </div>
  </div>
</div>

<div class="container">

  <jsp:include page="${ body }" />

</div>

<script type="text/javascript" src="<c:url value="/resources/js/jquery-1.7.2.js" />"></script>
</body>
</html>
