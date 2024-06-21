import ast
import sys


def format_input(string):
    string_list = ast.literal_eval(string)
    return [elem for elem in string_list]


def main():
    dict_result = {}
    fp = open("result.txt", "w")
    for line in sys.stdin:
        data = format_input(line)
        if data[0] in dict_result:
            dict_in_result = dict_result[data[0]]
            dict_to_add = data[1]
            file_name = [*dict_to_add][0]
            if file_name in dict_in_result:
                dict_in_result[file_name] += dict_to_add[file_name]
            else:
                dict_in_result[file_name] = dict_to_add[file_name]
        else:
            dict_result[data[0]] = data[1]
    for key in dict_result:
        fp.write("<{}, {}>\n".format(key, dict_result[key]))
    fp.close()


if __name__ == "__main__":
    main()
