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

package haushalt.gui.dialoge;

import haushalt.auswertung.FarbPaletten;
import haushalt.gui.DatumField;
import haushalt.gui.DeleteableTextField;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;

/**
 * Dialog zum Ändern der Optionen.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5.4/2008.05.14
 */

/*
 * 2008.05.14 Reiter für benutzer-definierte Farben
 * 2008.04.15 Farbe für zukünftige Buchungen
 * 2008.01.22 Umstellung der Auswahl des Delkeys auf Index
 * 2007.05.30 Internationalisierung
 * 2006.01.27 Entfernen der globalen Option
 * "Unterkategorien verwenden"
 * 2005.03.10 Erweiterung: Gemerkte Buchungen ab Datum
 */

public class DlgOptionen extends JDialog {

	private static final boolean DEBUG = false;
	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();
	private static final Logger LOGGER = Logger.getLogger(DlgOptionen.class.getName());

	private final Locale[] listeLocales = Locale.getAvailableLocales();

	// GUI-Komponenten
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private final JPanel allgemeinPane = new JPanel();
	private final JComboBox sprache;
	private final DeleteableTextField ordner = new DeleteableTextField();
	private final DeleteableTextField waehrung = new DeleteableTextField();
	private final JCheckBox euroImport = new JCheckBox();

	private final JPanel registerPane = new JPanel();
	private final String[] reiterAuswahl = {"BOTTOM", "TOP", "LEFT", "RIGHT"};
	private final JComboBox reiter = new JComboBox(this.reiterAuswahl);
	private final JCheckBox gemerkte = new JCheckBox();
	private final DatumField startDatum = new DatumField();
	private final String[] deltasteAuswahl = {
			RES.getString("delkey_selection0"), RES.getString("delkey_selection1"), RES.getString("delkey_selection2"),
			RES.getString("delkey_selection3")};
	private final JComboBox deltaste = new JComboBox(this.deltasteAuswahl);
	private final JButton farbeSelektion = new JButton();
	private final JButton farbeGitter = new JButton();
	private final JButton farbeZukunft = new JButton();

	private final JPanel auswertungPane = new JPanel();
	private final String[] fontAuswahl = {"SansSerif", "Serif", "Monospaced"};
	private final JComboBox font = new JComboBox(this.fontAuswahl);
	private final DeleteableTextField punkt = new DeleteableTextField();

	private final JPanel customPane = new JPanel();
	private final JButton buttonAdd;
	private final JButton buttonDelete;
	private final JButton buttonEdit;
	private final DefaultListModel listModel = new DefaultListModel();
	private final JList customColor = new JList(this.listModel);

	private final JPanel buttonPane = new JPanel();
	private final JButton buttonOK = new JButton(RES.getString("button_ok"));
	private final JButton buttonAbbruch = new JButton(RES.getString("button_cancel"));

	// Daten
	private final Properties properties;

	public DlgOptionen(final Haushalt haushalt, final Properties properties) {
		super(haushalt.getFrame(), RES.getString("options"), true); // = modal
		final String[] sprachen = new String[this.listeLocales.length];
		for (int i = 0; i < this.listeLocales.length; i++) {
			sprachen[i] = this.listeLocales[i].getDisplayName();
		}
		this.sprache = new JComboBox(sprachen);
		this.properties = properties;
		this.allgemeinPane.setLayout(new GridLayout(0, 2));
		this.allgemeinPane.add(new JLabel(RES.getString("language_hint") + ":"));
		this.allgemeinPane.add(this.sprache);
		this.allgemeinPane.add(new JLabel(RES.getString("working_directory") + ":"));
		this.allgemeinPane.add(this.ordner);
		this.allgemeinPane.add(new JLabel(RES.getString("currency_symbol") + ":"));
		this.allgemeinPane.add(this.waehrung);
		this.allgemeinPane.add(new JLabel(RES.getString("import_currency") + ":"));
		this.allgemeinPane.add(this.euroImport);
		this.allgemeinPane.add(new JLabel(RES.getString("start_date_remembered_bookings") + ":"));
		this.allgemeinPane.add(this.startDatum);
		this.allgemeinPane.add(new JLabel(""));
		this.tabbedPane.add(RES.getString("general"), this.allgemeinPane);

		this.registerPane.setLayout(new GridLayout(0, 2));
		this.registerPane.add(new JLabel(RES.getString("tab_placement") + ":"));
		this.registerPane.add(this.reiter);
		this.registerPane.add(new JLabel(RES.getString("use_remembered_bookings") + ":"));
		this.registerPane.add(this.gemerkte);
		this.registerPane.add(new JLabel(RES.getString("key_clear_cell") + ":"));
		this.registerPane.add(this.deltaste);
		this.registerPane.add(new JLabel(RES.getString("background_color_selection") + ":"));
		this.registerPane.add(this.farbeSelektion);
		this.registerPane.add(new JLabel(RES.getString("grid_color") + ":"));
		this.registerPane.add(this.farbeGitter);
		this.registerPane.add(new JLabel(RES.getString("future_color") + ":"));
		this.registerPane.add(this.farbeZukunft);
		this.tabbedPane.add(RES.getString("register"), this.registerPane);

		this.auswertungPane.setLayout(new GridLayout(0, 2));
		this.auswertungPane.add(new JLabel(RES.getString("font") + ":"));
		this.auswertungPane.add(this.font);
		this.auswertungPane.add(new JLabel(RES.getString("font_size") + ":"));
		this.auswertungPane.add(this.punkt);
		this.auswertungPane.add(new JLabel(""));
		this.auswertungPane.add(new JLabel(""));
		this.auswertungPane.add(new JLabel(""));
		this.auswertungPane.add(new JLabel(""));
		this.auswertungPane.add(new JLabel(""));
		this.tabbedPane.add(RES.getString("report"), this.auswertungPane);

		this.customColor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.customColor.setCellRenderer(new ColorRenderer(true));
		this.customColor.setVisibleRowCount(6);
		final MouseListener mouseListener = new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if ((e.getClickCount() == 2) && !DlgOptionen.this.customColor.isSelectionEmpty()) {
					final int idx = DlgOptionen.this.customColor.locationToIndex(e.getPoint());
					final Color alteFarbe = (Color) DlgOptionen.this.customColor.getSelectedValue();
					final Color neueFarbe = JColorChooser.showDialog(
							haushalt.getFrame(),
							RES.getString("custom_color"),
							alteFarbe);
					if (neueFarbe != null) {
						DlgOptionen.this.listModel.removeElementAt(idx);
						DlgOptionen.this.listModel.insertElementAt(neueFarbe, idx);
					}
				}
			}
		};
		this.customColor.addMouseListener(mouseListener);
		final JScrollPane listScrollPane = new JScrollPane(this.customColor);
		this.customPane.add(listScrollPane);
		this.buttonAdd = new JButton(RES.getString("button_add"), haushalt.bildLaden("Add16.png"));
		this.customPane.add(this.buttonAdd);
		this.buttonDelete = new JButton(RES.getString("button_delete"), haushalt.bildLaden("Delete16.png"));
		this.customPane.add(this.buttonDelete);
		this.buttonEdit = new JButton(RES.getString("button_edit"), haushalt.bildLaden("Edit16.png"));
		this.customPane.add(this.buttonEdit);
		this.tabbedPane.add(RES.getString("custom_color"), this.customPane);

		this.farbeSelektion.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				final Color farbe = JColorChooser.showDialog(
						haushalt.getFrame(),
						RES.getString("background_color_selection"),
						DlgOptionen.this.farbeSelektion.getBackground());
				if (farbe != null) {
					DlgOptionen.this.farbeSelektion.setText("#" + Integer.toHexString(farbe.getRGB()).toUpperCase());
					DlgOptionen.this.farbeSelektion.setBackground(farbe);
				}
			}
		});
		this.farbeGitter.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				final Color farbe = JColorChooser.showDialog(
						haushalt.getFrame(),
						RES.getString("grid_color"),
						DlgOptionen.this.farbeGitter.getBackground());
				if (farbe != null) {
					DlgOptionen.this.farbeGitter.setText("#" + Integer.toHexString(farbe.getRGB()).toUpperCase());
					DlgOptionen.this.farbeGitter.setBackground(farbe);
				}
			}
		});
		this.farbeZukunft.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				final Color farbe = JColorChooser.showDialog(
						haushalt.getFrame(),
						RES.getString("future_color"),
						DlgOptionen.this.farbeZukunft.getBackground());
				if (farbe != null) {
					DlgOptionen.this.farbeZukunft.setText("#" + Integer.toHexString(farbe.getRGB()).toUpperCase());
					DlgOptionen.this.farbeZukunft.setBackground(farbe);
				}
			}
		});
		this.buttonAdd.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				final Color neueFarbe = JColorChooser.showDialog(haushalt.getFrame(), RES.getString("custom_color"), Color.WHITE);
				if (neueFarbe != null) {
					DlgOptionen.this.listModel.addElement(neueFarbe);
				}
			}
		});
		this.buttonDelete.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				if (!DlgOptionen.this.customColor.isSelectionEmpty()) {
					final int idx = DlgOptionen.this.customColor.getSelectedIndex();
					DlgOptionen.this.listModel.removeElementAt(idx);
				}
			}
		});
		this.buttonEdit.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				if (!DlgOptionen.this.customColor.isSelectionEmpty()) {
					final int idx = DlgOptionen.this.customColor.getSelectedIndex();
					final Color alteFarbe = (Color) DlgOptionen.this.customColor.getSelectedValue();
					final Color neueFarbe = JColorChooser.showDialog(
							haushalt.getFrame(),
							RES.getString("custom_color"),
							alteFarbe);
					if (neueFarbe != null) {
						DlgOptionen.this.listModel.removeElementAt(idx);
						DlgOptionen.this.listModel.insertElementAt(neueFarbe, idx);
					}
				}
			}
		});
		this.buttonOK.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				exit();
				setVisible(false);
			}
		});
		this.buttonAbbruch.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
			}
		});
		this.buttonPane.add(this.buttonOK);
		this.buttonPane.add(this.buttonAbbruch);
		final Container contentPane = getContentPane();
		contentPane.add(this.tabbedPane, BorderLayout.CENTER);
		contentPane.add(this.buttonPane, BorderLayout.SOUTH);
		getRootPane().setDefaultButton(this.buttonOK);
	}

	/**
	 * Zeigt den Dialog auf dem Bildschirm an, wenn er noch nicht sichtbar ist.
	 */
	public void showDialog() {
		init();
		setLocationRelativeTo(getOwner());
		pack();
		setVisible(true);
	}

	private void init() {
		final String localeName = RES.getLocale().getDisplayName();
		for (int i = 0; i < this.sprache.getItemCount(); i++) {
			if (localeName.equals(this.sprache.getItemAt(i))) {
				this.sprache.setSelectedIndex(i);
			}
		}
		this.ordner.setText(this.properties.getProperty("jhh.ordner"));
		this.font.setSelectedItem(this.properties.getProperty("jhh.opt.font", "SansSerif"));
		this.punkt.setText(this.properties.getProperty("jhh.opt.punkt", "12"));
		this.gemerkte.setSelected(Boolean.valueOf(this.properties.getProperty("jhh.opt.gemerkte", "true")).booleanValue());
		this.startDatum.setText(this.properties.getProperty("jhh.opt.startdatum", "01.01.00"));
		this.waehrung.setText(this.properties.getProperty("jhh.opt.waehrung", "€"));
		final int idx = Integer.parseInt(this.properties.getProperty("jhh.opt.deltaste", "0"));
		this.deltaste.setSelectedIndex(idx);
		this.reiter.setSelectedItem(this.properties.getProperty("jhh.opt.reiter", "BOTTOM"));
		this.euroImport.setSelected(Boolean.valueOf(this.properties.getProperty("jhh.opt.euroimport", "true")).booleanValue());
		int farbe = new Integer(this.properties.getProperty("jhh.opt.selektion", "12632256")).intValue(); // #c0c0c0
		this.farbeSelektion.setText(Integer.toHexString(farbe).toUpperCase());
		this.farbeSelektion.setBackground(new Color(farbe));
		farbe = new Integer(this.properties.getProperty("jhh.opt.gitter", "10066329")).intValue(); // #999999
		this.farbeGitter.setText(Integer.toHexString(farbe).toUpperCase());
		this.farbeGitter.setBackground(new Color(farbe));
		farbe = new Integer(this.properties.getProperty("jhh.opt.zukunft", "16777088")).intValue(); // #ffff80
		this.farbeZukunft.setText(Integer.toHexString(farbe).toUpperCase());
		this.farbeZukunft.setBackground(new Color(farbe));
		final int anz = FarbPaletten.setCustomColor(this.properties.getProperty("jhh.opt.custom", "16776960"));
		this.listModel.removeAllElements();
		for (int i = 0; i < anz; i++) {
			this.listModel.addElement(FarbPaletten.getFarbe(i, "Custom"));
		}
		if (DEBUG) {
			LOGGER.info("Anzahl Custom Colors: " + anz);
		}
	}

	protected void exit() {
		this.properties.setProperty("jhh.opt.sprache", "" + this.listeLocales[this.sprache.getSelectedIndex()]);
		this.properties.setProperty("jhh.ordner", this.ordner.getText());
		this.properties.setProperty("jhh.opt.font", "" + this.font.getSelectedItem());
		this.properties.setProperty("jhh.opt.punkt", this.punkt.getText());
		this.properties.setProperty("jhh.opt.gemerkte", "" + this.gemerkte.isSelected());
		this.properties.setProperty("jhh.opt.startdatum", "" + this.startDatum.getText());
		this.properties.setProperty("jhh.opt.waehrung", this.waehrung.getText());
		this.properties.setProperty("jhh.opt.deltaste", "" + this.deltaste.getSelectedIndex());
		this.properties.setProperty("jhh.opt.reiter", "" + this.reiter.getSelectedItem());
		this.properties.setProperty("jhh.opt.euroimport", "" + this.euroImport.isSelected());
		this.properties.setProperty("jhh.opt.selektion", "" + this.farbeSelektion.getBackground().getRGB());
		this.properties.setProperty("jhh.opt.gitter", "" + this.farbeGitter.getBackground().getRGB());
		this.properties.setProperty("jhh.opt.zukunft", "" + this.farbeZukunft.getBackground().getRGB());
		final Color[] farben = new Color[this.listModel.getSize()];
		for (int i = 0; i < this.listModel.getSize(); i++) {
			farben[i] = (Color) this.listModel.getElementAt(i);
		}
		FarbPaletten.setCustomColor(farben);
		this.properties.setProperty("jhh.opt.custom", "" + FarbPaletten.getCustomColor());
		if (DEBUG) {
			LOGGER.info(RES.getString("option_set"));
			this.properties.list(System.out);
		}
	}

}
