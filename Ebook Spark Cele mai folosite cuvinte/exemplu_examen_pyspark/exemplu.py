import os

from pyspark import *
from pyspark.streaming import StreamingContext

os.environ["PYSPARK_PYTHON"] = "python3"
os.environ["PYSPARK_DRIVER_PYTHON"] = "python3"


def runMain():

    ctx = SparkContext(appName="streaming", master="local[*]")
    ctx.setLogLevel("WARN")

    spark_context = StreamingContext(ctx, 1)

    lines_data = spark_context.textFileStream("4300-0.txt")

    words = lines_data.flatMap(lambda line: line.split(" ")).filter(lambda word: word != '')

    mapped_words = words.map(lambda word: (word, 1))
    words_count = mapped_words.reduceByKey(lambda x, y: x+y)

    words_count.saveAsTextFiles("result.txt")
    spark_context.start()
    spark_context.awaitTermination()
    spark_context.stop()


if __name__ == '__main__':
    runMain()
