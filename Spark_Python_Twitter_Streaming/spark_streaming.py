from pyspark import SparkContext
from pyspark.streaming import StreamingContext
from pyspark.streaming.kafka import KafkaUtils
import json

# define inputs
master = "local[*]"
appName = "twitterStreamContent"
batch_interval = 10 # second(s)
host = "localhost"
port = 9092
topic = {'trump' : 1} # {topic : partitions}

if __name__ == "__main__":

	# create batch stream
	sc = SparkContext(master, appName)
	ssc = StreamingContext(sc, batch_interval)

	# set Kafka DStream
	zookeeper_quorum = ":".join([host, str(port)])
	consumer_group_id = "spark-streaming"

	kafkaStream = KafkaUtils.createStream(ssc, zookeeper_quorum, consumer_group_id, topic)

	# apply transformations
	parsed = kafkaStream.map(lambda j: json.loads(j[1]))
	text = parsed.map(lambda tweet: tweet["text"])
	text.pprint()

	# start streaming context
	ssc.start()

	# stop streaming context (auto)
	ssc.awaitTermination()
