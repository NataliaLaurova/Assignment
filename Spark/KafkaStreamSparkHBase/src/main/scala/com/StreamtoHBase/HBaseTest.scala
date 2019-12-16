package com.StreamtoHBase

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.Cell
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.{HBaseAdmin, HTable, Put, TableDescriptorBuilder}
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.{HColumnDescriptor, HTableDescriptor}

object HBaseTest  extends Serializable{

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("HBaseTest")
    val ssc = new StreamingContext(conf, Seconds(2))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092", //not zookeeper only broker server
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "stream_consumer_group_id",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = Array("trump")
    val kafkaStream = KafkaUtils.createDirectStream[String,String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val raw_text = kafkaStream.map(x => scala.util.parsing.json.JSON.parseFull(x.value())
      .get.asInstanceOf[Map[String, Any]]).filter(x => x.get("lang").mkString == "en")

    ///create row with 2 field
    val tweet = raw_text.map(x => (x.get("id").mkString, x.get("text").mkString))

    //change structure to list for send to dataframe
    val new_line = tweet.map(x => (x._1, x._2))

    //Hbase
    //ssc.checkpoint("hdfs://localhost:9000/kafkaHBase")

    def toHBase(row: (_,_)): Unit ={
      val tableName = "twitter"
      val config = HBaseConfiguration.create()
      config.set(TableInputFormat.INPUT_TABLE,tableName)
      val admin = new HBaseAdmin(config)
      //hConf.set("hbase.zookeeper.quorum", "localhost:2181")
//      admin.isTableAvailable(tableName)
//      admin.listTables
      val hTable = new HTable(config, tableName)
      val tableDescriptor = new TableDescriptorBuilder(tableName)
      admin.createTable(tableDescriptor)
      val thePut = new Put(Bytes.toBytes(row._1.toString))
      thePut.addColumn(Bytes.toBytes("tweets"), Bytes.toBytes("id"), Bytes.toBytes(row._1.toString))
      thePut.addColumn(Bytes.toBytes("tweets"), Bytes.toBytes("text"), Bytes.toBytes(row._2.toString))
      hTable.put(thePut)
    }

    val HBase_insert: Unit = new_line.foreachRDD(rdd => if (!rdd.isEmpty()) rdd.foreach(toHBase(_)))
    ssc.start()
    ssc.awaitTermination()
  }

}