package server.readout;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import server.data.IMonitor;
import server.data.KeyValueStore;

public class Readout {
	public static void start(KeyValueStore store) {
		final JFrame jframe = new JFrame("Server Readout");
		jframe.setBackground(Color.WHITE);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JPanel main = new JPanel();
		main.setBackground(Color.WHITE);
		main.setLayout(new FlowLayout());
		final JList<Pair> jleft = new JList<>();
		final JList<Pair> jright = new JList<>();
		final DefaultListModel<Pair> model_left = new DefaultListModel<>();
		final DefaultListModel<Pair> model_right = new DefaultListModel<>();
		jleft.setModel(model_left);
		jright.setModel(model_right);
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
							model_left.removeAllElements();
							model_right.removeAllElements();
							int i = strv.length / 2;
							for (Pair p : strv) {
								if (i-- < 0) {
									model_right.addElement(p);
								} else {
									model_left.addElement(p);
								}
							}
						}
					});
					jleft.repaint();
					jright.repaint();
					main.repaint();
					jframe.repaint();
				}
			}
		});
		main.add(jleft);
		main.add(jright);
		jframe.setContentPane(main);
		jframe.pack();
		jframe.setSize(640, 480);
		jframe.setVisible(true);
		store.setAllDirty();
	}
}
