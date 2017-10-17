package org.cooze.hadoop.hbase.java.demo;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * @author cooze
 * @version 2.0.0 创建于 2017/10/17
 */
public class HBaseCURD {

    private static Configuration conf;
    private static Connection connection;

    static {
        try {
            //获取配置对象
            conf = HBaseConfiguration.create();

            //初始化配置参数
            //            conf.set("hbase.rootdir", "hdfs://localhost:9000/hbase");
            conf.set("hbase.zookeeper.quorum", "localhost");//如果是集群,则用","隔开多个ip即可
            conf.set("hbase.zookeeper.property.clientport", "2181");

            conf.setInt("hbase.rpc.timeout", 20000);
            conf.setInt("hbase.client.operation.timeout", 30000);
            conf.setInt("hbase.client.scanner.timeout.period", 20000);

            //创建连接
            connection = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            //打印日志
            e.printStackTrace();
        }
    }


    /**
     * HBase建表操作
     *
     * @param tableName    表名
     * @param columnFamily 列族
     * @throws Exception 异常信息
     */
    public static void createTable(String tableName, String... columnFamily) throws Exception {
        TableName tn = TableName.valueOf(tableName);
        Admin admin = connection.getAdmin();

        if (admin.tableExists(tn)) {
            throw new Exception("表名已经被创建");
        }

        HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);

        if (columnFamily != null && columnFamily.length > 0) {
            for (String col : columnFamily) {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(col);
                hTableDescriptor.addFamily(hColumnDescriptor);
            }
        }
        admin.createTable(hTableDescriptor);
        admin.close();
    }

    /**
     * HBase删除表操作
     *
     * @param tableName 表名
     */
    public static void deleteTable(String tableName) throws Exception {
        TableName tn = TableName.valueOf(tableName);
        Admin admin = connection.getAdmin();
        //表不存在,则不删除
        if (!admin.tableExists(tn)) {
            return;
        }
        admin.disableTable(tn);
        admin.deleteTable(tn);
        admin.close();
    }

    /**
     * HBase插入数据
     *
     * @param tableName    表名
     * @param rowkey       行键
     * @param columnFamily 列族
     * @param column       列
     * @param value        值
     * @throws Exception 异常
     */
    public static void insterRow(String tableName, String rowkey, String columnFamily, String column, String value) throws Exception {

        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(Bytes.toBytes(rowkey));
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
        table.put(put);

        table.close();
    }

    /**
     * HBase删除指定行键的整条数据
     *
     * @param tableName 表名
     * @param rowkey    行键
     * @throws IOException 异常
     */
    public static void deleteRow(String tableName, String rowkey) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowkey));
        //删除指定列族
        //delete.addFamily(Bytes.toBytes(colFamily));
        //删除指定列
        //delete.addColumn(Bytes.toBytes(colFamily),Bytes.toBytes(col));
        table.delete(delete);
        table.close();
    }

    /**
     * HBase查找数据
     *
     * @param tableName    表明
     * @param rowkey       行键
     * @param columnFamily 列族
     * @param column       列
     * @throws IOException
     */
    public static void getData(String tableName, String rowkey, String columnFamily, String column) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowkey));

        //获取指定列数据
        if (StringUtils.isNotEmpty(column)) {
            get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
        }

        Result result = table.get(get);
        formatPrint(result);
        table.close();
    }

    /**
     * HBase扫描查找数据
     *
     * @param tableName 表名
     * @throws IOException 异常
     */
    public static void scanData(String tableName) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();

        ResultScanner resultScanner = table.getScanner(scan);
        for (Result result : resultScanner) {
            formatPrint(result);
        }
        table.close();
    }

    /**
     * HBase 根据行键更新数据
     *
     * @param tableName    表名
     * @param rowkey       行键
     * @param columnFamily 列族
     * @param column       列
     * @param value        值
     * @throws Exception 异常
     */
    public static void update(String tableName, String rowkey, String columnFamily, String column, String value) throws Exception {
        if (StringUtils.isEmpty(rowkey)
                || StringUtils.isEmpty(columnFamily)
                || StringUtils.isEmpty(column)
                || isNull(value)) {
            throw new Exception("参数错误！");
        }
        //获取HBase表对象
        Table table = connection.getTable(TableName.valueOf(tableName));

        Put put = new Put(Bytes.toBytes(rowkey));

        put.addImmutable(Bytes.toBytes(columnFamily),
                Bytes.toBytes(column), Bytes.toBytes(value));
        table.put(put);
        System.out.println("data Updated");
        table.close();
    }

    /**
     * 格式化打印输出
     *
     * @param result 查找结果
     */
    private static void formatPrint(Result result) {
        if (result == null) {
            return;
        }
        System.out.println("============================================================================================");
        for (Cell cell : result.rawCells()) {
            System.out.println("行键:" + new String(CellUtil.cloneRow(cell)) + " ");
            System.out.println("时间戳:" + cell.getTimestamp() + " ");
            System.out.println("列族:" + new String(CellUtil.cloneFamily(cell)) + " ");
            System.out.println("列名:" + new String(CellUtil.cloneQualifier(cell)) + " ");
            System.out.println("值:" + new String(CellUtil.cloneValue(cell)) + " ");
            System.out.println("--------------------------------------------------------------------------------------------");
        }

        System.out.println("============================================================================================");

    }


    private static boolean isNull(Object obj) {
        return obj == null;
    }

    private static void showUsage() {
        System.out.println("建表命令\t\t create tableName columnFamily ... ");
        System.out.println("删表命令\t\t delTable  tableName");
        System.out.println("插入数据\t\t insert tableName rowkey columnFamily column value");
        System.out.println("删除数据\t\t delete tableName rowkey");
        System.out.println("更新数据\t\t put tableName rowkey columnFamily column value");
        System.out.println("查找数据\t\t get tableName rowkey columnFamily column");
        System.out.println("扫描数据\t\t scan tableName");
    }


    public static void main(String[] args) throws Exception {
        if (args == null || args.length < 1) {
            showUsage();
            return;
        }
        String[] params = null;
        int argsLen = args.length;
        if (argsLen >= 2) {
            params = new String[argsLen - 1];
            System.arraycopy(args, 1, params, 0, argsLen - 1);
        } else {
            params = new String[0];
        }

        String cmd = args[0].toLowerCase();
        switch (cmd) {
            case "create":
                int paramsLen = params.length;
                if (paramsLen < 2) {
                    showUsage();
                    break;
                }
                String[] createTableParams = new String[paramsLen - 1];
                System.arraycopy(params, 1, createTableParams, 0, paramsLen - 1);
                createTable(params[0], createTableParams);
                break;
            case "delTable":
                deleteTable(params[0]);
                break;
            case "insert": {
                if (params.length < 5) {
                    showUsage();
                    break;
                }
                insterRow(params[0], params[1], params[2], params[3], params[4]);
            }
            break;
            case "delete": {
                if (params.length < 2) {
                    showUsage();
                    break;
                }
                deleteRow(params[0], params[1]);
            }
            break;
            case "put": {
                if (params.length < 5) {
                    showUsage();
                    break;
                }
                update(params[0], params[1], params[2], params[3], params[4]);
            }
            break;
            case "get": {
                if (params.length < 4) {
                    showUsage();
                    break;
                }
                getData(params[0], params[1], params[2], params[3]);
            }
            break;

            case "scan": {
                scanData(params[0]);
            }
            break;

            default:
                showUsage();
                break;
        }
    }
}
