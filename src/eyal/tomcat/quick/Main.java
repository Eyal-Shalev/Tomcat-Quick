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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.AppEvent.PreferencesEvent;

public class Main {

	private static Preferences prefs = Preferences
			.userNodeForPackage(Main.class);

	public static final String DEFAULT_TOMCAT_LOCATION = "/usr/local/tomcat/bin/";

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

		com.apple.eawt.Application.getApplication().setPreferencesHandler(
				new PreferencesHandler() {

					@Override
					public void handlePreferences(PreferencesEvent event) {
						JFrame prefWindow = new PrefWindow(prefs);
						prefWindow.setVisible(true);
					}
				});

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				shutdown();
			}
		}));
	}

	private static void startup() {
		if (locationValid()) {
			try {
				Runtime.getRuntime().exec(getTomcatLocation() + "startup.sh", null,
						null);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}

	private static void shutdown() {
		if (locationValid()) {
			try {
				Runtime.getRuntime().exec(getTomcatLocation() + "shutdown.sh",
						null, null);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}

	public static String getTomcatLocation() {
		return prefs.get("tomcatLocation", DEFAULT_TOMCAT_LOCATION);
	}
	
	public static boolean locationValid() {
		return (new File(getTomcatLocation() + "startup.sh").exists());
	}

}

/**
 * @author eyalshalev
 * 
 */
class PrefWindow extends JFrame {

	private static Preferences prefs;

	private static JLabel tomcatLocationLabel = new JLabel("Tomcat Location");

	private static JTextField tomcatLocation = new JTextField(30);
	
	private static Icon validIcon = new ImageIcon(PrefWindow.class.getResource("valid.png"));
	
	private static Icon invalidIcon = new ImageIcon(PrefWindow.class.getResource("invalid.png"));
	
	private static JButton tomcatLocationToDefault = new JButton("Return to default");

	/**
	 * @throws HeadlessException
	 */
	public PrefWindow(Preferences prefs) throws HeadlessException {
		PrefWindow.prefs = prefs;

		setAlwaysOnTop(true);

		setTitle("Tomcat Quick Preferences");

		setSize(700, 300);

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

		contentPane.add(tomcatLocationLabel);

		tomcatLocation.setText(Main.getTomcatLocation());

		tomcatLocation.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				save();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				save();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				save();
			}
		});

		tomcatLocationLabel.setLabelFor(tomcatLocation);
		contentPane.add(tomcatLocation);
		
		tomcatLocationToDefault.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				tomcatLocation.setText(Main.DEFAULT_TOMCAT_LOCATION);
				save();
			}
		});
		
		contentPane.add(tomcatLocationToDefault);

		layout.putConstraint(SpringLayout.WEST, tomcatLocationLabel, 10,
				SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.NORTH, tomcatLocationLabel, 30,
				SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.NORTH, tomcatLocation, 25,
				SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.WEST, tomcatLocation, 20,
				SpringLayout.EAST, tomcatLocationLabel);
		layout.putConstraint(SpringLayout.NORTH, tomcatLocationToDefault, 25,
				SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.WEST, tomcatLocationToDefault, 10,
				SpringLayout.EAST, tomcatLocation);

		contentPane.setOpaque(true);
		setContentPane(contentPane);
		
		save();
	}

	public static void save() {
		prefs.put("tomcatLocation", tomcatLocation.getText());
		if (tomcatLocation.getText().equals(Main.DEFAULT_TOMCAT_LOCATION)) {
			tomcatLocationToDefault.setEnabled(false);
		}
		else {
			tomcatLocationToDefault.setEnabled(true);
		}
		validateLocation();
	}
	

	
	public static void validateLocation() {
		if (Main.locationValid()) {
			tomcatLocationLabel.setIcon(validIcon);
		}
		else {
			tomcatLocationLabel.setIcon(invalidIcon);
		}
	}

}
