#!/usr/bin/python

import socket, threading, Queue         

def init(host,port):
    s = socket.socket()        
    host = ("10.251.14.147")
    port = 50000
    s.connect((host, port))

    queue = Queue.Queue()

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
        print "got", out
        return out
    def threadbody():
        assert parse4(getall(4)) == 0xD007D074
        while 1:
            header = getall(6)
            length = parse4(header[0:4])
            typeid = parse2(header[4:6])
            data = getall(length)
            queue.put ((typeid, data))
    thread = threading.Thread(target=threadbody)
    thread.start()

    return queue

