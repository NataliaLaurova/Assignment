name := "HBaseStream"

version := "0.1"

scalaVersion := "2.11.0"

// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.1.0"// % "provided"

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.0"

// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.1.0"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.1.0"

// https://mvnrepository.com/artifact/org.apache.kafka/kafka
libraryDependencies += "org.apache.kafka" %% "kafka" % "0.10.2.0"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-client
libraryDependencies += "org.apache.hbase" % "hbase-client" % "1.3.1"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-common
libraryDependencies += "org.apache.hbase" % "hbase-common" % "1.3.1"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-protocol
libraryDependencies += "org.apache.hbase" % "hbase-protocol" % "1.3.1"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-hadoop2-compat
libraryDependencies += "org.apache.hbase" % "hbase-hadoop2-compat" % "1.3.1"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-annotations
libraryDependencies += "org.apache.hbase" % "hbase-annotations" % "1.3.1"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-server
libraryDependencies += "org.apache.hbase" % "hbase-server" % "1.3.1"


