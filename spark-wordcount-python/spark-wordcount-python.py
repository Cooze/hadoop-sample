#!/usr/bin/env python
#-*-conding:utf-8-*-

import logging
from operator import add
from pyspark import SparkContext
import sys

logging.basicConfig(format='%(message)s', level=logging.INFO)

files = "/wordcount"

def main():
    sc = SparkContext("local","WordCount")

    # 将hdfs中的文件成rdd
    rdd = sc.textFile(files)

    # 统计单词个数
    counts = rdd.flatMap(lambda line: line.split(" ")).map(lambda word: (word, 1)).reduceByKey(lambda a, b: a + b)

    # 在控制台中打印输出
    results = counts.collect()

    for result in results:
        print("%s\t\t%d" % (result[0],result[1]))
    sc.stop()

if __name__=="__main__":
       main()
