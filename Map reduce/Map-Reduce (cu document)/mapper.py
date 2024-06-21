
import sys

file_names = ["fisier1.txt", "fisier2.txt", "fisier3.txt"]

def mapper():
    for file_name in file_names:
        try: 
            f = open(file_name, 'r')
            lines = f.readlines()
            for line in lines:
                line = line.strip()
                words = line.split()
                for word in words:
                    print('%s, {%s: %s}' % (word, f.name, 1))
        except:
            pass
mapper()