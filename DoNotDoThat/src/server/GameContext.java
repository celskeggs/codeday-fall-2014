package server;

import server.data.KeyValueStore;

public class GameContext {
	public final KeyValueStore storage = new KeyValueStore();

	public void processGame(ServerContext serverContext) {
		boolean[] out = serverContext.listPlayers();
		int count = 0, ready = 0;
		for (int i = 0; i < out.length; i++) {
			if (out[i]) {
				count++;
				Boolean b = (Boolean) storage.get("isready." + i);
				if (b != null && b) {
					ready++;
				}
			}
		}
		if (count >= 2 && count == ready) {
			
		}
	}

	public void initGame() {
		storage.put("mode.isinlobby", true);
	}
}
