name := "Twitter_Streaming"

version := "0.1"

scalaVersion := "2.11.0"

//Libraries

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.4"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.4"

libraryDependencies += "org.apache.bahir" %% "spark-streaming-twitter" % "2.0.0"
// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming-twitter
//libraryDependencies += "org.apache.spark" %% "spark-streaming-twitter" % "1.6.3"

// https://mvnrepository.com/artifact/org.twitter4j/twitter4j-core
libraryDependencies += "org.twitter4j" % "twitter4j-core" % "3.0.6"

// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.4.4"

// https://mvnrepository.com/artifact/com.google.code.gson/gson
libraryDependencies += "com.google.code.gson" % "gson" % "2.7"

// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming-kafka-0-10
// libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.4.4"

// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming-kafka-0-10
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.4.4" excludeAll(
  ExclusionRule(organization = "org.spark-project.spark", name = "unused"),
  ExclusionRule(organization = "org.apache.hadoop"),
  ExclusionRule(organization = "org.apache.spark", name = "spark-streaming")
)