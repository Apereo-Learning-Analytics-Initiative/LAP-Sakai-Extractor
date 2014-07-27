/*
 * Copyright 2014 Sakaiproject Licensed under the
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

    /**
     * Button click to download a file
     */
    $(".buttons-extraction-download > td").on("click", "button", function() {
        $("#action").val(this.id);
        $("#form-download").submit();
    });

    /**
     * Button click to start a data extraction
     */
    $(".button-extraction").on("click", "button", function() {
        var filterData = $("#form-extraction").serialize();

        extractData(filterData, function(success, errorThrown) {
            var status = (success) ? "success" : "error";
            $("#status-type").val(status);
            if (!success) {
                $("#status-error-thrown").val(errorThrown);
            }

            $("#form-extraction").submit();
        });
    });

    /**
     * POST request to run data extraction
     */
    function extractData(filterData, callback) {
        var request = $.ajax({
            type: "POST",
            url:  url + "extraction",
            data: filterData,
            cache: false,
            async: false
        });

        request.success(function(data, status, jqXHR) {
            callback(true, "");
        });

        request.fail(function(jqXHR, textStatus, errorThrown) {
            callback(false, errorThrown);
        });
    }

    /**
     *  Date picker for activity date range on extraction
     */
    $(function() {
        $(".date-picker").datepicker({
            dateFormat: "yy-mm-dd",
            constrainInput: true,
            maxDate: "+0d",
            onClose: function() {
                checkDatePickerDates();
            }
        });
    });

    /**
     * Validates the dates selected in the date picker
     * Throws a warning if the end date is before the start date
     */
    function checkDatePickerDates() {
        $(".button-extraction > button").show();
        $(".date-picker-error").hide();

        var startDate = $("#start-date").datepicker("getDate");
        var endDate = $("#end-date").datepicker("getDate");

        if (startDate && endDate) {
            if (startDate > endDate) {
                $(".button-extraction > button").hide();
                $(".date-picker-error").show();
            }
        }
    }

    /**
     * GET request for the statistics from the server
     */
    $.ajax({
        type: "GET",
        url:  url + "statistics",
        cache: false,
        async: false,
        success: (function(data, status, jqXHR) {
            $.map(data.latestExtractionDate, function(date, i) {
                $(".latest-extraction-date").html(date.displayDate);
            });
            $(".next-extraction-date").html(data.nextExtractionDate);
            createExtractionListing(data.allExtractionDates);
            createDownloadButtons(data.availableFiles);
        }),
        fail: (function(jqXHR, textStatus, errorThrown) {})
    });

    /**
     * Creates the drop-down listing of extraction dates
     */
    function createExtractionListing(allExtractionDates) {
        var extractionsExist = false;
        $.each(allExtractionDates, function(key, value) {
            $("#extraction-date").append($("<option>", {value : key}).text(value.displayDate));
            extractionsExist = true;
        });

        // show the "no extractions" dialog if none exist
        if (!extractionsExist) {
            $("#extraction-date").hide();
            $(".buttons-extraction-download").hide();
            $(".no-extractions-exist").show();
        }
    }

    /**
     * Creates the download button for each available file type
     */
    function createDownloadButtons(availableFiles) {
        $.each(availableFiles, function(key, value) {
            $(".buttons-extraction-download > td")
                .append(
                    $("<button>",
                        {
                            id : key,
                            class : "btn btn-primary"
                        }
                    )
                    .text(key)
                );
        });
    }

});
