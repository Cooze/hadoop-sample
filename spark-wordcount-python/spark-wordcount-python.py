#!/usr/bin/env python
#-*-conding:utf-8-*-

import logging
from operator import add
from pyspark import SparkContext
import sys

logging.basicConfig(format='%(message)s', level=logging.INFO)

#files = sys.argv[0]
files = "/wordcount"

def main():
    sc = SparkContext("local","WordCount")

    # text_file rdd object
    rdd = sc.textFile(files)

    # counts
    counts = rdd.flatMap(lambda line: line.split(" ")).map(lambda word: (word, 1)).reduceByKey(lambda a, b: a + b)

    # print on console
    results = counts.collect()

    for result in results:
        print("%s\t\t%d" % (result[0],result[1]))
    sc.stop()

if __name__=="__main__":
       main()