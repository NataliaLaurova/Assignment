package com.HBaseStreaming

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{State, StateSpec}
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HTableDescriptor}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.client.{HBaseAdmin, HTable, Put}
import org.apache.log4j.{Level, Logger}

object StreamingSpark {
  def main(args: Array[String]) {

    Logger.getRootLogger.setLevel(Level.ERROR)
    //EnableLog.setStreamingLogLevels()
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val conf = new SparkConf().setMaster("local[*]").setAppName("StreamingSpark")
    val ssc = new StreamingContext(conf, Seconds(2))
    /*
    * Defingin the Kafka server parameters
    */
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean))
    val topics = Array("trump") //topics list

    val kafkaStream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams))

    val raw_text = kafkaStream.map(x => scala.util.parsing.json.JSON.parseFull(x.value())
      .get.asInstanceOf[Map[String, Any]]).filter(x => x.get("lang").mkString == "en")

    ///create row with 2 field
    val tweet = raw_text.map(x => (x.get("id").mkString, x.get("text").mkString))

    //change structure to list for send to dataframe
    val new_line = tweet.map(x => (x._1, x._2))
    //Defining a check point directory for performing stateful operations
    ssc.checkpoint("hdfs://localhost:9000/tmp")
    //val cnt = splits.map(x => (x, 1)).reduceByKey(_ + _).updateStateByKey(updateFunc)

    def toHBase(row: (_, _)) {

      val hConf = new HBaseConfiguration()
      hConf.set("hbase.zookeeper.quorum", "localhost")
      hConf.set("hbase.zookeeper.property.client.port", "2181")
      val tableName = "Twitter"
      val hTable = new HTable(hConf, tableName)
      val tableDescription = new HTableDescriptor(tableName)
      val thePut = new Put(Bytes.toBytes(row._1.toString))
      //thePut.addColumn(Bytes.toBytes("Tweets"), Bytes.toBytes("id"), Bytes.toBytes(row._1.toString))
      thePut.addColumn(Bytes.toBytes("Tweets"), Bytes.toBytes("text"), Bytes.toBytes(row._2.toString))
      hTable.put(thePut)
    }

    val Hbase_inset: Unit = new_line.foreachRDD(rdd => if (!rdd.isEmpty()) rdd.foreach(toHBase(_)))
    ssc.start()
    ssc.awaitTermination()
  }
}