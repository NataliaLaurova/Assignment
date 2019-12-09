package com.StreamtoHBase

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.spark._
import org.apache.hadoop.hbase.spark.HBaseRDDFunctions._
import org.apache.spark.sql.execution.datasources.hbase
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog



object SparkStreamingtoHBase {
  def main(args: Array[String]) {

    EnableLog.setStreamingLogLevels()

    val conf = new SparkConf().setAppName("SparkStreamingtoHBase").setMaster("local[*]")
    // Setting the batch interval over which we perform our pollution average calculation
    val stc = new StreamingContext(conf, Seconds(2))

    val ss = SparkSession
      .builder
      .appName("SparkStreamingtoHBase")
      //.config("spark.hbase.host", "hostname")
      //.config("zookeeper.znode.parent", "/hbase-unsecure")
      //.config("hbase.master", "hostname"+":60001")
      .getOrCreate()

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092", //not zookeeper only broker server
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "stream_consumer_group_id",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    // Creating a stream to read from Kafka
    val topics = Array("trump")

    val stream = KafkaUtils.createDirectStream[String, String](
      stc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    val schema = StructType(
      List(
        StructField("id", StringType, nullable = true),
        StructField("text", StringType, nullable = true)
      )
    )

    def catalog = s"""{
                     |"table":{"namespace":"default", "name":"Tweets"},
                     |"rowkey":"key",
                     |"columns":{
                     |"rowkey":{"cf":"rowkey", "col":"key", "type":"string"},
                     |"tweetid":{"cf":"Id", "col":"Id", "type":"string"},
                     |"textoftweet":{"cf":"Text", "col":"Text", "type":"string"}
                     |}
                     |}""".stripMargin

    /// parsing raw tweet and create map with raw data
    val raw_text = stream.map(x => scala.util.parsing.json.JSON.parseFull(x.value())
      .get.asInstanceOf[Map[String, Any]]).filter(x => x.get("lang").mkString == "en")

    ///create rdd with 2 field
    val tweet = raw_text.map(x => (x.get("id").mkString, x.get("text").mkString))

    //change structure to list for send to dataframe
    val new_line = tweet.map(x => Row(x._1, x._2))
    //new_line

    new_line.foreachRDD { rdd =>
      val df_raw = ss.createDataFrame(rdd, schema)
      //df.show()
      //df = df_raw.write.options(Map(HBaseTableCatalog.tableCatalog -> catalog, HBaseTableCatalog.newTable -> "1")).format("org.apache.spark.sql.execution.datasources.hbase").save()

      //val df = withCatalog(catalog)
      //df.show()
    }

    stc.start()
    stc.awaitTermination()
  }
}