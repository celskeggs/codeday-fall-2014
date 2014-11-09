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
	else:
		raise Exception("unhandled type: %s" % type(x))
