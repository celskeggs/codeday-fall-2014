package server.data;

public interface IMonitor {
	public void set(String key, Object value);
	public void remove(String key);
	public void commit();
}
