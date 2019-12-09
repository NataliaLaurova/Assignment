//package com.StreamtoHBase
//
//import org.apache.spark
//import org.apache.spark.sql.execution.datasources.hbase._
//import org.apache.spark.{SparkConf, SparkContext}
//import org.apache.spark.sql.{Row, SQLContext, SQLImplicits, SaveMode, SparkSession}
//import org.apache.spark.sql.types.{StringType, StructField, StructType}
//import org.apache.hadoop.hbase.HBaseConfiguration
//import org.apache.hadoop.hbase.{HColumnDescriptor, HTableDescriptor}
//import org.apache.hadoop.hbase.mapreduce.TableInputFormat
//import org.apache.hadoop.hbase.client.HBaseAdmin
//import org.apache.spark.streaming.{Seconds, StreamingContext}
//import org.apache.hadoop.hbase.spark._
//import org.apache.hadoop.hbase.spark.HBaseRDDFunctions._
//import org.apache.spark.sql.execution.datasources.hbase
//
//
//object TestHBase {
//  def main(args: Array[String]): Unit = {
//    EnableLog.setStreamingLogLevels()
//
//    val conf = new SparkConf().setAppName("SparkStreamingtoHBase").setMaster("local[*]")
//    // Setting the batch interval over which we perform our pollution average calculation
//
//    //val stc = new StreamingContext(conf, Seconds(2))
//
//    val ss = SparkSession
//      .builder
//      .appName("SparkStreamingtoHBase")
//      .master("local[*]")
//      //.config("spark.hbase.host", "hostname")
//      //.config("zookeeper.znode.parent", "/hbase-unsecure")
//      //.config("hbase.master", "hostname"+":60001")
//      .getOrCreate()
//
//    def catalog = s"""{
//                     |"table":{"namespace":"default", "name":"Tweet"},
//                     |"rowkey":"key",
//                     |"columns":{
//                     |"rowkey":{"cf":"rowkey", "col":"key", "type":"string"},
//                     |"Id":{"cf":"Id", "col":"Id_of_tweet", "type":"string"},
//                     |"Text":{"cf":"Text", "col":"Text_of_tweet", "type":"string"},
//                     |}
//                     |}""".stripMargin
//
//    val schema = StructType(
//      List(
//        StructField("rowkey", StringType, nullable = true),
//        StructField("id", StringType, nullable = true),
//        StructField("text", StringType, nullable = true)
//      )
//    )
//    case class TweetRecord(   rowkey: String,
//                              Id: String,
//                              Text: String
//                            )
//
//    val newContact = TweetRecord("1", "10948283520", "Test tweet")
//
//    val newData = new Array[TweetRecord](1)
//    newData(0) = newContact
//
//    val record = ss.sparkContext.parallelize(newData).map(x => Row(x.rowkey,x.Id,x.Text)) //.map(x => TweetRecord(x.rowkey,x.Id,x.Text)) //.map(x => (x.rowkey,x.Id,x.Text))
//    println(record)
//
//    //ss.createDataFrame(record, schema)
////      .write.options(Map(HBaseTableCatalog.tableCatalog -> catalog, HBaseTableCatalog.newTable -> "1"))
////      .format("org.apache.spark.sql.execution.datasources.hbase")
////      .save()
//
////    val tableName = "test"
////    val config = HBaseConfiguration.create()
////    config.set(TableInputFormat.INPUT_TABLE,tableName)
////    val admin = new HBaseAdmin(config)
////
////    admin.isTableAvailable(tableName)
////    admin.listTables
////
////    val tableDescriptor = new HTableDescriptor(tableName)
////    tableDescriptor.addFamily(new HColumnDescriptor("cf".getBytes()))
////    admin.createTable(tableDescriptor)
//
////  stc.start()
////  stc.awaitTermination()
//  }
//}