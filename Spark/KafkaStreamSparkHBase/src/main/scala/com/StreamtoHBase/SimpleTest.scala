package com.StreamtoHBase

import org.apache.spark.sql.SparkSession
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.io.ImmutableBytesWritable

object SimpleTest  extends Serializable {
  case class EmpRow(empID:String, name:String, city:String)

  def parseRow(result: Result): EmpRow = {
    val rowkey = Bytes.toString(result.getRow)
    val cfDataBytes = Bytes.toBytes("metadata")

    val d0 = rowkey
    val d1 = Bytes.toString(result.getValue(cfDataBytes, Bytes.toBytes("name")))
    val d2 = Bytes.toString(result.getValue(cfDataBytes, Bytes.toBytes("city")))
    EmpRow(d0, d1, d2)
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Consumer").getOrCreate()

    val hconf = HBaseConfiguration.create()
    hconf.set("hbase.zookeeper.quorum","localhost")
    hconf.set(TableInputFormat.INPUT_TABLE,"emp")

    val hbaseRDD = spark.sparkContext.newAPIHadoopRDD(
      hconf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )

    import spark.implicits._
    val resultRDD = hbaseRDD.map(tuple => tuple._2)
    val empRDD = resultRDD.map(parseRow)
    val empDF = empRDD.toDF

    empDF.show()
  }
}
