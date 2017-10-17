### hadoop+hbase 伪分布式安装


基本环境及软件：

|软件版本 | 软件包|
|:---:|:---:|
|centos-6.x ||   
|JDK-1.8 | jdk-8u112-linux-x64.tar.gz|  
|hadoop-2.7|hadoop-2.7.4.tar.gz|
|hbase-1.2.6|hbase-1.2.6-bin.tar.gz|

java基础环境和hadoop为分布式安装[请点击，跳转至"hadoop+spark 伪分布式安装"](https://github.com/Cooze/hadoop-sample/blob/master/hadoop_spark_install.md)，参考`解压&安装hadoop`小节。<br>

#### HBase安装：
1、键入如下命令将HBase软件包解压至指定目录：
```
tar -zxvf hbase-1.2.6-bin.tar.gz -C /usr/local/
```
键入如下命令，切换至HBase配置文件所在路径：
```
cd /usr/local/hbase-1.2.6/conf
```

2、编辑`hbase-env.sh`HBase环境变量配置文件，填入如下内容：
```
export JAVA_HOME=/usr/local/jdk1.8.0_112/
```
3、编辑`hbase-site.xml`配置文件，填写内容如下：
```xml
<configuration>
    <!--zookeeper集群地址,有多个用","隔开-->
    <property>
        <name>hbase.zookeeper.quorum</name>
        <value>localhost</value>
    </property>
    <!--zookeeper数据目录-->
    <property>
        <name>hbase.zookeeper.property.dataDir</name>
        <value>/opt/hadoop/zookeeper</value>
    </property>
    <!--开启集群分布式模式-->
    <property>
        <name>hbase.cluster.distributed</name>
        <value>true</value>
    </property>
    <!--HBase 数据存放到 hdfs上-->
    <property>
        <name>hbase.rootdir</name>
        <value>hdfs://localhost:9000/hbase</value>
    </property>
    <!-- hbase WEB UI 端口配置  -->
    <property>
        <name>hbase.master.info.port</name>
        <value>60010</value>
    </property>
</configuration> 
```
4、启动HBase
```
/usr/local/hbase-1.2.6/bin/start-hbase.sh
```
5、检查HBase，是否成功安装：
检查hdfs上是否有/hbase目录.
```
[root@localhost bin]# hadoop fs -ls /hbase
Found 9 items
drwxr-xr-x   - root supergroup          0 2017-10-17 03:19 /hbase/.tmp
drwxr-xr-x   - root supergroup          0 2017-10-17 07:19 /hbase/MasterProcWALs
drwxr-xr-x   - root supergroup          0 2017-10-17 03:19 /hbase/WALs
drwxr-xr-x   - root supergroup          0 2017-10-17 03:32 /hbase/archive
drwxr-xr-x   - root supergroup          0 2017-10-17 03:19 /hbase/corrupt
drwxr-xr-x   - root supergroup          0 2017-10-16 07:06 /hbase/data
-rw-r--r--   1 root supergroup         42 2017-10-16 07:05 /hbase/hbase.id
-rw-r--r--   1 root supergroup          7 2017-10-16 07:05 /hbase/hbase.version
drwxr-xr-x   - root supergroup          0 2017-10-17 07:30 /hbase/oldWALs
```
6、开启centos防火墙的`60010`端口，允许外网访问HBase WEB UI:
编辑防火墙配置文件`/etc/sysconfig/iptables`添加如下配置：
```
-A INPUT -m state --state NEW -m tcp -p tcp --dport 60010 -j ACCEPT
```
重启防火墙：
```
service iptables restart
```
访问hbase的web ui页面：
```
http://{your_ip_address}:60010
```
7、熟悉HBase java API<br>
Hbase java api的基本操作示例代码，[跳转至github地址](https://github.com/Cooze/hadoop-sample/tree/master/hbase-java-opt-demo)<br>
将git上的程序编译之后上传至HBase服务器，键入如下命令运行程序：
```
java -cp hbase-java-opt-demo-1.0-SNAPSHOT-jar-with-dependencies.jar \
 org.cooze.hadoop.hbase.java.demo.HBaseCURD
```
运行示例：<br>
创建一个表，表名为hello，拥有一个键族foo；<br>
建表：
```
java -cp hbase-java-opt-demo-1.0-SNAPSHOT-jar-with-dependencies.jar \
org.cooze.hadoop.hbase.java.demo.HBaseCURD create hello foo
```
---
插入数据：
```
java -cp hbase-java-opt-demo-1.0-SNAPSHOT-jar-with-dependencies.jar \
 org.cooze.hadoop.hbase.java.demo.HBaseCURD insert hello 1 foo test nihao
```
---
查询数据：
``` 
java -cp hbase-java-opt-demo-1.0-SNAPSHOT-jar-with-dependencies.jar \
 org.cooze.hadoop.hbase.java.demo.HBaseCURD scan hello
```
OK,结束！

