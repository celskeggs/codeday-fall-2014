package server.readout;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import server.data.IMonitor;
import server.data.KeyValueStore;
import server.logger.Logger;

public class Readout {
	public static void start(KeyValueStore store) {
		final JFrame jframe = new JFrame("Server Readout");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JPanel main = new JPanel();
		final JList<Pair> jl = new JList<>();
		final DefaultListModel<Pair> model = new DefaultListModel<>();
		jl.setModel(model);
		final HashMap<String, Pair> strs = new HashMap<String, Pair>();
		store.setMonitor(new IMonitor() {
			boolean updated = false;

			@Override
			public void set(String key, Object value) {
				if (!strs.containsKey(key)) {
					updated = true;
					strs.put(key, new Pair(key, value));
				} else {
					if (strs.get(key).value != value) {
						updated = true;
						strs.get(key).value = value;
					}
				}
			}

			@Override
			public void remove(String key) {
				if (strs.containsKey(key)) {
					updated = true;
					strs.remove(key);
				}
			}

			@Override
			public void commit() {
				if (updated) {
					final Pair[] strv = strs.values().toArray(new Pair[strs.size()]);
					Arrays.sort(strv);
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							model.removeAllElements();
							for (Pair p : strv) {
								model.addElement(p);
							}
						}
					});
					jl.repaint();
					main.repaint();
					jframe.repaint();
				}
			}
		});
		main.add(jl);
		jframe.setContentPane(main);
		jframe.pack();
		jframe.setSize(640, 480);
		jframe.setVisible(true);
		store.setAllDirty();
	}
}
