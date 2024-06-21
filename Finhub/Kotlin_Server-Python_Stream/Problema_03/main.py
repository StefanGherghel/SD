import json
import os

from pyspark import SparkContext
from pyspark.streaming import StreamingContext

os.environ["PYSPARK_PYTHON"] = "python3"
os.environ["PYSPARK_DRIVER_PYTHON"] = "python3"
os.environ["JAVA_HOME"] = "/usr/lib/jvm/jdk1.8.0_291"

positive_words = open("positive-words.txt", "r", encoding="ISO-8859-1").readlines()
negative_words = open("negative-words.txt", "r", encoding="ISO-8859-1").readlines()


def myPrint(obj):
    for elem in obj:
        tolerance = 0.05
        pos_prc = 0
        neg_prc = 0
        for elem2 in elem:
            pos = 0
            neg = 0

            if elem2 == "summary":
                words = elem[elem2].lower().split()
                for word in words:
                    if str(word + "\n") in positive_words:
                        pos += 1
                    if str(word + "\n") in negative_words:
                        neg += 1

                pos_prc = pos / len(words)
                neg_prc = neg / len(words)
                print("Content:" + elem["summary"])
        if abs(pos_prc - neg_prc) < tolerance:
            print("Neutral post")
        elif pos_prc > neg_prc:
            print("Positive post")
        else:
            print("Negative post")


if __name__ == "__main__":
    context = SparkContext(appName="Stream", master="local[*]")
    context.setLogLevel("WARN")
    spark_context = StreamingContext(context, 5)
    data = spark_context.socketTextStream("localhost", 8888)
    json_data = data \
        .map(lambda x: json.loads(x))
    json_data.foreachRDD(lambda rdd: myPrint(rdd.collect()))
    spark_context.start()
    spark_context.awaitTermination()
    spark_context.stop()
