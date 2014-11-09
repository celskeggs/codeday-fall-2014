package server;

import server.data.KeyValueStore;

public class GameContext {
	public final KeyValueStore storage = new KeyValueStore();
	
	public void processGame(ServerContext serverContext) {
		ClientContext[] out = serverContext.listPlayers();
		int count = 0, ready = 0;
		for (int i = 0; i < out.length; i++) {
			storage.put("connected." + i, out[i] != null);
			if (out[i] != null) {
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
		String cls = (String) storage.get("class." + client.clientId);
		
		storage.put("attack." + client.clientId, cmdname);
	}

	public void sendChatMessage(ClientContext client, String textline) {
		for (ClientContext target : client.serverContext.listPlayers()) {
			target.receivedChatMessage("[" + target.clientId + "] " + textline);
		}
	}
}
