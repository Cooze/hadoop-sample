package org.cooze.hadoop.spark.wordcount.scala

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by cooze on 2017/10/13.
  */
object WordCount {
  def main(args: Array[String]): Unit = {
    println("Hello WordCount programmer.")

    val appName = "WordCount"
    val master = "local"

    val conf = new SparkConf().setAppName(appName).setMaster(master)
    val context = new SparkContext(conf)

    //传入的第一个参数就是待计算文件路径
    val filePath = args(0)

    val rdd = context.textFile(filePath)

    //spark rrd
    rdd.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).collect().foreach(println)

    context.stop()

  }

}
