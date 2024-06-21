import string
import sys


def main():
    cli_arguments = sys.argv[1:]
    for file_name in cli_arguments:
        f = open("exemplu_mapreduce/input/{}".format(file_name), "r")
        for line in f:
            table = str.maketrans('', '', string.punctuation)
            result = list(map(lambda word: [word.translate(table), {file_name: 1}],
                              list(filter(lambda word: word != "", line.strip().split(" ")))))
            for elem in result:
                print(str(elem))
        f.close()


if __name__ == "__main__":
    main()
