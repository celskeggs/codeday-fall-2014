#!/usr/bin/python

import socket, threading, decode, Queue, hashlib, sys

def getall(length, sock):
    out = ""
    while len(out) < length:
        data = sock.recv(length - len(out))
        if data:
            out += data
        else:
            print "Server closed connection."
            sys.exit()
    return out
def encode2(x):
    return chr((x >> 8) & 0xFF) + chr(x & 0xFF)
def encode4(x):
    return chr((x >> 24) & 0xFF) + chr((x >> 16) & 0xFF) + chr((x >> 8) & 0xFF) + chr(x & 0xFF)
def parse2(x):
    return (ord(x[0]) << 8) | ord(x[1])
def parse4(x):
    return (ord(x[0]) << 24) | (ord(x[1]) << 16) | (ord(x[2]) << 8) | ord(x[3])
def ask_route_server():
	ls = socket.socket()
	oldtimeout = ls.gettimeout()
	ls.settimeout(3)
	try:
		ls.connect(("10.251.14.147", 50001))
	except socket.error:
		try:
			ls.connect(("127.0.0.1", 50001))
		except socket.error:
			ls.settimeout(oldtimeout)
			try:
				ls.connect((raw_input("Enter the route server or LiteServer IP address> "), 50001))
			except socket.error:
				print "Route server or LiteServer not contacted."
				raw_input("Press enter...")
				sys.exit()
	ls.settimeout(oldtimeout)
	ls.send(encode4(0xDEADBEEF))
	b = (getall(1, ls) == 0)
	len = parse2(getall(2, ls))
	data = getall(len, ls)
	if b:
		print "Cannot get server: " + data
		sys.exit()
	else:
		print "Server provided address: " + data
		host, port = data.split(":")
		return host, int(port)

s = socket.socket()
host, port = ask_route_server()
#host = ("10.251.14.127")
#port = 50000
s.connect((host, port))

dictionary = {}
chatlines = Queue.Queue()
local_id = -2

s.send(encode4(0xd007d074))

def threadbody():
    assert parse4(getall(4, s)) == 0xD007D074
    while 1:
        header = getall(6, s)
        length = parse4(header[0:4])
        typeid = parse2(header[4:6])
        data = getall(length, s)
        if typeid == 0x0102:
			if data in dictionary:
				del dictionary[data]
        elif typeid == 0x0204:
            namelen = ord(data[0])
            key = data[1:namelen+1]
            body = data[namelen+1:]
            dictionary[key] = decode.decode(body)
        elif typeid == 0x0306:
            chatlines.put(data)
        elif typeid == 0x0408:
            global local_id
            local_id = ord(data[0])
        else:
            raise Exception("unhandled data command: %d" % typeid)
def nextline():
    try:
        return chatlines.get_nowait()
    except Queue.Empty:
        return None
_sentinel = object()
def my(prefix, default=_sentinel):
	key = "%s.%d" % (prefix, local_id)
	if default == _sentinel:
		return dictionary[key]
	else:
		return dictionary.get(key, default)
thread = threading.Thread(target=threadbody)
thread.daemon = True
thread.start()

commands = ["hello", "enter", "attack", "chat", "name", "class", "wait"]

def sendraw(typeid,data):
    length = len(data)
    s.send(encode4(length) + encode2(typeid) + data)
def send(cmd, data):
    sendraw(commands.index(cmd), decode.encode(data))
digest = hashlib.sha512()
def dohash(name):
	with open(name) as f1:
		while True:
			x = f1.read()
			if x == "": break
			digest.update(x)
dohash("interpreter.py")
dohash("main.py")
dohash("client.py")
send("hello", digest.hexdigest())

def close():
    s.close()

