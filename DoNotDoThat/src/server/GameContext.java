package server;

import server.data.KeyValueStore;

public class GameContext {
	public final KeyValueStore storage = new KeyValueStore();
	
	public void processGame(ServerContext serverContext) {
		boolean[] out = serverContext.listPlayers();
		int count = 0, ready = 0;
		for (int i = 0; i < out.length; i++) {
			storage.put("connected." + i, out[i]);
			if (out[i]) {
				count++;
				Boolean b = (Boolean) storage.get("isready." + i);
				if (b != null && b) {
					ready++;
				}
			}
		}
		Boolean b = (Boolean) storage.get("mode.isinlobby");
		if (b != null && b) {
			if (count >= 2 && count == ready) {
				storage.put("mode.isinlobby", false);
				storage.put("mode.countdown", 30);
			}
		} else {
			int countdown = (int) storage.get("mode.countdown");
			//storage.put("mode.countdown", )
		}
	}

	public void initGame() {
		storage.put("mode.isinlobby", true);
	}

	public void queueAttack(ClientContext client, String cmdname) {
		// TODO Auto-generated method stub

	}
}
