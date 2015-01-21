#!/usr/bin/python
import sys
import os

#Filter conditions based on previous project
bp = ["404_error/", "Main_Page", "Hypertext_Transfer_Protocol", "Favicon.ico", "Search"]
ext = [".jpg", ".gif", ".png", ".JPG", ".GIF", ".PNG", ".txt", ".ico"]
special = ["Media:", "Special:", "Talk:", "User:", "User_talk:", "Project:", "Project_talk:", "File:", "File_talk:",
"MediaWiki:", "MediaWiki_talk:", "Template:", "Template_talk:", "Help:", "Help_talk:", "Category:", "Category_talk:",
"Portal:", "Wikipedia:", "Wikipedia_talk:"]

#get and select date from input file
fname= os.environ["map_input_file"]
datestr=fname[40:48]
date=datestr[-4:-2]+'/'+datestr[-2:]+'/'+datestr[0:4]+':'

filtered =[]
for line in sys.stdin:
    line=line.strip();
    comp = line.split(' ') 

    #if loop with all the constraints to filter lines
    #avoided defining seperate functions to prevent repeated function calls
    if comp[0]=="en" and (not(comp[1][0].islower())) and (comp[1] not in bp) and (comp[1][-4:] not in ext) and (not(any(comp[1].startswith(x) for x in special))):
        filtered.append([comp[1],comp[2]])


for entries in filtered:
    print ('%s\t%s\t%s'% ( entries[0],  str(entries[1]), date))


