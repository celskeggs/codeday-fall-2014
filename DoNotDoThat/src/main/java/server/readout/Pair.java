package server.readout;

public class Pair implements Comparable<Pair> {
	public Object value;
	public final String key;

	public Pair(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	public String toString() {
		return key + " = " + value;
	}

	@Override
	public int compareTo(Pair arg0) {
		return key.compareTo(arg0.key);
	}
}
