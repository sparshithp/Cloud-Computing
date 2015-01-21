#!/usr/bin/python

from operator import itemgetter

import sys
#initializes the variables required to reduce the mapped file
current_word = None
current_count = 0
word = None
out=[] #a list to store the data for every article

for line in sys.stdin:
   
    line = line.strip()

    row= line.split('\t')
    

#continue if there is a value error
    try:
        row[1] = int(row[1])  
    except ValueError:    
        continue

    if current_word == row[0]:
        current_count += row[1]
        if row[2] in out:
            pos = out.index(row[2]) #if the date was present on the lsist, it adds the views to the date
            out[pos+1]+=row[1]
        else:
            out.append(row[2]) # appends views for another date which was not on the list previously
            out.append(row[1])
       
    else:
        if current_word and (current_count>100000):             
            sys.stdout.write('%s\t%s' % ( current_count, current_word)) #print the monthly count and the article name
            for i in out[2:]:
                sys.stdout.write('\t%s' %(i)) #print the date and daily views
            sys.stdout.write("\n")
        del out[:]
        current_count = row[1]
        current_word = row[0]
        word=row[0]
        out.extend([current_word, current_count, row[2], current_count])

# For last row 
if current_word == word and (current_count>100000):  
    if  len(out)!=0:
        sys.stdout.write ('%s\t%s' % (current_count, current_word))
        
        for i in out[2:]:
            sys.stdout.write ('\t%s' %(i)) 
        sys.stdout.write("\n") 
