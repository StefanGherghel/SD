#!/usr/bin/env python
"""reducer.py"""
import re
import sys

words_dictionary = {}

# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()
    re.split("['/ ]", line)

    # parse the input we got from mapper.py
    letter, word = line.split()  # line.split('\t', 1)

    if letter in words_dictionary:
        if word not in words_dictionary[letter]:
            words_dictionary[letter].append(word)
    else:
        words_dictionary[letter] = list(word)

# do not forget to output the last word if needed!
for key in words_dictionary:
    print('{}\t{}'.format(key, words_dictionary[key]))
