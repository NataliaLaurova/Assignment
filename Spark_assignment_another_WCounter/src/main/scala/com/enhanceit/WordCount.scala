package com.enhanceit

import org.apache.spark.{SparkConf, SparkContext}

object WordCount {

  def main(args: Array[String]): Unit ={
    //Create SparkConfig object SparkContext to initialize Spark
    val inputFile = args(0)
    val outputFile = args(1)
    val conf = new SparkConf().setMaster("local[*]").setAppName("WordCount")
    val sc = new SparkContext(conf)
    val input = sc.textFile(inputFile)
    val words = input.flatMap(_.split(" "))
    val counts = words.map((_,1)).reduceByKey(_+_)
    counts.saveAsTextFile(outputFile)

  }

}












