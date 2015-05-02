package server.readout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import server.ServerContext;
import server.data.IMonitor;
import server.data.KeyValueStore;
import server.logger.LogLevel;
import server.logger.Logger;
import server.logger.LoggingTarget;

public class Readout {
	public static void start(KeyValueStore store, final ServerContext server) {
		final JFrame jframe = new JFrame("Server Readout");
		jframe.setBackground(Color.WHITE);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JPanel main = new JPanel();
		main.setBackground(Color.WHITE);
		main.setLayout(new FlowLayout());
		final JPanel entry = new JPanel();
		entry.setLayout(new GridBagLayout());
		entry.setMinimumSize(new Dimension(200, 20));
		entry.setSize(new Dimension(200, 20));
		entry.setMaximumSize(new Dimension(200, 20));
		final JTextField repl = new JTextField();
		repl.addActionListener(new ActionListener() {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine script = manager.getEngineByName("javascript");
			Bindings b = new SimpleBindings();
			{
				b.put("server", server);
				b.put("game", server.context);
				b.put("store", server.context.storage);
			}
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Logger.info(Objects.toString(script.eval(repl.getText(), b)));
				} catch (Throwable thr) {
					Logger.info("Failed", thr);
				}
				repl.setText("");
			}
		});
		final JTextField text = new JTextField();
		text.setMinimumSize(new Dimension(200, 20));
		text.setSize(200, 20);
		text.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String contents = text.getText();
				if (!contents.isEmpty()) {
					String data = "[SUPREME SERVER MONKEY] " + contents;
					server.context.sendMessage(server, data);
					Logger.info(data);
					text.setText("");
				}
			}
		});
		//entry.
		entry.setSize(200, 20);
		entry.add(repl);
		entry.add(text);
		final JList<String> jinfo = new JList<>();
		final JList<Pair> jleft = new JList<>();
		final JList<Pair> jright = new JList<>();
		final DefaultListModel<String> model_info = new DefaultListModel<>();
		final DefaultListModel<Pair> model_left = new DefaultListModel<>();
		final DefaultListModel<Pair> model_right = new DefaultListModel<>();
		jinfo.setModel(model_info);
		jleft.setModel(model_left);
		jright.setModel(model_right);
		Logger.addTarget(new LoggingTarget() {
			@Override
			public void log(LogLevel level, String message, Throwable throwable) {
				this.log(level, message, throwable == null ? null : throwable.toString());
			}

			@Override
			public void log(final LogLevel level, final String message, final String extended) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						if (model_info.size() > 20) {
							model_info.removeElementAt(0);
							model_info.setElementAt("... logs elised ...", 0);
						}
						model_info.addElement("[" + level + "] " + message + (extended == null ? "" : " [" + extended + "]"));
					}
				});
			}
		});
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
		main.add(entry);
		main.add(jinfo);
		main.add(jleft);
		main.add(jright);
		jframe.setContentPane(main);
		jframe.pack();
		jframe.setSize(640, 480);
		jframe.setVisible(true);
		store.setAllDirty();
	}
}
