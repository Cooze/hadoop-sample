### hadoop+spark 为分布式安装
基本环境及软件：

|软件版本 | 软件包|
|:---:|:---:|
|centos-6.x ||   
|JDK-1.8 | jdk-8u112-linux-x64.tar.gz|  
|scala-2.11.8 | jdk-8u112-linux-x64.tar.gz| 
|hadoop-2.7|hadoop-2.7.4.tar.gz|
|spark-2.1.2|spark-2.1.2-bin-hadoop2.7.tgz|

键入命令如下,安装hadoop所需的基础软件：
```
    yum install -y ssh pdsh
``` 

#### jdk和scala安装
`安装JDK`
1、键入如下命令将jdk软件包解压至指定目录：
```
tar -zxvf jdk-8u112-linux-x64.tar.gz -C /usr/local/
```
2、键入如下命令将scala软件包解压至指定目录：
```
 tar -zxvf scala-2.11.8.tgz -C /usr/local/
```
3、键入如下命令编辑profile文件：
```
vim /etc/profile
```
4、设置jdk和scala环境变量，在profile文件的末尾添加如下内容：
```
export JAVA_HOME=/usr/local/jdk1.8.0_112
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export SCALA_HOME=/usr/local/scala-2.11.8
export PATH=$JAVA_HOME/bin:$SCALA_HOME/bin:$PATH
```
5、键入如下命令让，环境变量立即生效：
```
source /etc/profile
```
6、检查jdk
```
[root@localhost ~]# java -version
java version "1.8.0_112"
Java(TM) SE Runtime Environment (build 1.8.0_112-b15)
Java HotSpot(TM) 64-Bit Server VM (build 25.112-b15, mixed mode)
```

7、检查scala
```
[root@localhost ~]# scala -version
Scala code runner version 2.11.8 -- Copyright 2002-2016, LAMP/EPFL
```

#### 解压&安装hadoop

1、键入如下命令将hadoop软件包解压至指定目录：
```
tar -zxvf hadoop-2.7.4.tar.gz -C /usr/local/
```
2、键入如下命令，给hadoop添加java环境变量：
```
vim /usr/local/hadoop-2.7.4/etc/hadoop/hadoop-env.sh
```
在`hadoop-env.sh`文件中添加如下内容：
```
export JAVA_HOME=/usr/local/jdk1.8.0_112
```
3、修改hadoop配置文件：<br>
1)键入如下命令，切换至hadoop配置文件目录：
``` 
/usr/local/hadoop-2.7.4/etc/hadoop
```
2)编辑`core-site.xml`配置文件,添加如下内容：
```xml
<configuration>
    <!--指定NamNode通信地址-->
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
   <!--指定Hadoop运行时产生文件的存储路径-->
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/opt/hadoop/tmp</value>
    </property>
</configuration>
```
3)编辑`hdfs-site.xml`配置文件，添加如下内容：
```xml
<configuration>
    <property>
        <name>dfs.name.dir</name>
        <value>/opt/hadoop/hdfs/name</value>
        <description>namenode上存储hdfs名字空间元数据 </description>
    </property>

    <property>
        <name>dfs.data.dir</name>
        <value>/opt/hadoop/hdfs/data</value>
        <description>datanode上数据块的物理存储位置</description>
    </property>

    <!-- 设置hdfs副本数量 -->
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
</configuration>
```
4)编辑`mapred-site.xml`配置文件，配置mapreducer框架运行在yarn上：<br>
键入如下命令复制`mapred-site.xml`配置文件：
``` 
cp mapred-site.xml.template mapred-site.xml
```
`mapred-site.xml`配置文件内容如下:
```xml
<configuration>
<!-- 通知框架MR使用YARN -->
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
```
4)编辑yarn配置`yarn-site.xml`，添加如下内容：
```xml
<configuration>
<!--reducer取数据的方式是mapreduce_shuffle-->
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
</configuration>
```
4、键入如下命令，创建hadoop数据目录：
```
mkdir -p /opt/hadoop/{tmp,hdfs/{data,name}}
```
5、键入如下命令，配置免密钥登录：
```
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 0600 ~/.ssh/authorized_keys
```
6、开启防火墙端口：<br>
键入如下命令，编辑防火墙配置文件：
```
vim /etc/sysconfig/iptables
```
添加如下内容：
```
-A INPUT -m state --state NEW -m tcp -p tcp --dport 8088 -j ACCEPT
-A INPUT -m state --state NEW -m tcp -p tcp --dport 50070 -j ACCEPT
```
重启防火墙：
```
service iptables restart
```
7、格式化hdfs，`只需要在第一次安装的时候格式化hdfs`,命令如下：
```
/usr/local/hadoop-2.7.4/bin/hdfs namenode -format
```
8、键入如下命令，启动hdfs和yarn：
``` 
/usr/local/hadoop-2.7.4/sbin/start-all.sh
```
9、在浏览器中分别输入如下地址校验，hadoop是否安装成功：
1)hadoop管理界面：`http://{your_ip_address}:50070/`
2)hadoop集群状态界面：`http://{your_ip_address}:8088/`

#### 解压&安装spark
1、键入如下命令将spark软件包解压至指定目录：
```
tar -zxvf spark-2.1.2-bin-hadoop2.7.tgz -C /usr/local/
```
2、编辑spark环境变量配置文件：
键入如下命令复制`spark-env.sh`配置文件
```
cp spark-env.sh.tamplate spark-env.sh
```
在`spark-env.sh`配置文件中添加如下内容：
```
export SCALA_HOME=/usr/local/scala-2.11.8
export JAVA_HOME=/usr/local/jdk1.8.0_112
export HADOOP_HOME=/usr/local/hadoop-2.7.4
export HADOOP_CONF_DIR=${HADOOP_HOME}/etc/hadoop
export SPARK_MASTER_IP=localhost
export SPARK_LOCAL_IP=localhost
export SPAPK_LOCAL_DIRS=/usr/local/spark-2.1.2-bin-hadoop2.7
export SPARK_DRIVER_MEMORY=1G
```
3、在slave配置文件中添加如下内容：
```
localhost
```
4、键入如下命令，启动spark：
```
/usr/local/spark-2.1.2-bin-hadoop2.7/sbin/start-all.sh
```
#### mapreduce 和 spark wordcount测试
1、创建`WordCount.txt`文件,内容如下：
```
Hello hadoop
hello spark
```
2、在`hdfs`中创建一个目录`wordcount`，并上传`WordCount.txt`文件，命令如下:
创建目录：
```
hadoop fs -mkdir /wordcount
```
上传文件：
```
hadoop fs -put -f WordCount.txt /wordcount
```
3、编写wordcount程序并执行：
1）mapreduce 版本示例代码，[跳转至github地址](https://github.com/Cooze/hadoop-sample/mapreduce-wordcount)<br>
将git上的程序编译之后上传至hadoop服务器，键入如下命令运行mapreduce：
```
hadoop jar mapreduce-wordcount.jar org.cooze.hadoop.mapreduce.wordcount.WordCount /wordcount /output
```
---
2) spark java版本示例代码，[跳转至github地址](https://github.com/Cooze/hadoop-sample/spark-wordcount-java)<br>
将git上的程序编译之后上传至hadoop服务器，键入如下命令将spark程序提交到spark中运行：
```
spark-submit --master spark://localhost:7077 \
 --name WordCount --class org.cooze.hadoop.spark.wordcount.java.WordCount \
  --executor-memory 512M --total-executor-cores 2 \
  ./spark-wordcount-java.jar /wordcount
```
---
3) spark scala版本示例代码，[跳转至github地址](https://github.com/Cooze/hadoop-sample/spark-wordcount-scala)<br>
将git上的程序编译之后上传至hadoop服务器，键入如下命令将spark程序提交到spark中运行：
```
spark-submit --master spark://localhost:7077 \
 --name WordCount --class org.cooze.hadoop.spark.wordcount.scala.WordCount \
  --executor-memory 512M --total-executor-cores 2 \
  ./spark-wordcount-scala.jar /wordcount
```
---
4) spark python版本示例代码，[跳转至github地址](https://github.com/Cooze/hadoop-sample/spark-wordcount-python)<br>
将git上的程序编译之后上传至hadoop服务器，键入如下命令将spark程序提交到spark中运行：
```
spark-submit --executor-memory 512M --total-executor-cores 2 \
spark-wordcount-python.py 
```
