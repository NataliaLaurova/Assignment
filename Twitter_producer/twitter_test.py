from tweepy.streaming import StreamListener
from tweepy import OAuthHandler
from tweepy import Stream
from kafka import SimpleProducer, KafkaClient

access_token = "714804243371319296-emEQMO6N5kP7iHW7hBqcT27qnvHs1mP"
access_token_secret =  "X3yU1lIRfst73wGqcT6uimvsVrRxI0Mf8symf2YOW08cI"
consumer_key =  "dZuBNJ30MTqMptFLPRIDZW10r"
consumer_secret =  "w13ZgbBTLjziltB4OJbAbyNos2EbPAMwymVLEcX3wwv4Qdciu1"

class StdOutListener(StreamListener):
    def on_data(self, data):
        producer.send_messages("trump", data.encode('utf-8'))
        print (data)
        return True
    def on_error(self, status):
        print (status)

kafka = KafkaClient("localhost:9092")
producer = SimpleProducer(kafka)
l = StdOutListener()
auth = OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)
stream = Stream(auth, l)
stream.filter(track="trump")