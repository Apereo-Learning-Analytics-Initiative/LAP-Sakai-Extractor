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

$(document).ready(function() {
    var url = "/direct/lap-sakai-extractor/";

    $(".csv").click(function() {
        $("#action").val(this.id);
        $("#downloadForm").submit();
    });

    $("#generate").click(function() {
        var criteria = $("#criteria").serialize();
        generateNewData(this.id, criteria, function(data) {
            outputResponse(data);
        });
    });

    /*function getData(endpoint, directory, callback) {
        var request = $.ajax({ 
            type: "GET",
            url:  url + endpoint + "/" + directory,
            data: {}
         });

        request.success(function(data, status, jqXHR) {
            callback(data);
        });

        request.error(function(jqXHR, textStatus, errorThrown) {
            callback("Request failed: " + textStatus + ", error : " + errorThrown);
        });
    }*/

    function generateNewData(endpoint, criteria, callback) {
        console.log(criteria);
        var request = $.ajax({ 
            type: "POST",
            url:  url + endpoint,
            data: criteria
         });

        request.success(function(data, status, jqXHR) {
            callback(data);
        });

        request.error(function(jqXHR, textStatus, errorThrown) {
            callback("Request failed: " + textStatus + ", error : " + errorThrown);
        });
    }

    // temp function for testing
    function outputResponse(data) {
        $("#jsonResponse").html(JSON.stringify(data, null, 2));
    }
});