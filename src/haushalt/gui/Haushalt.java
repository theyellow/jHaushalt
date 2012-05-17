/*
 * This file is part of jHaushalt.
 * jHaushalt is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * jHaushalt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
 */

package haushalt.gui;

import haushalt.auswertung.CsvHandler;
import haushalt.auswertung.DlgContainerAuswertung;
import haushalt.auswertung.FarbPaletten;
import haushalt.daten.AbstractBuchung;
import haushalt.daten.Datenbasis;
import haushalt.daten.Datenbasis.QuickenImportException;
import haushalt.daten.Datum;
import haushalt.daten.EinzelKategorie;
import haushalt.daten.Euro;
import haushalt.daten.SplitBuchung;
import haushalt.daten.StandardBuchung;
import haushalt.daten.Umbuchung;
import haushalt.gui.dialoge.DlgAutoBuchung;
import haushalt.gui.dialoge.DlgBereinigen;
import haushalt.gui.dialoge.DlgEinrichtung;
import haushalt.gui.dialoge.DlgHilfe;
import haushalt.gui.dialoge.DlgImport;
import haushalt.gui.dialoge.DlgInfo;
import haushalt.gui.dialoge.DlgKategorienBearbeiten;
import haushalt.gui.dialoge.DlgOptionen;
import haushalt.gui.dialoge.DlgRegisterBearbeiten;
import haushalt.gui.dialoge.DlgSplitBuchung;
import haushalt.gui.dialoge.DlgSuchenErsetzen;
import haushalt.gui.generischerdialog.DatumGDP;
import haushalt.gui.generischerdialog.EineKategorieGDP;
import haushalt.gui.generischerdialog.EuroGDP;
import haushalt.gui.generischerdialog.GenerischerDialog;
import haushalt.gui.generischerdialog.RegisterGDP;
import haushalt.gui.generischerdialog.TextGDP;
import haushalt.gui.mac.MacAdapter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.6/2010.08.14
 */

/*
 * 2010.08.14 Name der Datei für die Properties jetzt variabel
 * (PROPERTIES_FILENAME)
 * 2009.07.27 Erhöhen der Versionsnummer auf 2.6
 * 2008.04.15 Erhöhen der Versionsnummer auf 2.5.4 :-)
 * 2008.04.04 BugFix: Korrektur Doppelpunkt
 * 2008.03.19 Erhöhen der Versionsnummer auf 2.5.3 :-)
 * 2008.03.11 Erhöhen der Versionsnummer auf 2.5.2 :-)
 * 2008.03.10 Erhöhen der Versionsnummer auf 2.5.1 :-)
 * 2008.02.12 Überprüfung, ob Auswertungen geaendert, hinzugefügt
 * 2008.01.15 Internationalisierung
 * 2007.04.03 Automatisches Anlegen des ersten Registers beim Neueinrichten
 * eines Haushaltsbuchs
 * 2007.02.22 Erweiterung: Export aller Buchungen
 * 2007.02.14 Register können umbenannt und von Hand verschoben werden
 * 2007.02.06 Erweiterung: Beim Erstellen eines neuen Registers kann
 * optional ein Eröffnungssaldo übergeben werden
 * 2007.01.31 BugFix: Korrektes Merken der Größe des Auswertungsdialog
 * Erhöhen der Versionsnummer auf 2.5 :-)
 * 2006.06.21 Erhöhen der Versionsnummer auf 2.1.3 :-)
 * 2006.06.19 Speicherung der Fenstergröße des Auswertungsdialog
 * 2006.06.11 Erhöhen der Versionsnummer auf 2.1.2 :-)
 * 2006.04.19 Erhöhen der Versionsnummer auf 2.1.1 :-)
 * 2006.02.09 Ausdruck eines Registers; Verlagerung der
 * Druckereinstellungen in den Auswertungsdialog
 * 2006.02.06 Erweiterung: Planungen hinzugefügt
 * 2006.02.03 BugFix: Nur reduzierte Split-Buchungen speichern
 * 2006.02.01 Umwandeln von Buchungen hinzugefügt
 * 2006.01.31 Methode zum Anzeigen von Änderungen in einem
 * Register
 * 2006.01.24 Erhöhen der Versionsnummer auf 2.1 :-)
 * 2005.04.30 BugFix: Nach Splitten Buchung merken
 * 2005.03.10 Erweiterung: Gemerkte Buchungen ab Datum
 * 2005.02.18 BugFix: Nachdem Splitten einer Buchung wurde
 * nicht "mitgesprungen".
 * 2004.08.25 BugFix: Abbruch beim CSV-Import berücksichtigt.
 */
public class Haushalt implements KeyListener, ListSelectionListener {

	public static final String COPYRIGHT = "jHaushalt v2.6 * (C)opyright 2002-2011 Lars H. Hahn";
	public static final String VERSION = "2.6";
	public static final String PROPERTIES_FILENAME = ".jhh";

	private static final Logger LOGGER = Logger.getLogger(Haushalt.class.getName());
	private static final boolean DEBUG = false;
	private static final TextResource RES = TextResource.get();

	private final JTextField status = new JTextField(COPYRIGHT);
	private final Properties properties;
	private final JFrame frame = new JFrame();
	private final JTabbedPane tabbedPane;
	private TableColumnModel columnModel = null;
	private final Image icon;
	private final GemerkteBuchungenGlassPane glassPane = new GemerkteBuchungenGlassPane();
	private final DlgOptionen dlgOptionen;
	private final DlgSuchenErsetzen dlgSuchenErsetzen;
	private Datenbasis db;
	private DlgContainerAuswertung containerAuswertung;
	private final ActionHandler actionHandler;

	private final FileFilter fileFilter = new FileFilter() {
		@Override
		public boolean accept(final File file) {
			if (file.isDirectory()) {
				return true;
			}
			if (file.getName().toLowerCase().endsWith(".jhh")) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return RES.getString("jhaushalt_files");
		}
	};

	/**
	 * Einziger Konstruktor.
	 */
	public Haushalt(final String dateiname) {
		// Properties laden
		this.properties = new Properties();
		if (DEBUG) {
			LOGGER.info("Lade die Properties.");
		}
		final String userHome = System.getProperty("user.home");
		this.properties.setProperty("jhh.ordner", userHome); // Default-Wert
		// Arbeitsordner
		final File datei = new File(userHome, PROPERTIES_FILENAME);
		if (datei.exists()) {
			try {
				final FileInputStream fis = new FileInputStream(datei);
				this.properties.load(fis);
				fis.close();
				if (DEBUG) {
					this.properties.list(System.out);
				}
			} catch (final Exception e) {
				if (DEBUG) {
					LOGGER.warning("-W- Keine Properties geladen.");
				}
			}
		} else {
			final DlgEinrichtung einrichten = new DlgEinrichtung(this.frame, this.properties);
			einrichten.pack();
			einrichten.setVisible(true);
		}

		// Das Neusetzen der Locale geht leider nicht an einer zentralen Stelle
		// ...
		RES.setLocale(this.properties.getProperty("jhh.opt.sprache", "" + Locale.getDefault()));
		Locale.setDefault(RES.getLocale());
		this.frame.setLocale(RES.getLocale());

		// Look-and-Feel:
		final String systemClassName = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(systemClassName);
		} catch (final Exception e) {
			LOGGER.warning(e.getMessage());
		}
		// TODO Auskommentiert; da Fehlermedlung unklar wie die Locale gesetzt
		// wird
		// sun.awt.AppContext.getAppContext().put("JComponent.defaultLocale",
		// res.getLocale());

		// Erzeuge Hauptfenster
		final Container contentPane = this.frame.getContentPane();
		if (DEBUG) {
			LOGGER.info("Hauptfenster: Lade das Icon.");
		}
		this.icon = bildLaden("jhh-icon.gif").getImage();
		this.frame.setIconImage(this.icon);
		this.tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
		final int breite = new Integer(this.properties.getProperty("jhh.register.breite", "600")).intValue();
		final int hoehe = new Integer(this.properties.getProperty("jhh.register.hoehe", "400")).intValue();
		this.tabbedPane.setPreferredSize(new Dimension(breite, hoehe));
		this.frame.setGlassPane(this.glassPane);
		this.frame.setTitle(COPYRIGHT);
		// Das Menü wird initialisiert
		if (DEBUG) {
			LOGGER.info("Initialisiere das Menü.");
		}
		this.actionHandler = new ActionHandler(this);
		this.frame.setJMenuBar(this.actionHandler.erzeugeMenuBar());
		contentPane.add(this.actionHandler.erzeugeToolBar(), BorderLayout.PAGE_START);
		this.status.setEditable(false);
		contentPane.add(this.status, BorderLayout.PAGE_END);
		this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				beenden();
			}
		});

		// Letzte Datei laden oder neu initialisieren
		// In jedem Fall wird die Datenbasis erzeugt.
		if (DEBUG) {
			LOGGER.info("Lade Daten / Erzeuge Datenbasis.");
		}
		if (dateiname != null) {
			laden(new File(dateiname));
		} else if (this.properties.containsKey("jhh.dateiname")) {
			laden(new File(this.properties.getProperty("jhh.dateiname")));
		} else {
			neu();
		}

		// Dialog für die Optionen erzeugen; die Optionen sind eine
		// Teilmenge der Properties
		if (DEBUG) {
			LOGGER.info("Options-Dialog initialisieren.");
		}
		this.dlgOptionen = new DlgOptionen(this, this.properties);

		// Dialog Suchen/ersetzen erzeugen
		this.dlgSuchenErsetzen = new DlgSuchenErsetzen(this);

		oberflaecheAnpassen(); // hier werden auch andere Optionen gesetzt

		// some smaller improvements for apple-users
		if (isMacOSX()) {
			MacAdapter.macStyle(this);
		}

		if (DEBUG) {
			LOGGER.info("Applikation initialisiert.");
		}
	}

	/**
	 * Liefert das Hauptfenster.
	 * 
	 * @return Hauptfenster
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Liefert das Arbeitsverzeichnis.
	 * 
	 * @return Arbeitsverzeichnis
	 */
	public String getOrdner() {
		return properties.getProperty("jhh.ordner");
	}

	// -- Einstellungen
	// ---------------------------------------------------------

	/**
	 * Übernimmt die optionalen Einstellungen des Benutzters.
	 * 
	 */
	private void oberflaecheAnpassen() {
		Euro.setWaehrungssymbol(properties.getProperty("jhh.opt.waehrung", "€"));
		setTabPlacement(properties.getProperty("jhh.opt.reiter", "BOTTOM"));
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			final JScrollPane scrollPane = (JScrollPane) tabbedPane.getComponent(i);
			final JTable table = (JTable) scrollPane.getViewport().getView();
			table.setSelectionBackground(getFarbeSelektion());
			table.setGridColor(getFarbeGitter());
		}
		db.setStartDatum(new Datum(properties.getProperty("jhh.opt.startdatum", "01.01.00")));
		int idx;
		try {
			idx = Integer.parseInt(properties.getProperty("jhh.opt.deltaste", "0"));
		} catch (final NumberFormatException e) {
			// Kann Auftreten, da Version < 2.5 noch kein Index, sondern
			// Klartext gespeichert hat
			idx = 0;
			properties.setProperty("jhh.opt.deltaste", "0");
		}
		int deltaste;
		switch (idx) {
			case 1:
				deltaste = InputEvent.SHIFT_MASK;
				break;
			case 2:
				deltaste = InputEvent.CTRL_MASK;
				break;
			case 3:
				deltaste = InputEvent.ALT_MASK;
				break;
			default:
				deltaste = 0;
		}
		DeleteableTextField.setDeltaste(deltaste);
		FarbPaletten.setCustomColor(properties.getProperty("jhh.opt.custom", "16776960"));
	}

	public Color getFarbeSelektion() {
		final int farbe = new Integer(properties.getProperty("jhh.opt.selektion", "12632256")).intValue(); // #c0c0c0
		return new Color(farbe);
	}

	public Color getFarbeGitter() {
		final int farbe = new Integer(properties.getProperty("jhh.opt.gitter", "10066329")).intValue(); // #999999
		return new Color(farbe);
	}

	public Color getFarbeZukunft() {
		final int farbe = new Integer(properties.getProperty("jhh.opt.zukunft", "16777088")).intValue(); // #ffff80
		return new Color(farbe);
	}

	public String getFontname() {
		return properties.getProperty("jhh.opt.font", "SansSerif");
	}

	public int getFontgroesse() {
		return new Integer(properties.getProperty("jhh.opt.punkt", "12")).intValue();
	}

	/**
	 * Lädt Bilder aus dem Verzeichnis <code>res/</code>.
	 * 
	 * @param dateiname
	 *            Dateiname des Bildes (ohne Pfad)
	 */
	public ImageIcon bildLaden(final String dateiname) {
		final URLClassLoader urlLoader = (URLClassLoader) getClass().getClassLoader();
		final URL fileLoc = urlLoader.findResource("res/" + dateiname);
		if (DEBUG) {
			LOGGER.info("Lade Bild @ " + fileLoc);
		}
		return new ImageIcon(fileLoc);
	}

	/**
	 * Setzt den Ort der Register-Reiter.
	 * 
	 * @param tabPlacement
	 *            "TOP", "BOTTOM", "LEFT" oder "RIGHT"
	 */
	private void setTabPlacement(final String tabPlacement) {
		int tp = SwingConstants.BOTTOM;
		final Class<?> c = SwingConstants.class;
		try {
			final Field field = c.getField(tabPlacement);
			tp = field.getInt(new Integer(0));
		} catch (final NoSuchFieldException e) {
			LOGGER.warning(e.getMessage());
		} catch (final IllegalAccessException e) {
			LOGGER.warning(e.getMessage());
		}
		tabbedPane.setTabPlacement(tp);
	}

	// -- Register-Tabellen
	// ------------------------------------------------------

	/**
	 * Wandelt alle Register in Tabellen um und zeigt sie so an.
	 */
	private void zeigeAlleRegisterTabs() {
		final String[] register = db.getRegisterNamen();
		for (int i = 0; i < register.length; i++) {
			zeigeRegisterTab(register[i]);
		}
	}

	/**
	 * Wandelt ein Register in eine Tabelle um und zeigt sie so an.
	 * 
	 * @param regname
	 *            Name des Registers
	 * @return Nummer des Registers
	 */
	public int zeigeRegisterTab(final String regname) {
		final int tabNr = tabbedPane.indexOfTab(regname);
		// int tabNr = getRegisterTabNr(""+tableModel);
		if (tabNr > -1) {
			// Register wird schon angezeigt, wahrscheinlich wurde es verändert:
			registerVeraendert(tabNr);
			return tabNr;
		}
		final RegisterTableModel tableModel = new RegisterTableModel(this, db, regname);
		JTable table;
		if (columnModel == null) {
			// Das ColumnModel wird von der ersten erzeugten Tabelle genommen
			// und
			// initialisiert. Alle Tabellen erhalten so die gleichen
			// Spalten-Breiten.
			table = new JTable(tableModel);
			columnModel = table.getColumnModel();
			if (properties.containsKey("jhh.register.spalte0")) {
				for (int j = 0; j < columnModel.getColumnCount(); j++) {
					final int breite = new Integer(properties.getProperty("jhh.register.spalte" + j)).intValue();
					columnModel.getColumn(j).setPreferredWidth(breite);
					if (DEBUG) {
						LOGGER.info("" + tableModel + ": Breite Spalte " + j + " = " + breite);
					}
				}
			}
		} else {
			table = new JTable(tableModel, columnModel);
		}
		table.setSurrendersFocusOnKeystroke(true);
		table.getSelectionModel().addListSelectionListener(this);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setSelectionBackground(getFarbeSelektion());
		table.setGridColor(getFarbeGitter());
		table.setComponentPopupMenu(actionHandler.getPopupMenu());

		// Cell-Editoren erzeugen
		table.setDefaultEditor(Datum.class, new DefaultCellEditor(new DatumField()));
		final DeleteableTextField textField = new DeleteableTextField();
		textField.addKeyListener(this);
		final DefaultCellEditor cellEditor = new DefaultCellEditor(textField);
		table.setDefaultEditor(String.class, cellEditor);
		table.setDefaultEditor(Object.class, new KategorieCellEditor(this, db));
		table.setDefaultEditor(Euro.class, new DefaultCellEditor(new EuroField()));
		// Cell-Renderer erzeugen
		table.setDefaultRenderer(Datum.class, new DatumRenderer(properties));
		table.setDefaultRenderer(String.class, new DefaultTableCellRenderer());
		table.setDefaultRenderer(Object.class, new KategorieRenderer());
		table.setDefaultRenderer(Euro.class, new EuroRenderer());

		final JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tabbedPane.add(scrollPane, "" + tableModel);
		table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
		final JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
		// JavaBug: Hier ist der maxValue noch 100 ...
		scrollBar.setValue(100);
		// Hier hat MaxValue den tatsaechlichen Wert ...
		scrollBar.setValue(scrollBar.getMaximum());

		if (tabbedPane.getTabCount() == 1) {
			frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
			frame.validate();
			if (DEBUG) {
				LOGGER.info("Register '" + regname + "' ist erste Table -> tabbedPane angezeigt.");
			}
		}

		frame.repaint();
		if (DEBUG) {
			LOGGER.info("Table zu Register '" + regname + "' erzeugt.");
		}
		return tabbedPane.getTabCount() - 1;
	}

	/**
	 * Nennt den Tab eines Registers um.
	 * 
	 * @param alterName
	 *            alter Name des Registers
	 * @param neuerName
	 *            neuer Name des Registers
	 */
	public void renameRegisterTab(final String alterName, final String neuerName) {
		final int idx = tabbedPane.indexOfTab(alterName);
		tabbedPane.setTitleAt(idx, neuerName);
		final JScrollPane scrollPane = (JScrollPane) tabbedPane.getComponentAt(idx);
		final JTable table = (JTable) scrollPane.getViewport().getView();
		final RegisterTableModel tableModel = (RegisterTableModel) table.getModel();
		tableModel.setRegisterName(neuerName);
	}

	/**
	 * Erhöht den Index des Register-Tabs um eins.
	 * 
	 * @param regname
	 *            Registername
	 */
	public void bewegeRegisterNachOben(final String regname) {
		final int idx = tabbedPane.indexOfTab(regname);
		final JScrollPane scrollPane = (JScrollPane) tabbedPane.getComponentAt(idx);
		tabbedPane.removeTabAt(idx);
		tabbedPane.insertTab(regname, null, scrollPane, null, idx + 1);
	}

	/**
	 * Erniedrigt den Index des Register-Tabs um eins.
	 * 
	 * @param regname
	 *            Registername
	 */
	public void bewegeRegisterNachUnten(final String regname) {
		final int idx = tabbedPane.indexOfTab(regname);
		final JScrollPane scrollPane = (JScrollPane) tabbedPane.getComponentAt(idx);
		tabbedPane.removeTabAt(idx);
		tabbedPane.insertTab(regname, null, scrollPane, null, idx - 1);
	}

	/**
	 * Löscht die Tabelle zu einem Register. Falls keine Tabelle mehr vorhanden
	 * ist,
	 * wird wieder das Hintergrundbild angezeigt.
	 * 
	 * @param regname
	 *            Name des Registers
	 */
	private void entferneRegisterTab(final String regname) {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			if (regname.equals(tabbedPane.getTitleAt(i))) {
				final JScrollPane scrollPane = (JScrollPane) tabbedPane.getComponentAt(i);
				final JTable table = (JTable) scrollPane.getViewport().getView();
				// Die Register teilen sich ein ColumnModel, bevor ein Register
				// gelöscht wird
				// muss daher das ColumnModel neu gesetzt werden.
				table.setColumnModel(new DefaultTableColumnModel());
				tabbedPane.removeTabAt(i);
			}
		}
		if (tabbedPane.getTabCount() == 0) {
			// Wenn das letzte Register entfernt wurde, wird auch die TabbedPane
			// entfernt.
			// Dies ist ein Relikt aus der Zeit als noch ein Hintergrund
			// angezeigt wurde
			columnModel = null;
			frame.getContentPane().remove(tabbedPane);
			frame.validate();
		}
		frame.repaint();
	}

	private void entferneAlleRegisterTabs() {
		final Container contentPane = frame.getContentPane();
		tabbedPane.removeAll();
		contentPane.remove(tabbedPane);
		columnModel = null;
		frame.validate();
		frame.repaint();
	}

	/**
	 * Teilt mit, dass die Daten des Registers verändert wurden
	 * 
	 * @param name
	 *            Name des Registers
	 */
	public void registerVeraendert(final String name) {
		registerVeraendert(tabbedPane.indexOfTab(name));
	}

	private void registerVeraendert(final int nr) {
		final JScrollPane scrollPane = (JScrollPane) tabbedPane.getComponentAt(nr);
		final JTable table = (JTable) scrollPane.getViewport().getView();
		final RegisterTableModel tableModel = (RegisterTableModel) table.getModel();
		tableModel.fireTableDataChanged();
	}

	public void alleRegisterVeraendert() {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			registerVeraendert(i);
		}
	}

	/**
	 * Wählt eine bestimmte Buchung in einer der Register-Tabellen aus.
	 * 
	 * @param regname
	 *            Name des Registers
	 * @param buchungIndex
	 *            Zeile der Buchung
	 */
	public void selektiereBuchung(final String regname, int buchungIndex) {
		final int tabIndex = tabbedPane.indexOfTab(regname);
		if (tabIndex > -1) {
			tabbedPane.setSelectedIndex(tabIndex);
			final JScrollPane scrollPane = (JScrollPane) tabbedPane.getComponentAt(tabIndex);
			final JViewport viewport = scrollPane.getViewport();
			final JTable table = (JTable) viewport.getComponent(0);
			if (buchungIndex >= table.getRowCount()) {
				buchungIndex = table.getRowCount() - 1;
			}
			table.setRowSelectionInterval(buchungIndex, buchungIndex);
			table.scrollRectToVisible(table.getCellRect(buchungIndex, 0, true));
			if (DEBUG) {
				LOGGER.info("selektiereBuchung: Buchung Nr. " + buchungIndex + " in Register " + regname + " selektiert.");
			}
		} else {
			status.setText("-E- "
				+ RES.getString("status_register_not_found1")
				+ " "
				+ regname
				+ " "
				+ RES.getString("status_register_not_found2"));
		}
	}

	// -- Gemerkte Buchungen
	// -----------------------------------------------------

	/**
	 * Setzt die Koordinaten an denen die gemerkte Buchung erscheinen soll.
	 * Die Koordinaten werden in Abhängigkeit von dem TextField gesetzt in dem
	 * der Buchungstext eingegeben wird.
	 * 
	 * @param comp
	 *            Component = JTextField
	 */
	public void setzteKoordinatenGemerkteBuchung(final Component comp) {
		final Point xy = SwingUtilities.convertPoint(comp.getParent(), comp.getX(), comp.getY(), glassPane);
		glassPane.setTextKoordinaten(xy);
	}

	public boolean gemerkteBuchungen() {
		return Boolean.valueOf(properties.getProperty("jhh.opt.gemerkte", "true")).booleanValue();
	}

	/**
	 * Zeigt eine zuvor gemerkte Buchung an.
	 * 
	 * @param prefix
	 *            Anfang des Buchungstextes
	 */
	public void zeigeGemerkteBuchung(final String prefix) {
		if (gemerkteBuchungen()) {
			final AbstractBuchung buchung = db.findeGemerkteBuchung(prefix);
			if (buchung != null) {
				final String text = buchung.getText() + " / " + buchung.getKategorie() + " / " + buchung.getWert();
				glassPane.setText(text);
				glassPane.setVisible(true);
			} else {
				glassPane.setVisible(false);
			}
		}
	}

	/**
	 * Überprüft, ob schon ein Register angelegt wurde
	 * 
	 * @return <code>false</code> mindestens ein Register vorhanden
	 */
	private boolean keinRegisterVorhanden() {
		if (tabbedPane.getTabCount() == 0) {
			final int n = JOptionPane.showConfirmDialog(
					frame,
					RES.getString("message_no_register"),
					RES.getString("warning"),
					JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.OK_OPTION) {
				registerBearbeiten();
			}
			return true;
		}
		return false;
	}

	// == Menü: Datei
	// ============================================================

	/**
	 * Überprüft, ob die Applikationsdaten geändert wurden. Wird von neu() und
	 * beenden() aufgerufen.
	 * 
	 * @return <code>true</code> - Applikation wurde geändert, <code>false</code> - Daten sind gespeichert
	 */
	private boolean abfrageGeaendert() {
		if ((db != null) && (db.isGeaendert() || containerAuswertung.isGeaendert())) {
			final int n = JOptionPane.showConfirmDialog(frame, RES.getString("message_data_changed"));
			switch (n) {
				case JOptionPane.CANCEL_OPTION:
					return false;
				case JOptionPane.NO_OPTION:
					return true;
				case JOptionPane.OK_OPTION:
					speichern();
					break;
				default:
					break;
			}
			if (db.isGeaendert() || containerAuswertung.isGeaendert()) {
				// war
				// nicht
				// erfolgreich!
				return false;
			}
		}
		return true;
	}

	public void neu() {
		if (abfrageGeaendert()) {
			properties.remove("jhh.dateiname");
			entferneAlleRegisterTabs();
			db = new Datenbasis();
			containerAuswertung = new DlgContainerAuswertung(this, db);
			final int auswertungBreite = new Integer(properties.getProperty("jhh.auswertung.breite", "600")).intValue();
			final int auswertungHoehe = new Integer(properties.getProperty("jhh.auswertung.hoehe", "400")).intValue();
			containerAuswertung.setPreferredSize(new Dimension(auswertungBreite, auswertungHoehe));
			status.setText(COPYRIGHT);
			final String name = db.erzeugeRegister(RES.getString("default_register_name"));
			zeigeRegisterTab(name);
			db.addUmbuchung(new Datum(), RES.getString("opening_balance"), name, name, Euro.NULL_EURO);
		}
	}

	public void laden() {
		if (abfrageGeaendert()) {
			final JFileChooser dateidialog = new JFileChooser();
			dateidialog.setFileFilter(fileFilter);
			dateidialog.setCurrentDirectory(new File(properties.getProperty("jhh.ordner")));
			if (dateidialog.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
				laden(dateidialog.getSelectedFile());
			}
		}
	}

	private void laden(final File datei) {
		entferneAlleRegisterTabs();
		db = new Datenbasis();
		db.setStartDatum(new Datum(properties.getProperty("jhh.opt.startdatum", "01.01.00")));
		try {
			final FileInputStream fis = new FileInputStream(datei);
			final ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(frame, RES.getString("reading")
				+ " "
				+ datei
				+ " ...", fis);
			final DataInputStream in = new DataInputStream(pmis);
			final String versionInfo = in.readUTF();
			final boolean isCurrentVersion = versionInfo.equals("jHaushalt" + Datenbasis.VERSION_DATENBASIS);
			final String warningMessage = RES.getString("message_old_version1")
				+ " "
				+ versionInfo
				+ " "
				+ RES.getString("message_old_version2")
				+ Datenbasis.VERSION_DATENBASIS
				+ RES.getString("message_old_version3");
			final String warningTitle = RES.getString("warning");
			if ((!isCurrentVersion)
				&& (JOptionPane.showConfirmDialog(null, warningMessage, warningTitle, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)) {
				fis.close();
			} else {
				db.laden(in, versionInfo);
				fis.close();
				properties.setProperty("jhh.dateiname", datei.getPath());
				status.setText(datei.getPath() + " " + RES.getString("status_loaded") + ".");
			}
		} catch (final FileNotFoundException e) {
			status.setText("-E- " + datei.getPath() + " " + RES.getString("status_not_found"));
			properties.remove("jhh.dateiname");
		} catch (final IOException e) {
			status.setText("-E- " + RES.getString("status_load_error") + ": " + datei.getPath());
			properties.remove("jhh.dateiname");
		}
		final int anzahl = db.ausfuehrenAutoBuchungen(new Datum());
		if (anzahl > 0) {
			status.setText(RES.getString("executed_automatic_bookings1")
				+ " "
				+ anzahl
				+ " "
				+ RES.getString("executed_automatic_bookings2"));
		}
		zeigeAlleRegisterTabs();

		// Auswertungen laden
		if (DEBUG) {
			LOGGER.info("Initialisiere die Auswertungen.");
		}
		containerAuswertung = new DlgContainerAuswertung(this, db);
		final String dateiname = datei.getPath() + ".jha";
		containerAuswertung.laden(dateiname);
		final int auswertungBreite = new Integer(properties.getProperty("jhh.auswertung.breite", "600")).intValue();
		final int auswertungHoehe = new Integer(properties.getProperty("jhh.auswertung.hoehe", "400")).intValue();
		containerAuswertung.setPreferredSize(new Dimension(auswertungBreite, auswertungHoehe));

	}

	public void speichern() {
		final String dateiname = properties.getProperty("jhh.dateiname");
		if (DEBUG) {
			LOGGER.info("Speichern: Dateiname=" + dateiname);
		}
		if (dateiname == null) {
			speichernUnter();
		} else {
			speichern(new File(dateiname));
		}
	}

	private void speichern(File datei) {
		try {
			if (!datei.getName().toLowerCase().endsWith(".jhh")) {
				final String name = datei.getAbsolutePath() + ".jhh";
				datei = new File(name);
			}
			final FileOutputStream fos = new FileOutputStream(datei);
			final DataOutputStream out = new DataOutputStream(fos);
			db.speichern(out);
			out.flush();
			fos.close();
			properties.setProperty("jhh.dateiname", datei.getPath());
			status.setText(datei.getPath() + " " + RES.getString("status_saved") + ".");
		} catch (final FileNotFoundException e1) {
			status.setText("-E- " + datei.getPath() + " " + RES.getString("status_not_found"));
		} catch (final IOException e2) {
			status.setText("-E- " + RES.getString("status_write_error") + ": " + datei.getPath());
		}

		// Speichern der Auswertungen
		containerAuswertung.speichern(datei.getPath() + ".jha");

	}

	public void speichernUnter() {
		final JFileChooser dateidialog = new JFileChooser();
		dateidialog.setFileFilter(fileFilter);
		dateidialog.setCurrentDirectory(new File(properties.getProperty("jhh.ordner")));
		if (dateidialog.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			speichern(dateidialog.getSelectedFile());
		}
	}

	public void beenden() {
		if (abfrageGeaendert()) {
			final Dimension dimension = tabbedPane.getSize();
			properties.setProperty("jhh.register.breite", "" + dimension.width);
			if (dimension.height > 100) {
				properties.setProperty("jhh.register.hoehe", "" + dimension.height);
			} else {
				properties.setProperty("jhh.register.hoehe", "100");
			}
			if (columnModel != null) {
				for (int i = 0; i < columnModel.getColumnCount(); i++) {
					properties.setProperty("jhh.register.spalte" + i, "" + columnModel.getColumn(i).getWidth());
				}
			}

			// Speichert die individuellen Programmeigenschaften in die Datei
			// <i>PROPERTIES_FILENAME</i>.
			final String userHome = System.getProperty("user.home");
			final File datei = new File(userHome, PROPERTIES_FILENAME);
			try {
				final FileOutputStream fos = new FileOutputStream(datei);
				properties.store(fos, "Properties: " + VERSION);
				fos.close();
			} catch (final FileNotFoundException e1) {
				if (DEBUG) {
					LOGGER.warning("-W- 'Datei nicht gefunden' beim Speichern der Properties.");
				}
			} catch (final IOException e) {
				if (DEBUG) {
					LOGGER.warning("-W- Properties konnten nicht ins Home-Verzeichnis geschrieben werden.");
				}
			}

			// Ende :-(
			if (DEBUG) {
				LOGGER.info("Ende :-(");
			}
			System.exit(0);
		}
	}

	// === Menü: Bearbeiten
	// ======================================================

	public void suchen() {
		if (keinRegisterVorhanden()) {
			return;
		}
		dlgSuchenErsetzen.showDialog(db);
	}

	public void neueBuchungErstellen() {
		final int registerIndex = tabbedPane.getSelectedIndex();
		final String regname = tabbedPane.getTitleAt(registerIndex);
		db.addStandardBuchung(regname, new StandardBuchung());

		final JScrollPane scrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
		final JTable table = (JTable) scrollPane.getViewport().getView();
		final RegisterTableModel tableModel = (RegisterTableModel) table.getModel();
		final int buchungIndex = table.getRowCount();
		tableModel.fireTableRowsInserted(buchungIndex, buchungIndex);
	}

	public void umbuchen() {
		if (keinRegisterVorhanden()) {
			return;
		}
		final int registerIndex = tabbedPane.getSelectedIndex();
		final String regname = tabbedPane.getTitleAt(registerIndex);
		final GenerischerDialog dlg = new GenerischerDialog(RES.getString("create_rebooking"), frame);
		final DatumGDP pane1 = new DatumGDP(RES.getString("insert_date") + ":", new Datum());
		dlg.addPane(pane1);
		final TextGDP pane2 = new TextGDP(RES.getString("insert_posting_text") + ":", RES.getString("default_posting_text"));
		dlg.addPane(pane2);
		final RegisterGDP pane3 = new RegisterGDP(RES.getString("select_source_register") + ":", db, regname);
		dlg.addPane(pane3);
		final RegisterGDP pane4 = new RegisterGDP(RES.getString("select_destination_register") + ":", db, regname);
		dlg.addPane(pane4);
		final EuroGDP pane5 = new EuroGDP(RES.getString("insert_amount") + ":", new Euro());
		dlg.addPane(pane5);
		if (dlg.showDialog()) {
			final Datum datum = (Datum) pane1.getRefreshedWert();
			final String text = "" + pane2.getRefreshedWert();
			final String quelle = "" + pane3.getRefreshedWert();
			final String ziel = "" + pane4.getRefreshedWert();
			final Euro betrag = (Euro) pane5.getRefreshedWert();
			db.addUmbuchung(datum, text, quelle, ziel, betrag);
			registerVeraendert(quelle);
			registerVeraendert(ziel);
		}
	}

	public void loeschen() {
		if (keinRegisterVorhanden()) {
			return;
		}
		final int registerIndex = tabbedPane.getSelectedIndex();
		final JScrollPane scrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
		final String regname = tabbedPane.getTitleAt(registerIndex);
		final JTable table = (JTable) scrollPane.getViewport().getView();
		final int buchungIndex = table.getSelectedRow();
		if (buchungIndex == -1) {
			JOptionPane.showMessageDialog(frame, RES.getString("message_no_row_selected"));
			return;
		}
		if (buchungIndex == db.getAnzahlBuchungen(regname)) {
			JOptionPane.showMessageDialog(frame, RES.getString("message_row_can_not_be_deleted"));
			return;
		}
		db.entferneBuchung(regname, buchungIndex);
		final RegisterTableModel tableModel = (RegisterTableModel) table.getModel();
		tableModel.fireTableRowsDeleted(buchungIndex, buchungIndex);
		if (table.getCellEditor() != null) {
			table.getCellEditor().cancelCellEditing();
		}
		table.requestFocus();
		table.setRowSelectionInterval(buchungIndex, buchungIndex);
		status.setText(RES.getString("status_posting_deleted1")
			+ " "
			+ (buchungIndex + 1)
			+ " "
			+ RES.getString("status_posting_deleted2")
			+ " "
			+ regname
			+ " "
			+ RES.getString("status_posting_deleted3"));
	}

	public void splitten() {
		if (keinRegisterVorhanden()) {
			return;
		}
		final int registerIndex = tabbedPane.getSelectedIndex();
		final JScrollPane scrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
		final String regname = tabbedPane.getTitleAt(registerIndex);
		final JTable table = (JTable) scrollPane.getViewport().getView();
		final int buchungIndex = table.getSelectedRow();
		if (buchungIndex == -1) {
			JOptionPane.showMessageDialog(frame, RES.getString("message_no_row_selected"));
			return;
		}
		final AbstractBuchung buchung = db.getBuchung(regname, buchungIndex);
		if (buchung.getClass() == Umbuchung.class) {
			JOptionPane.showMessageDialog(frame, RES.getString("message_rebookings_can_not_be_split"));
		} else {
			SplitBuchung splitBuchung;
			if (buchung.getClass() == StandardBuchung.class) {
				splitBuchung = new SplitBuchung((StandardBuchung) buchung);
			} else {
				splitBuchung = (SplitBuchung) buchung;
			}
			final JDialog dlg = new DlgSplitBuchung(this, db, splitBuchung);
			dlg.pack();
			dlg.setVisible(true);
			final int pos = db.ersetzeBuchung(regname, buchungIndex, splitBuchung.reduziere());
			db.buchungMerken(splitBuchung.reduziere());
			registerVeraendert(regname);
			selektiereBuchung(regname, pos);
		}
		if (table.getCellEditor() != null) {
			table.getCellEditor().cancelCellEditing();
		}
		table.requestFocus();
		if (DEBUG) {
			LOGGER.info("Buchung Nr. " + buchungIndex + " im Register " + regname + " gesplittet.");
		}
	}

	public void umwandeln() {
		if (keinRegisterVorhanden()) {
			return;
		}
		final int registerIndex = tabbedPane.getSelectedIndex();
		final JScrollPane scrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
		final String regname = tabbedPane.getTitleAt(registerIndex);
		final JTable table = (JTable) scrollPane.getViewport().getView();
		final int buchungIndex = table.getSelectedRow();
		if (buchungIndex == -1) {
			JOptionPane.showMessageDialog(frame, RES.getString("message_no_row_selected"));
			return;
		}
		if (buchungIndex == db.getAnzahlBuchungen(regname)) {
			JOptionPane.showMessageDialog(frame, RES.getString("message_row_can_not_be_deleted"));
			return;
		}
		final AbstractBuchung buchung = db.getBuchung(regname, buchungIndex);
		if (buchung.getClass() == Umbuchung.class) {
			JOptionPane.showMessageDialog(frame, RES.getString("message_posting_is_already_a_reposting"));
			return;
		}
		final GenerischerDialog dlg = new GenerischerDialog(RES.getString("convert"), frame);
		final RegisterGDP pane = new RegisterGDP(RES.getString("select_destination_register") + ":", db, regname);
		dlg.addPane(pane);
		if (dlg.showDialog()) {
			final Datum datum = buchung.getDatum();
			final String text = buchung.getText();
			final String quelle = regname;
			final String ziel = "" + pane.getRefreshedWert();
			final Euro betrag = Euro.NULL_EURO.sub(buchung.getWert());
			db.entferneBuchung(regname, buchungIndex);
			db.addUmbuchung(datum, text, quelle, ziel, betrag);
			registerVeraendert(quelle);
			registerVeraendert(ziel);
		}
	}

	public void registerBearbeiten() {
		final DlgRegisterBearbeiten dlg = new DlgRegisterBearbeiten(this, db);
		dlg.showDialog();
	}

	public void kategorienBearbeiten() {
		final DlgKategorienBearbeiten dlg = new DlgKategorienBearbeiten(this, db);
		dlg.showDialog();
		alleRegisterVeraendert();
	}

	public void alteBuchungenLoeschen() {
		final GenerischerDialog dlg = new GenerischerDialog(RES.getString("delete_old_bookings"), frame);
		final DatumGDP pane = new DatumGDP(RES.getString("cutoff_date") + ":", new Datum());
		pane.setPreferredSize(new Dimension(320, 60));
		dlg.addPane(pane);

		if (dlg.showDialog()) {
			final Datum datum = (Datum) pane.getRefreshedWert();
			db.entferneAlteBuchungen(datum);
			alleRegisterVeraendert();
			status.setText(RES.getString("status_posting_deleted4")
				+ " "
				+ datum
				+ " "
				+ RES.getString("status_posting_deleted5"));
		}
	}

	public void kategorieErsetzen() {
		final GenerischerDialog dlg = new GenerischerDialog(RES.getString("replace_category"), frame);
		final EineKategorieGDP pane1 = new EineKategorieGDP(RES.getString("current_category") + ":", db, null);
		dlg.addPane(pane1);
		final EineKategorieGDP pane2 = new EineKategorieGDP(RES.getString("new_category") + ":", db, null);
		dlg.addPane(pane2);

		if (dlg.showDialog()) {
			final EinzelKategorie alteKategorie = (EinzelKategorie) pane1.getRefreshedWert();
			final EinzelKategorie neueKategorie = (EinzelKategorie) pane2.getRefreshedWert();
			final int anzahl = db.ersetzeKategorie(alteKategorie, neueKategorie);
			status.setText("" + anzahl + " " + RES.getString("status_replaced_categories"));
			alleRegisterVeraendert();
		}
	}

	public void kategorienBereinigen() {
		final JDialog dlg = new DlgBereinigen(this, db);
		dlg.pack();
		dlg.setVisible(true);
		alleRegisterVeraendert();
	}

	public void registerVereinigen() {
		if (keinRegisterVorhanden()) {
			return;
		}
		final int registerIndex = tabbedPane.getSelectedIndex();
		final String regname = tabbedPane.getTitleAt(registerIndex);
		final GenerischerDialog dlg = new GenerischerDialog(RES.getString("join_register"), frame);
		final RegisterGDP pane1 = new RegisterGDP(RES.getString("select_source_register") + ":", db, regname);
		pane1.setPreferredSize(new Dimension(330, 60));
		dlg.addPane(pane1);
		final RegisterGDP pane2 = new RegisterGDP(RES.getString("select_destination_register") + ":", db, regname);
		pane2.setPreferredSize(new Dimension(330, 60));
		dlg.addPane(pane2);

		if (dlg.showDialog()) {
			final String quelle = (String) pane1.getRefreshedWert();
			final String ziel = (String) pane2.getRefreshedWert();
			if (!quelle.equals(ziel)) {
				db.registerVereinigen(quelle, ziel);
				entferneRegisterTab(quelle);
				status.setText(RES.getString("status_register_deleted1")
					+ " "
					+ quelle
					+ " "
					+ RES.getString("status_register_deleted2"));
				registerVeraendert(ziel);
			} else {
				JOptionPane.showMessageDialog(frame, RES.getString("message_registers_may_not_be_equal"));
			}
		}
	}

	// == Menü: Ausgabe
	// ==========================================================

	public void zeigeAuswertung() {
		if (keinRegisterVorhanden()) {
			return;
		}
		containerAuswertung.zeigeDialog();
		final Dimension dimension = containerAuswertung.getSize();
		properties.setProperty("jhh.auswertung.breite", "" + dimension.width);
		properties.setProperty("jhh.auswertung.hoehe", "" + dimension.height);
		containerAuswertung.setPreferredSize(dimension);
	}

	public void exportCSV() {
		if (keinRegisterVorhanden()) {
			return;
		}
		final ArrayList<String[]> buchungen = db.getBuchungen();
		final CsvHandler handler = new CsvHandler(buchungen);
		handler.exportDlg(frame, properties.getProperty("jhh.ordner"));
	}

	public void drucken() {
		final int tabIndex = tabbedPane.getSelectedIndex();
		if (tabIndex != -1) {
			final JScrollPane scrollPane = (JScrollPane) tabbedPane.getComponentAt(tabIndex);
			final JViewport viewport = scrollPane.getViewport();
			final JTable table = (JTable) viewport.getComponent(0);
			final MessageFormat header = new MessageFormat(RES.getString("register") + ": " + tabbedPane.getTitleAt(tabIndex));
			final MessageFormat footer = new MessageFormat(RES.getString("message_printed_with"));
			try {
				table.print(JTable.PrintMode.FIT_WIDTH, header, footer, true, null, true);
			} catch (final PrinterException e) {
				LOGGER.warning(e.getMessage());
			}
		}
	}

	// == Menü: Extras
	// ===========================================================

	public void optionen() {
		dlgOptionen.showDialog();
		oberflaecheAnpassen();
	}

	public void autoBuchung() {
		if (keinRegisterVorhanden()) {
			return;
		}
		final DlgAutoBuchung dlg = new DlgAutoBuchung(this, db);
		dlg.zeigeDialog();
		alleRegisterVeraendert();
	}

	public void importCSV() {
		if (keinRegisterVorhanden()) {
			return;
		}
		final int registerIndex = tabbedPane.getSelectedIndex();
		final String regname = tabbedPane.getTitleAt(registerIndex);
		final DlgImport spaltenZuordnung = new DlgImport(this);
		spaltenZuordnung.pack();
		spaltenZuordnung.setVisible(true);
		final String[][] importTabelle = spaltenZuordnung.getImportTabelle();
		if (importTabelle != null) {
			db.importBuchungen(regname, importTabelle);
			status.setText("" + importTabelle.length + " " + RES.getString("status_postings_imported"));
			registerVeraendert(registerIndex);
		}
	}

	public void importQuicken() {
		if (keinRegisterVorhanden()) {
			return;
		}
		final int registerIndex = tabbedPane.getSelectedIndex();
		final String regname = tabbedPane.getTitleAt(registerIndex);
		final GenerischerDialog dlg = new GenerischerDialog(RES.getString("import_quicken"), frame);
		final RegisterGDP pane = new RegisterGDP(RES.getString("select_register") + ":", db, regname);
		pane.setPreferredSize(new Dimension(250, 60));
		dlg.addPane(pane);
		if (dlg.showDialog()) {
			final JFileChooser dateidialog = new JFileChooser();
			dateidialog.setCurrentDirectory(new File(properties.getProperty("jhh.ordner")));
			if (dateidialog.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
				final File datei = dateidialog.getSelectedFile();
				try {
					final FileInputStream in = new FileInputStream(datei);
					final boolean euroImport = Boolean.valueOf(properties.getProperty("jhh.opt.euroimport", "true")).booleanValue();
					db.importQuickenRegister(in, (String) pane.getRefreshedWert(), euroImport);
					in.close();
					zeigeAlleRegisterTabs();
				} catch (final FileNotFoundException e1) {
					LOGGER.warning("-E- " + datei.getPath() + " nicht gefunden!");
					status.setText(e1.getMessage());
				} catch (final IOException e2) {
					LOGGER.warning("-E- Fehler beim Importieren: " + datei.getPath());
					status.setText(e2.getMessage());
				} catch (final QuickenImportException e) {
					LOGGER.warning("-E- Fehler beim Importieren: " + datei.getPath());
					status.setText(e.getMessage());
				}
			}
		}
	}

	// == Menü: Hilfe
	// ============================================================

	/**
	 * Wird von Menü-Handler bei Auswahl von '<b>Hilfe/Inhalt</b>' aufgerufen.
	 * Es wird standardmäßig die Datei "html/help.html" angezeigt.
	 */
	public void hilfeInhalt() {
		final DlgHilfe dlg = new DlgHilfe(frame);
		dlg.pack();
		dlg.setVisible(true);
	}

	/**
	 * Wird von Menü-Handler bei Auswahl von '<b>Hilfe/Programm-Info</b>'
	 * aufgerufen.
	 */
	public void programmInfo() {
		final DlgInfo dlg = new DlgInfo(frame);
		dlg.pack();
		dlg.setVisible(true);
	}

	// -- Methoden des Interface 'KeyListener'
	// -----------------------------------

	public void keyReleased(final KeyEvent e) {
		if (e.getSource().getClass() == DeleteableTextField.class) {
			final DeleteableTextField textField = (DeleteableTextField) e.getSource();
			setzteKoordinatenGemerkteBuchung(textField);
			zeigeGemerkteBuchung(textField.getText());
		}
	}

	public void keyPressed(final KeyEvent e) {
		// nichts zu tun !
	}

	public void keyTyped(final KeyEvent e) {
		// nichts zu tun !
	}

	// -- Methoden des Interface 'ListSelectionListener'
	// ----------------------------

	public void valueChanged(final ListSelectionEvent e) {
		glassPane.setVisible(false);
	}

	public static boolean isMacOSX() {
		final String osName = System.getProperty("os.name");
		return osName.startsWith("Mac OS X");
	}

	// -- MAIN
	// -------------------------------------------------------------------

	public static void main(final String[] args) {
		final String version = System.getProperty("java.specification.version");
		if (version.compareTo("1.5") < 0) {
			JOptionPane.showMessageDialog(
					null,
					RES.getString("message_wrong_java_version1")
						+ " "
						+ version
						+ " "
						+ RES.getString("message_wrong_java_version2"));
			System.exit(0);
		}
		String dateiname = null;
		if ((args.length > 0) && (!args[0].equals(""))) {
			dateiname = args[0];
		}

		// small 'hack' for Apple-users
		if (isMacOSX()) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "jHaushalt");
		}

		final Haushalt haushalt = new Haushalt(dateiname);
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final JFrame mainFrame = haushalt.getFrame();
				mainFrame.pack();
				// Frame mittig im Bildschirm platzieren:
				mainFrame.setLocationRelativeTo(null);
				mainFrame.setVisible(true);
			}
		});
	}

}
