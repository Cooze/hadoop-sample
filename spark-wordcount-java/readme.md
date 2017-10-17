本例子是使用java写的一个spark的wordcount例子.<br>

运行命令:
```
spark-submit --master spark://localhost:7077 \
 --name WordCount --class org.cooze.hadoop.spark.wordcount.java.WordCount \
  --executor-memory 512M --total-executor-cores 2 \
  ./spark-wordcount-java.jar hdfs://localhost:9000/wordcount
```
或
```
spark-submit --master spark://localhost:7077 \
 --name WordCount --class org.cooze.hadoop.spark.wordcount.java.WordCount \
  --executor-memory 512M --total-executor-cores 2 \
  ./spark-wordcount-java.jar /wordcount
```