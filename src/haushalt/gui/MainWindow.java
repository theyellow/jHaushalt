package haushalt.gui;

import haushalt.auswertung.domain.HaushaltDefinition;
import haushalt.auswertung.domain.MainWindowProperties;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class MainWindow {
	private final JFrame frame = new JFrame();
	private MainWindowProperties properties;
	private JTabbedPane tabbedPane;
	private ActionHandler actionHandler;
	private JTextField status = new JTextField(HaushaltDefinition.COPYRIGHT);
	private GemerkteBuchungenGlassPane glassPane;
	
	public MainWindow(MainWindowProperties properties, JTabbedPane tabbedPane, ActionHandler actionHandler,GemerkteBuchungenGlassPane gemerkteBuchungen) {
		this.properties = properties;
		this.tabbedPane = tabbedPane;
		this.actionHandler = actionHandler;
		this.glassPane = gemerkteBuchungen;

		defineFrame();
	}

	public void defineMainWindow() {
		final Container contentPane = frame.getContentPane();
		final int breite = properties.getWidth();
		final int hoehe = properties.getHeight();
		this.tabbedPane.setPreferredSize(new Dimension(breite, hoehe));
		contentPane.add(actionHandler.erzeugeToolBar(), BorderLayout.PAGE_START);
		contentPane.add(this.status, BorderLayout.PAGE_END);
	}
	
	public void setStatus(String text) {
		status.setText(text);
	}
	
	public void setCopyrightText() {
		setStatus(HaushaltDefinition.COPYRIGHT);
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
		this.frame.setTitle(HaushaltDefinition.COPYRIGHT);
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
