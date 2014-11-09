import client

queue = client.init ("10.251.14.147", 50000)

while True: 
	print queue.get()
