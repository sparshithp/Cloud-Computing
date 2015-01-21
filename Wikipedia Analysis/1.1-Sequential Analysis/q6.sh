#!/bin/bash

#How many views did this article get?

awk 'NR==1 {print $2}' output.txt
