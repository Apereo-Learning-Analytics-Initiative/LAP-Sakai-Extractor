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

<h2><spring:message code="lap.title" /></h2>

<div class="instructions clear">
    <spring:message code="lap.instructions" />
</div>
<fieldset class="formFieldset">
    <legend class="formLegend">Download a data report</legend>
    <form id="downloadForm" method="post" action="download.htm">
        <div class="selectDateRange">
            <select id="directory" name="directory" class="form-control">
                <c:forEach var="directory" items="${directories}">
                    <option value="${directory.key}">${directory.value}</option>
                </c:forEach>
            </select>
        </div>
        <div>
            <button class="btn btn-primary csv" id="courses"><spring:message code="lap.button.courses" /></button>
            <button class="btn btn-primary csv" id="grades"><spring:message code="lap.button.grades" /></button>
            <button class="btn btn-primary csv" id="students"><spring:message code="lap.button.students" /></button>
            <button class="btn btn-primary csv" id="usage"><spring:message code="lap.button.usage" /></button>
        </div>
        <input type="hidden" id="action" name="action" value="" />
    </form>
</fieldset>
<fieldset class="formFieldset">
    <legend class="formLegend">Generate a new set of data reports</legend>
    <div class="generate">
        <label for="criteria"><spring:message code="lap.label.criteria" /></label>
        <input type="text" id="criteria" name="criteria" value="" placeholder="use '%' for a wildcard" />
    </div>
    <div class="generate">
        <button class="btn btn-danger" id="generate"><spring:message code="lap.button.generate" /></button>
    </div>
</fieldset>

</div>

<jsp:directive.include file="/WEB-INF/jsp/footer.jsp" />
