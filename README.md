LAP-Sakai-Extractor
===================

Learning analytics processor data extraction handler for Sakai. This will extract the learning system data that is needed for LAP to do processing from a Sakai LMS

There are two files that will be generated:

<ul>
    <li>activity.csv</li>
    <li>grades.csv</li>
<ul>

The files will be placed in a directory named as the current date and time the extraction was run. For example, an extraction occurring on June 27, 2014 at 12:30:00 will be in a directory:

    20140627_123000


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