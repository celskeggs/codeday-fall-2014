#!/usr/bin/python

import socket, threading, decode

s = socket.socket()        
host = ("10.251.14.147")
port = 50000
s.connect((host, port))

dictionary = {}

def encode2(x):
    return chr((x >> 8) & 0xFF) + chr(x & 0xFF)
def encode4(x):
    return chr((x >> 24) & 0xFF) + chr((x >> 16) & 0xFF) + chr((x >> 8) & 0xFF) + chr(x & 0xFF)
s.send(encode4(0xd007d074))

def parse2(x):
    return (ord(x[0]) << 8) | ord(x[1])
def parse4(x):
    return (ord(x[0]) << 24) | (ord(x[1]) << 16) | (ord(x[2]) << 8) | ord(x[3])
def getall(length):
    out = ""
    while len(out) < length:
        data = s.recv(length - len(out))
        if data:
            out += data
        else:
            print "Served closed connection."
            sys.exit()
    return out
def threadbody():
    assert parse4(getall(4)) == 0xD007D074
    while 1:
        header = getall(6)
        length = parse4(header[0:4])
        typeid = parse2(header[4:6])
        data = getall(length)
        if typeid == 0x0102:
            del dictionary[data]
        elif typeid == 0x0204:
            namelen = ord(data[0])
            key = data[1:namelen+1]
            body = data[namelen+1:]
            dictionary[key] = decode.decode(body)
        else:
            raise Exception("unhandled data command: %d" % typeid)
thread = threading.Thread(target=threadbody)
thread.start()

commands = ["hello", "enter"]

def sendraw(typeid,data):
    length = len(data)
    s.send(encode4(length) + encode2(typeid) + data)
def send(cmd, data):
    sendraw(commands.index(cmd), decode.encode(data))

