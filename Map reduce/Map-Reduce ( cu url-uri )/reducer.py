import sys

dictionar = {}

def reducer():
    for line in sys.stdin:
        line = line.strip()
        word, url, count = line.split()
        word = word[0:-1]
        url = url[1:-1]
        count = count[0:-1]

        if word not in dictionar:
            dictionar[word] = {url: 1}
        else:
            if url not in dictionar[word]:
                dictionar[word][url] = 1
            else:
                dictionar[word][url] += int(count)
    for words in dictionar:
        print(words + "," + str(dictionar[words]))
        
reducer()
