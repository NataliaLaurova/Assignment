from tweepy.streaming import StreamListener
from tweepy import OAuthHandler
from tweepy import Stream
from kafka import SimpleProducer, KafkaClient

access_token = "714804243371319296-emEQMO6N5kP7iHW7hBqcT27qnvHs1mP"
access_token_secret =  "X3yU1lIRfst73wGqcT6uimvsVrRxI0Mf8symf2YOW08cI"
consumer_key =  "dZuBNJ30MTqMptFLPRIDZW10r"
consumer_secret =  "w13ZgbBTLjziltB4OJbAbyNos2EbPAMwymVLEcX3wwv4Qdciu1"

# Twitter Authentication
authentication = OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
authentication.set_access_token(ACCESS_TOKEN, TOKEN_SECRET)

# Twitter Stream Listener
Localhost = "localhost:9092"	# connect to
Topic = "trump"			# filter by topic

# create Kafka Listener
class KafkaListener(StreamListener):
    
	def on_data(self, data):
		producer.send_messages(Topic, data.encode('utf-8'))
		print(data)
		return True

	def on_error(self, status):
		print(status)

# define client
client = KafkaClient(Localhost)

# define producer
producer = SimpleProducer(client)

# define listener
listener = KafkaListener()

# set stream & track HASHTAG
stream = Stream(authentication, listener)
