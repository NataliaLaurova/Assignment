from kafka import SimpleProducer, KafkaClient
from time import sleep
import urllib.request
import json
import pprint
#from authentication import TOKEN, URL

# get user input
SEARCH = input("Enter name of product: ")

# construct url
api_key = "yxeluxk3ok8vc7r80wr5oxo4mjnsj1"
url = "https://api.barcodelookup.com/v2/products?search=" + SEARCH + "s&formatted=y&key=" + api_key
#FREQ = input("Enter request frequency (req/min): ")

with urllib.request.urlopen(url) as url:
	data = json.loads(url.read().decode())

# Kafka Config
LOCALHOST = "localhost:9092"		# connect to
TOPIC = "trump"			# broadcast to topic
client = KafkaClient(LOCALHOST)		# define client
producer = SimpleProducer(client)	# define producer

l = data["products"]
t = []
for e in l:
	b = e["barcode_number"]
	p = e["product_name"]
	for s in e["stores"]:
		sn = s["store_name"]
		sp = s["store_price"]
		t.append({"product" : SEARCH, "product_name": p, "barcode" : b, "stores_name" : sn, "store_price" : sp})

for e in t:
	print(e, "\n")

# send to producer
producer.send_messages(TOPIC, str(t).replace("\'","\"").encode("utf-8"))

# done
