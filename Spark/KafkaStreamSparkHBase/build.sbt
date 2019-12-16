name := "KafkaStreamSparkHBase"

version := "0.1"

scalaVersion := "2.11.0"

//resolvers += "Hortonworks Repository" at "http://repo.hortonworks.com/content/repositories/releases/"
//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

//Libraries

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.4.4",
  "org.apache.spark" %% "spark-sql" % "2.4.4",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.4.4",
  "org.apache.spark" %% "spark-streaming" % "2.4.4",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2")

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7"

libraryDependencies ++= Seq(
  //"org.apache.hbase" % "hbase-client" % "2.2.2",
  "org.apache.hbase.connectors.spark" % "hbase-spark" % "1.0.0",
  "com.hortonworks" % "shc-core" % "1.1.1-2.1-s_2.11"
)

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-client
libraryDependencies += "org.apache.hbase" % "hbase-client" % "2.1.0"

// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-common
//libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.8.5"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "2.11-3.5.3"



//libraryDependencies ++= Seq(
//  "org.apache.hadoop" % "hadoop-core" % "1.2.1",
//  "org.apache.hbase" % "hbase" % "1.2.0",
//  "org.apache.hbase" % "hbase-client" % "1.2.0",
//  "org.apache.hbase" % "hbase-common" % "1.2.0",
//  "org.apache.hbase" % "hbase-server" % "1.2.0"
//)

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-client
//libraryDependencies += "org.apache.hbase" % "hbase-client" % "2.2.2"

// https://mvnrepository.com/artifact/org.apache.hbase.connectors.spark/hbase-spark
//libraryDependencies += "org.apache.hbase.connectors.spark" % "hbase-spark" % "1.0.0"