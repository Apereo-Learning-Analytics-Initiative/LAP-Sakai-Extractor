LAP-Sakai-Extractor
===================

Learning analytics processor data extraction handler for Sakai. This will extract the learning system data that is needed for LAP to do processing from a Sakai LMS

-----

There are two files that will be generated:

<ul>
    <li>activity.csv</li>
    <li>grades.csv</li>
<ul>

The files will be placed in a directory named as the current date and time the extraction was run. For example, an extraction occurring on June 27, 2014 at 12:30:00 will be in a directory named: 20140627_123000

You can configure where on the server (an NFS or similar is suggested), by setting the property 'lap.data.extraction.storage.path' to the path on the server. The default is to use the setting from bodyPath@org.sakaiproject.content.api.ContentHostingService and adding "/lap-data/" to the end.

-----

Data extraction can occur in two ways:

<ol>
    <li>Manually, via a button press</li>
    <li>Automatically, via threading</li>
</ol>

You may initiate a manual extraction of data by:

<ol>
    <li>(OPTIONALLY) Setting a search criteria on site titles (e.g. 'FA14', 'FA%', etc.)</li>
    <li>(OPTIONALLY) Setting a start date for the activity extraction</li>
    <li>(OPTIONALLY) Setting an end date for the activity extraction</li>
    <li>Pressing the 'Extract Data' button</li>
</ol>

You may also set up the system to automatically run a data extraction at certain times. To do so:

<ol>
    <li>Enable the automatic extraction by setting 'lap.automatic.extraction.enabled=true' in sakai.properties</li>
    <li>Set the times you wish the automatic extraction to run by setting the 'lap.data.extraction.times' property in sakai.properties. The default is midnight (00:00:00) and noon (12:00:00). The times must be set in 24-hour notation.</li>
</ol>

-----

Configuration:

sakai.properties:

```
# LAP Sakai Data Extractor

# Enable automatic data extraction
# Default: true
#lap.automatic.extraction.enabled=true

# File system path to store data extraction files
# Default: bodyPath@org.sakaiproject.content.api.ContentHostingService + lap-data
#lap.data.extraction.storage.path=

# Time of day that the automatic data extraction will run
#lap.data.extraction.times.count=2
#lap.data.extraction.times.1=00:00:00
#lap.data.extraction.times.2=12:00:00
```