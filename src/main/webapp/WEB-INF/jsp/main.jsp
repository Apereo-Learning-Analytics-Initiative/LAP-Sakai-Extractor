<%--

    Copyright 2014 Sakaiproject Licensed under the
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

<div class="instructions clear extraction-dates-display">
    <label><spring:message code="label.latest.data.extraction" /> <span class="latest-extraction-date statistics"><spring:message code="message.error.ajax.dates" /></span></label>
    <br />
    <label><spring:message code="label.next.data.extraction" /> <span class="next-extraction-date statistics"><spring:message code="message.error.ajax.dates" /></span></label>
</div>
<fieldset class="form">
    <legend class="form"><spring:message code="legend.download" /></legend>
    <form id="form-download" method="post" action="download.htm" target="_blank">
        <table class="table table-hover form download">
            <tr>
                <td><label for="extraction-date"><spring:message code="label.select.extraction" /></label></td>
                <td>
                    <select id="extraction-date" name="extraction-date" class="form-control"></select>
                    <label class="no-extractions-exist"><spring:message code="label.no.extractions" /></label>
                </td>
            </tr>
            <tr class="buttons-extraction-download">
                <td colspan="2"></td>
            <tr>
        </table>
        <input type="hidden" id="action" name="action" value="" />
    </form>
</fieldset>
<fieldset class="form">
    <legend class="form"><spring:message code="legend.extraction" /></legend>
    <form id="form-extraction" method="post" action="main.htm">
        <spring:message code="placeholder.criteria" var="criteriaPlaceholder" />
        <table class="table table-hover form extraction">
            <tr>
                <td><label for="criteria"><spring:message code="label.criteria" /></label></td>
                <td><input type="text" id="criteria" name="criteria" value="" placeholder="${criteriaPlaceholder}" /></td>
            </tr>
            <tr class="activity-dates">
                <td colspan="2">
                    <fieldset class="activity">
                        <legend class="form"><spring:message code="legend.activity.dates" /></legend>
                        <table class="table table-hover form">
                            <tr>
                                <td><label for="start-date"><spring:message code="label.start" /></label></td>
                                <td><input type="text" id="start-date" name="start-date" class="date-picker" readonly="true" /></td>
                            </tr>
                            <tr>
                                <td><label for="end-date"><spring:message code="label.end" /></label></td>
                                <td><input type="text" id="end-date" name="end-date" class="date-picker" readonly="true" /></td>
                            </tr>
                        </table>
                    </fieldset>
                <td>
            </tr>
            <tr>
                <td colspan="2" class="button-extraction">
                    <button class="btn btn-danger"><spring:message code="button.extraction" /></button>
                    <div class="alert alert-danger date-picker-error">
                        <span class="error-message"><spring:message code="message.error.invalid.date.range" /></span>
                    </div>
                </td>
            </tr>
        </table>
        <input type="hidden" id="status-type" name="status-type" value="" />
        <input type="hidden" id="status-error-thrown" name="status-error-thrown" value="" />
    </form>
</fieldset>

<jsp:directive.include file="/WEB-INF/jsp/footer.jsp" />
