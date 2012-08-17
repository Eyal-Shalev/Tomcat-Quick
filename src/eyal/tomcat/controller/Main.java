package eyal.tomcat.controller;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) throws Exception {
		if (!SystemTray.isSupported()) {
			JOptionPane
					.showMessageDialog(null, "System Tray is not supported.");
			System.exit(1);
		}

		SystemTray tray = SystemTray.getSystemTray();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		URL url = Main.class.getResource("trayIcon.png");
		Image image = toolkit.getImage(url);
		toolkit.prepareImage(image, -1, -1, null);

		PopupMenu menu = new PopupMenu();

		MenuItem startTomcat = new MenuItem("Start Tomcat");
		startTomcat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startup();
			}
		});
		menu.add(startTomcat);

		MenuItem stopTomcat = new MenuItem("Stop Tomcat");
		stopTomcat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shutdown();
			}
		});
		menu.add(stopTomcat);

		MenuItem quit = new MenuItem("Quit Tomcat Controller");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shutdown();
				System.exit(0);
			}
		});
		menu.add(quit);
		TrayIcon icon = new TrayIcon(image, "Tomcat Controller", menu);
		icon.setImageAutoSize(true);

		tray.add(icon);
	}

	private static void startup() {
		try {
			Runtime.getRuntime().exec("/usr/local/tomcat/bin/startup.sh", null,
					null);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private static void shutdown() {
		try {
			Runtime.getRuntime().exec("/usr/local/tomcat/bin/shutdown.sh",
					null, null);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

}
