/*
 * Copyright 2008 Sakaiproject Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

// hack for MSIE browser look-up error
$.browser={};(function(){$.browser.msie=false;
$.browser.version=0;if(navigator.userAgent.match(/MSIE ([0-9]+)\./)){
$.browser.msie=true;$.browser.version=RegExp.$1;}})();

$(document).ready(function() {
    var url = "/direct/lap-sakai-extractor/";

    $(".csv").click(function() {
        $("#action").val(this.id);
        $("#download-form").submit();
    });

    $("#generate").click(function() {
        var filterData = $("#generate-form").serialize();

        generateNewData(this.id, filterData, function(success, data) {
            var status = (success) ? "success" : "error";
            $("#statusMessageType").val(status);
            $("#statusMessage").val(data);

            $("#generate-form").submit();
        });
    });

    function generateNewData(endpoint, filterData, callback) {
        var request = $.ajax({
            type: "POST",
            url:  url + endpoint,
            data: filterData,
            cache: false,
            async: false
         });

        request.success(function(data, status, jqXHR) {
            callback(true, "Sakai data CSVs created successfully.");
        });

        request.fail(function(jqXHR, textStatus, errorThrown) {
            callback(false, errorThrown);
        });
    }

    $(function() {
        $(".datePicker").datepicker({dateFormat: "yy-mm-dd"});
    });
});