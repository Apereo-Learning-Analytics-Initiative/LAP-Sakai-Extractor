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
$.browser={};
(function(){
    $.browser.msie = false;
    $.browser.version = 0;
    if(navigator.userAgent.match(/MSIE ([0-9]+)\./)){
        $.browser.msie = true;
        $.browser.version = RegExp.$1;
    }
})();

$(document).ready(function() {
    var url = "/direct/lap-sakai-extractor/";

    /* download a csv file */
    $(".extraction-download-button").click(function() {
        $("#action").val(this.id);
        $("#download-form").submit();
    });

    /* start data extraction */
    $("#extraction").click(function() {
        var filterData = $("#extraction-form").serialize();

        extractData(this.id, filterData, function(success, data) {
            var status = (success) ? "success" : "error";
            $("#statusMessageType").val(status);
            $("#statusMessage").val(data);

            $("#extraction-form").submit();
        });
    });

    function extractData(endpoint, filterData, callback) {
        var request = $.ajax({
            type: "POST",
            url:  url + endpoint,
            data: filterData,
            cache: false,
            async: false
         });

        request.success(function(data, status, jqXHR) {
            callback(true, "Data CSVs created successfully.");
        });

        request.fail(function(jqXHR, textStatus, errorThrown) {
            callback(false, errorThrown);
        });
    }

    /* data picker for date range on extraction */
    $(function() {
        $(".datePicker").datepicker({dateFormat: "yy-mm-dd"});
    });

    /* get statistics */
    $.ajax({
        type: "GET",
        url:  url + "statistics",
        cache: false,
        async: false,
        success: (function(data, status, jqXHR) {
            $.map(data.latestExtractionDate, function(date, i) {
                $("#latest-extraction-date").html(date.displayDate);
            });
            $("#next-extraction-date").html(data.nextExtractionDate);
            createExtractionListing(data.allExtractionDates);
        }),
        fail: (function(jqXHR, textStatus, errorThrown) {
            $("#latest-extraction-date").html("Error getting data.");
            $("#next-extraction-date").html("Error getting data.");
        })
    });

    function createExtractionListing(allExtractionDates) {
        var extractionsExist = false;
        $.each(allExtractionDates, function(key, value) {
            $("#extractions-listing").append($("<option>", {value : key}).text(value.displayDate));
            extractionsExist = true;
        });

        // show the "no extractions" dialog if none exist
        if (!extractionsExist) {
            $("#extractions-listing").hide();
            $(".extraction-download-button").hide();
            $(".no-extractions-exist").show();
        }
    }

});
