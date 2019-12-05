package com.twitter_stream

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter.TwitterUtils
import scala.util.parsing.json._

object TwitterStream {
  def main(args: Array[String]): Unit = {

    if (args.length < 4 ) {
      System.err.println("Usage: TwitterStream <consumer key> <consumer secret> " +
        "<access token> <access token secret>") // [<filters>]")
      System.exit(1)
    }

    //StreamingExamples.setStreamingLogLevels()

    val Array(consumerKey, consumerSecret, accessToken, accessTokenSecret) = args.take(4)
    //val filters = args.takeRight(args.length - 4)

    //Set system properties

    System.setProperty("twitter4j.oauth.consumerKey", consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)

    StreamingExamples.setStreamingLogLevels()


    val sparkConf = new SparkConf().setAppName("TwitterStream").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    //val stream = TwitterUtils.createStream(ssc, None) //Dstream

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("twitter")
    val kafkaStream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    println(kafkaStream)

    kafkaStream.foreachRDD { rdd =>
      rdd.foreach { record =>
        val value = record.value()
        val tweet = scala.util.parsing.json.JSON.parseFull(value)
        val map:Map[String,Any] = tweet.get.asInstanceOf[Map[String, Any]]
        println(map.get("text"))
      }
    }

    ssc.start()
    ssc.awaitTermination()
  }
}
