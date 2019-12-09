package com.kafkastream

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark
import org.apache.spark.SparkConf
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SaveMode, SparkSession}
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._


object KafkaReceiver {

  def main(args: Array[String]) {

    StreamingExamples.setStreamingLogLevels()

    val conf = new SparkConf().setAppName("KafkaReceiver").setMaster("local[*]")
    // Setting the batch interval over which we perform our pollution average calculation
    val stc = new StreamingContext(conf, Seconds(2))

    val ss = SparkSession
      .builder
      .appName("KafkaReceiver")
      .config("spark.sql.warehouse.dir", "/user/hive/warehouse")
      .config("hive.metastore.uris", "thrift://localhost:9083")
      .getOrCreate()

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",   //not zookeeper only broker server
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
    /// parsing raw tweet and create map with raw data
    val raw_text = stream.map(x => scala.util.parsing.json.JSON.parseFull(x.value())
      .get.asInstanceOf[Map[String, Any]]).filter(x => x.get("lang").mkString == "en")

    ///create rdd with 2 field
    val tweet = raw_text.map(x => (x.get("id").mkString, x.get("text").mkString))

    //change structure to list for send to dataframe
    val new_line = tweet.map(x => Row(x._1,x._2))
    //new_line

    new_line.foreachRDD { rdd =>
      val df = ss.createDataFrame(rdd, schema)
      //df.createOrReplaceTempView("Tweet")
      df.show()
      //df.write.mode(SaveMode.Append).saveAsTable("default.test")
      //df.printSchema()
      //df.write.saveAsTable("")
    }

    stc.start()
    stc.awaitTermination()
  }
}

