package haushalt.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class MainWindow {
	public static final String COPYRIGHT = "jHaushalt v2.6 * (C)opyright 2002-2011 Lars H. Hahn";
	
	private final JFrame frame = new JFrame();
	private Properties properties;
	private JTabbedPane tabbedPane;
	private ActionHandler actionHandler;
	private JTextField status = new JTextField(COPYRIGHT);
	private GemerkteBuchungenGlassPane glassPane;
	
	public MainWindow(Properties properties, JTabbedPane tabbedPane, ActionHandler actionHandler,GemerkteBuchungenGlassPane gemerkteBuchungen) {
		this.properties = properties;
		this.tabbedPane = tabbedPane;
		this.actionHandler = actionHandler;
		this.glassPane = gemerkteBuchungen;

		defineFrame();
	}

	public void defineMainWindow() {
		final Container contentPane = frame.getContentPane();
		final int breite = new Integer(properties.getProperty("jhh.register.breite", "600")).intValue();
		final int hoehe = new Integer(properties.getProperty("jhh.register.hoehe", "400")).intValue();
		this.tabbedPane.setPreferredSize(new Dimension(breite, hoehe));
		contentPane.add(actionHandler.erzeugeToolBar(), BorderLayout.PAGE_START);
		contentPane.add(this.status, BorderLayout.PAGE_END);
	}
	
	public void setStatus(String text) {
		status.setText(text);
	}
	
	public void setCopyrightText() {
		setStatus(COPYRIGHT);
	}
	
	/**
	 * Liefert das Hauptfenster.
	 * 
	 * @return Hauptfenster
	 */
	public JFrame getFrame() {
		return frame;
	}
	
	private void defineFrame() {
		this.frame.setIconImage(loadIcon("jhh-icon.gif").getImage());
		this.frame.setGlassPane(this.glassPane);
		this.frame.setTitle(COPYRIGHT);
		this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				//beenden();
			}
		});
		this.status.setEditable(false);
	}

	private ImageIcon loadIcon(final String dateiname) {
		final URLClassLoader urlLoader = (URLClassLoader) getClass().getClassLoader();
		final URL fileLoc = urlLoader.findResource("res/" + dateiname);
		return new ImageIcon(fileLoc);
	}

	
}
