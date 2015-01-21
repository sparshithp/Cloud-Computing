#!/bin/bash

#Before filtering, what was the total number of requests made to all of wikipedia (all subprojects, all elements, all languages) during the hour covered by the file pagecounts-20140701-000000.gz

awk '{s+=$3}END{print s+0}' pagecounts-20140701-000000
