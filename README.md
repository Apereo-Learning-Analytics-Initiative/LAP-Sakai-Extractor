LAP-Sakai-Extractor
===================

Learning analytics processor data extraction handler for Sakai. This will extract the learning system data that is needed for LAP to do processing from a Sakai LMS

There are four files that will be generated:

<ul>
    <li>courses.csv</li>
    <li>grades.csv</li>
    <li>students.csv</li>
    <li>usage.csv</li>
<ul>

The files will be placed in a directory named as the current date and time the generation was run. For example, a generation occurring on June 27, 2014 at 12:30:00 will be in a directory:

    20140627_123000


Configuration:

sakai.properties:

```
# LAP Sakai Data Extractor

# Enable automatic data report generation
# Default: true
#lap.automatic.generation.enabled=true

# File system path to store data extraction files
# Default: bodyPath@org.sakaiproject.content.api.ContentHostingService + lap-data
#lap.data.storage.path=

# Time of day that the automatic data report generation will run
# Times are in GMT
#lap.data.generation.times.count=2
#lap.data.generation.times.1=00:00:00
#lap.data.generation.times=12:00:00

# Period of time (in seconds) between checks to see if automatic data report generation should run
# Default: 60 seconds
#lap.data.generation.check.interval=60
```