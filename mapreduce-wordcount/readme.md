本例子是一个mapreduce程序的word count的例子。<br>

在Hadoop上运行mapreduce程序的命令格式：<br>
hadoop jar jarFile mainclass args...<br>

本例子的运行命令：<br>
```
hadoop jar mapreduce-wordcount.jar org.cooze.hadoop.mapreduce.wordcount.WordCount /wordcount /output
```

解释：<br>
/test 是存放待计算的文件目录
/output 是通过是/test目录下的文件得到结果存放的目录

