import sys


def main():
    dict_result = {}
    fp = open("result.txt", "w")
    for line in sys.stdin:
        line = line.strip("\n")
        data = line.split(" ")
        if data[0] == "":
            continue
        if len(data) != 3:
            continue
        if data[0] in dict_result:
            aux = dict_result[data[0]]
            if data[1] in aux:
                dict_result[data[0]][data[1]] += int(data[2])
            else:
                dict_result[data[0]][data[1]] = int(data[2])
        else:
            dict_result[data[0]] = {data[1]: int(data[2])}
    for key in dict_result:
        fp.write("<{}, {}>\n".format(key, dict_result[key]))
    fp.close()


if __name__ == "__main__":
    main()
