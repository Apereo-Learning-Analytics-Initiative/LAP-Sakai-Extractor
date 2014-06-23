<%--

    Copyright 2008 Sakaiproject Licensed under the
    Educational Community License, Version 2.0 (the "License"); you may
    not use this file except in compliance with the License. You may
    obtain a copy of the License at

    http://www.osedu.org/licenses/ECL-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an "AS IS"
    BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
    or implied. See the License for the specific language governing
    permissions and limitations under the License.

--%>
<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" 
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" 
%><%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" 
%><%@ taglib prefix="spring" uri="http://www.springframework.org/tags" 
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css" />
<link media="all" href="/library/skin/neo-default/tool.css" rel="stylesheet" type="text/css" />

<!-- Additional CSS -->
<!-- link media="all" href="<c:url value='/css/jquery-ui.css'/>" rel="stylesheet" type="text/css" / -->
<link media="all" href="<c:url value='/css/main.css'/>" rel="stylesheet" type="text/css" />

<script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
<!-- JQuery latest libs in Sakai library -->
<!--script src="/library/js/jquery/jquery-1.9.1.min.js" language="JavaScript" type="text/javascript"></script>
<script src="/library/js/jquery/ui/1.8.4/jquery-ui-1.8.4.full.min.js" language="JavaScript" type="text/javascript"></script -->
<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>

<!-- Additional JavaScript -->
<script src="<c:url value='/js/scripts.js'/>" language="JavaScript" type="text/javascript"></script>

<!-- Twitter Bootstrap -->
<link media="all" href="<c:url value='/css/bootstrap.min.css'/>" rel="stylesheet" type="text/css" />

<title><spring:message code="lap.title" /></title>
</head>
<body onload="<%=request.getAttribute("sakai.html.body.onload")%>">
<!-- Hidden block to declare resource bundle variables for use in JS -->
<div style="display: none;">
  <!-- By convention, the id of the span must be "i18n_"+{message code}, the container div should be set to display:none;, use kaltura.i18n(key) in JS to lookup the message -->
</div>
<div class="portletBody">
