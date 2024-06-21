from pyspark import SparkContext
from pyspark.streaming import StreamingContext
import json
from datetime import datetime

class bcolors:
    GREEN = '\033[92m'
    ENDC = '\033[0m'



def print_info(json_obj):
    if len(json_obj) != 0:
        json_file = json.loads(json_obj[0])
        
        #am lasat 150 in loc de 80 pentru ca nu venea nicio stire care sa aiba un URL asa de mic
        #si ".jpg" pentru ca erau foarte putine png
        if ".jpg" in json_file['image'] and len(json_file['url']) <= 150:

            date_ = datetime.fromtimestamp(json_file['datetime']).strftime("%A, %B %d, %Y %I:%M:%S")

            print()

            print("==========================================")
            print(bcolors.GREEN + json_file['headline'] + bcolors.ENDC)
            print(bcolors.GREEN + str(date_) + bcolors.ENDC)
            print(bcolors.GREEN + json_file['url'] + bcolors.ENDC)
            print("==========================================")

            print()

if __name__ == '__main__':
    sc = SparkContext("local[2]", "News")
    ssc = StreamingContext(sc, 1)

    lines = ssc.socketTextStream("127.0.0.1", 65432)

    lines.foreachRDD(lambda rdd:print_info(rdd.collect()))


    ssc.start()  
    ssc.awaitTermination()  