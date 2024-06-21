import sys

dictionar = {}

def reducer():
    for line in sys.stdin:
        line = line.strip()
        word, filename, count = line.split()
        word = word[0:-1]
        filename = filename[1:-1]
        count = count[0:-1]

        if word not in dictionar:
            dictionar[word] = {filename: 1}
        else:
            if filename not in dictionar[word]:
                dictionar[word][filename] = 1
            else:
                dictionar[word][filename] += int(count)
    
#    for words in dictionar:
#        strr = "{"
#        for filenames in dictionar[words]:
#            strr += filenames + ":" + str(dictionar[words][filenames]) + ", "
#        strr = strr[0:-2]
#        strr += "}"
#        print(words + ", " + strr)


    for words in dictionar:
        print(words + "," + str(dictionar[words]))
        
reducer()
