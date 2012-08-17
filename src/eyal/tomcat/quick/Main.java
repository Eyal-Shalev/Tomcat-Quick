package eyal.tomcat.quick;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
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
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.AppEvent.PreferencesEvent;

public class Main {

	private Preferences myPreferences = null;

	private static final String DEFAULT_TOMCAT_LOCATION = "/usr/local/tomcat/bin/";

	public static void main(String[] args) throws Exception {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name",
				"Tomcat Quick");
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

		MenuItem restartTomcat = new MenuItem("Restart Tomcat");
		restartTomcat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shutdown();
				startup();
			}
		});
		menu.add(restartTomcat);

		MenuItem quit = new MenuItem("Quit Tomcat Controller");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu.add(quit);
		TrayIcon icon = new TrayIcon(image, "Tomcat Controller", menu);
		icon.setImageAutoSize(true);

		tray.add(icon);

//		com.apple.eawt.Application.getApplication().setPreferencesHandler(
//				new PreferencesHandler() {
//
//					@Override
//					public void handlePreferences(PreferencesEvent event) {
//						Preferences userPref = Preferences
//								.userNodeForPackage(this.getClass());
//						JFrame prefWindow = new PrefWindow(userPref);
//						prefWindow.setVisible(true);
//					}
//				});

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				shutdown();
			}
		}));
	}

	private static void startup() {
		try {
			Runtime.getRuntime().exec(getTomcatLocation() + "startup.sh", null,
					null);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private static void shutdown() {
		try {
			Runtime.getRuntime().exec(getTomcatLocation() + "shutdown.sh",
					null, null);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public static String getTomcatLocation() {
		return DEFAULT_TOMCAT_LOCATION;
	}

}

/**
 * @author eyalshalev
 * 
 */
class PrefWindow extends JFrame {

	/**
	 * @throws HeadlessException
	 */
	public PrefWindow(Preferences prefs) throws HeadlessException {
		setAlwaysOnTop(true);

		setTitle("Tomcat Quick Preferences");

		setSize(800, 600);

		setResizable(false);

		// Get the size of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		// Determine the new location of the window
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;

		setLocation(x, y);

		// Create and populate the panel.
	    SpringLayout layout = new SpringLayout();
		JPanel contentPane = new JPanel(layout);

		JLabel tomcatLocationLabel = new JLabel("Tomcat Location");

		contentPane.add(tomcatLocationLabel);

		JTextField tomcatLocation = new JTextField(15);

		tomcatLocation.setText(Main.getTomcatLocation());

		tomcatLocationLabel.setLabelFor(tomcatLocation);
		contentPane.add(tomcatLocation);
		

	    layout.putConstraint(SpringLayout.WEST, tomcatLocationLabel, 10, SpringLayout.WEST, contentPane);
	    layout.putConstraint(SpringLayout.NORTH, tomcatLocationLabel, 25, SpringLayout.NORTH, contentPane);
	    layout.putConstraint(SpringLayout.NORTH, tomcatLocation, 25, SpringLayout.NORTH, contentPane);
	    layout.putConstraint(SpringLayout.WEST, tomcatLocation, 20, SpringLayout.EAST, tomcatLocationLabel);
		
	    contentPane.setOpaque(true);
		setContentPane(contentPane);

	}

}
