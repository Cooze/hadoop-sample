本例子是用scala写的一个spark的wordcount的例子。<br>

将生成jar包提交到spark上运行的命令：
```
spark-submit --master spark://localhost:7077 --name WordCount \
--class org.cooze.hadoop.spark.wordcount.scala.WordCount --executor-memory 512M \
--total-executor-cores 2 ./spark-wordcount-scala.jar hdfs://localhost:9000/wordcount
```
或
```
spark-submit --master spark://localhost:7077 \
 --name WordCount --class org.cooze.hadoop.spark.wordcount.scala.WordCount \
  --executor-memory 512M --total-executor-cores 2 \
  ./spark-wordcount-scala.jar /wordcount

```

