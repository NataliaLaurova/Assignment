package com.kafkastream
import java.io._
import scala.io.Source

object testAPI {

  def main(args: Array[String]) {
    //println(
    val newline = scala.io.Source.fromURL("https://api.barcodelookup.com/v2/products?search=fruits&formatted=y&key=yxeluxk3ok8vc7r80wr5oxo4mjnsj1")
    println(newline.mkString) //.codec.encoder)
    //println(scala.util.parsing.json.JSON.parseFull(newline.mkString).get.asInstanceOf[Map[String, Any]].map(x => x("barcode_number")) )

    ///scala.io.Source.fromURL("http://google.com").mkString//)
  }
}

