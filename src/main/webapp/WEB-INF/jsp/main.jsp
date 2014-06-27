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
<%@ page contentType="text/html" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:directive.include file="/WEB-INF/jsp/header.jsp" />

<div class="portletBody">

<c:if test="${not empty(error)}">
    <div class="alert alert-danger">
        <a class="close" data-dismiss="alert">x</a>
        <span class="error-message">${error}</span>
    </div>
</c:if>
<c:if test="${not empty(success)}">
    <div class="alert alert-success">
        <a class="close" data-dismiss="alert">x</a>
        <span class="success-message">${success}</span>
    </div>
</c:if>

<h2><spring:message code="title" /></h2>

<div class="instructions clear">
    <label><spring:message code="label.latest.data.generation" /> <span id="last-run-date" class="statistics"></span></label>
    <br />
    <label><spring:message code="label.next.data.generation" /> <span id="next-run-date" class="statistics"></span></label>
</div>
<fieldset class="form-fieldset">
    <legend class="form-legend"><spring:message code="legend.download" /></legend>
    <form id="download-form" method="post" action="download.htm" target="_blank">
        <table class="table table-hover form-table">
            <tr>
                <td><label for="directory"><spring:message code="label.select.directory" /></label></td>
                <td>
                    <select id="directory" name="directory" class="form-control">
                        <c:forEach var="directory" items="${directories}">
                            <option value="${directory.key}">${directory.value}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <button class="btn btn-primary csv" id="courses"><spring:message code="button.courses" /></button>
                    <button class="btn btn-primary csv" id="grades"><spring:message code="button.grades" /></button>
                    <button class="btn btn-primary csv" id="students"><spring:message code="button.students" /></button>
                    <button class="btn btn-primary csv" id="usage"><spring:message code="button.usage" /></button>
                </td>
            <tr>
        </table>
        <input type="hidden" id="action" name="action" value="" />
    </form>
</fieldset>
<fieldset class="form-fieldset">
    <legend class="form-legend"><spring:message code="legend.generate" /></legend>
    <form id="generate-form" method="post" action="main.htm">
        <spring:message code="placeholder.criteria" var="criteriaPlaceholder" />
        <table class="table table-hover form-table">
            <tr>
                <td><label for="criteria"><spring:message code="label.criteria" /></label></td>
                <td><input type="text" id="criteria" name="criteria" value="" placeholder="${criteriaPlaceholder}" /></td>
            </tr>
            <tr>
                <td><label for="startDate"><spring:message code="label.start" /></label></td>
                <td><input type="text" id="startDate" name="startDate" class="datePicker" /></td>
            </tr>
            <tr>
                <td><label for="endDate"><spring:message code="label.end" /></label></td>
                <td><input type="text" id="endDate" name="endDate" class="datePicker" /></td>
            </tr>
            <tr>
                <td colspan="2"><button class="btn btn-danger" id="generate"><spring:message code="button.generate" /></button></td>
            </tr>
        </table>
        <input type="hidden" id="statusMessageType" name="statusMessageType" value="" />
        <input type="hidden" id="statusMessage" name="statusMessage" value="" />
    </form>
</fieldset>

</div>

<jsp:directive.include file="/WEB-INF/jsp/footer.jsp" />
