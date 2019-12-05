package com.enhanceit

import java.util.Properties

import org.apache.spark.streaming.dstream.DStream._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming._
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object TwitterStream {

  val conf = new SparkConf().setMaster("local[*]").setAppName("TwitterStream")
  val sc = new SparkContext(conf)
  //create context
  val ssc = new StreamingContext(sc, Seconds(10))

  // values of Twitter API.
  val consumerKey = "dZuBNJ30MTqMptFLPRIDZW10r"
  val consumerSecret = "w13ZgbBTLjziltB4OJbAbyNos2EbPAMwymVLEcX3wwv4Qdciu1"
  val accessToken = "714804243371319296-emEQMO6N5kP7iHW7hBqcT27qnvHs1mP"
  val accessTokenSecret = "X3yU1lIRfst73wGqcT6uimvsVrRxI0Mf8symf2YOW08cI"

  //Connection to Twitter API
  val cb = new ConfigurationBuilder
  cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret)

  val auth = new OAuthAuthorization(cb.build)
  val tweets = TwitterUtils.createStream(ssc, Some(auth))
  val englishTweets = tweets.filter(_.getLang() == "en")

  val statuses = englishTweets.map(status => (status.getText(),status.getUser.getName(),status.getUser.getScreenName(),status.getCreatedAt.toString))

  statuses.foreachRDD { (rdd, time) =>

    rdd.foreachPartition { partitionIter =>
      val props = new Properties()
      val bootstrap = "localhost:9092"
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("bootstrap.servers", bootstrap)
      val producer = new KafkaProducer[String, String](props)
      partitionIter.foreach { elem =>
        val dat = elem.toString()
        val data = new ProducerRecord[String, String]("trump", null, dat) // "trump" is the name of Kafka topic
        producer.send(data)
      }
      producer.flush()
      producer.close()
    }
  }
  ssc.start()
  ssc.awaitTermination()
}
