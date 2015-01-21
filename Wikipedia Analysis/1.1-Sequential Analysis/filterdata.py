#Python program to filter unwanted lines
#author: Sparshith Puttaswamy Gowda

f = open("pagecounts-20140701-000000", "r")
w = open("output.txt", "w")

#create lists with filtering string conditions
bp = ["404_error/", "Main_Page", "Hypertext_Transfer_Protocol", "Favicon.ico", "Search"]
ext = [".jpg", ".gif", ".png", ".JPG", ".GIF", ".PNG", ".txt", ".ico"]
special = ["Media:", "Special:", "Talk:", "User:", "User_talk:", "Project:", "Project_talk:", "File:", "File_talk:",
"MediaWiki:", "MediaWiki_talk:", "Template:", "Template_talk:", "Help:", "Help_talk:", "Category:", "Category_talk:",
"Portal:", "Wikipedia:", "Wikipedia_talk:"]


filtered =[]
for line in f:
    comp = line.split(' ') #split every line in the file at whitespace

    #if loop with all the constraints to filter lines
    #avoided defining seperate functions to prevent repeated function calls
    if comp[0]=="en" and (not(comp[1][0].islower())) and (comp[1] not in bp) and (comp[1][-4:] not in ext) and (not(any(comp[1].startswith(x) for x in special))):
        filtered.append([comp[1],comp[2]])

final = sorted(filtered,key = lambda l:int (l[1]), reverse=True) #sort based on number of views

for entries in final:
        w.write(str(entries[0]) + "\t" + (str(entries[1])) + "\n")#write article name and number of requests

f.close()
w.close()


