# decode.py
def decode4(x):
	return (ord(x[0]) << 24) | (ord(x[1]) << 16) | (ord(x[2]) << 8) | (ord(x[3]))
def encode4(x):
	return chr((x >> 24) & 0xFF) + chr((x >> 16) & 0xFF) + chr((x >> 8) & 0xFF) + chr(x & 0xFF)
def decode_i(x):
	assert len(x) >= 0, "bad length"
	fb = ord(x[0])
	if fb == 0:
		return None, x[1:]
	elif fb == 1:
		assert len(x) >= 5, "bad length"
		return decode4(x[1:5]), x[5:]
	elif fb == 2:
		assert len(x) >= 2, "bad length"
		return ord(x[1]) != 0x00, x[2:]
	elif fb == 3:
		assert len(x) >= 5, "bad length"
		ln = decode4(x[1:5])
		x = x[5:]
		out = []
		for i in range(ln):
			elem, x = decode_i(x)
			out.append(elem)
		return tuple(out), x
	elif fb == 4:
		assert len(x) >= 5, "bad length"
		count = decode4(x[1:5])
		x = x[5:]
		assert len(x) >= count
		return x[:count], x[count:]
	else:
		raise Exception("unrecognized typeid: %d" % fb)
def decode(x):
	out, rem = decode_i(x)
	assert not rem, "failed to use entire message"
	return out
def encode(x):
	if x == None:
		return "\x00"
	elif type(x) == int:
		return "\x01" + encode4(x)
	elif type(x) == bool:
		return "\x02\x01" if x else "\x02\x00"
	elif type(x) == tuple:
		return "\x03" + encode4(len(x)) + "".join(map(encode, x))
	elif type(x) == str:
		return "\x04" + encode4(len(x)) + x
	elif type(x) == unicode:
		return "\x04" + encode4(len(x)) + x.encode()
	else:
		raise Exception("unhandled type: %s" % type(x))
