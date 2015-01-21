#!/bin/bash

#What was the most popular article?

awk 'NR==1 {print $1}' output.txt
