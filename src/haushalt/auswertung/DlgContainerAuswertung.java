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

package haushalt.auswertung;

import haushalt.daten.Datenbasis;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.standard.PrinterResolution;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.6/2011.01.25
 */

/*
 * 2011.01.25 BuxFix: Nutzung "WrapLayout" für die ButtonPane
 * 2011.01.21 BugFix: Auflösung (Resolution) des Druckers gesetzt, um
 * fehlerhafte
 * Ausdrucke auf einigen Druckern zu verhindern
 * 2009.08.15 BugFix: Fehler beim Laden von Auswertungen ignoriert; ermöglicht
 * das Laden von neuen Auswertungen in alten Versionen
 * 2009.08.04 BugFix: Anzeigen der Button bei schmalem Fenster
 * 2008.05.15 BugFix: Falscher Fokus beim Sortieren der Auswertungen korrigiert
 * 2008.03.31 Erweiterung: Auswertungen sortieren
 * 2008.02.12 Überprüfung, ob Auswertungen geaendert, hinzugefügt
 * 2007.07.02 Internationalisierung
 * 2007.02.14 Versionsnummer und Versionskontrolle angepasst
 * 2006.06.19 Größe des Dialogs wird nicht mehr lokal festgelegt
 * 2006.06.11 Löschen der Anzeige nach dem Entfernen der letzten Auswertung
 * 2006.02.09 Verlagerung der Drucker-Einstellungen in den Dialog
 * 2006.02.07 Erweiterung um Versionskontrolle
 */

public class DlgContainerAuswertung extends JDialog implements ListSelectionListener {

	private static final long serialVersionUID = 1L;
	private static final boolean DEBUG = false;
	private static final TextResource res = TextResource.get();

	public final static String VERSION_AUSWERTUNG = "2.6";

	private boolean geaendert = false;
	private final Haushalt haushalt;
	private final Datenbasis db;
	private PageFormat seitenFormat = PrinterJob.getPrinterJob().defaultPage();
	private final ArrayList<AbstractAuswertung> auswertungen = new ArrayList<AbstractAuswertung>();

	// Liste und Ausgabefenster:
	private final DefaultListModel listModel = new DefaultListModel();
	private final JList list = new JList(this.listModel);
	private final JScrollPane listScrollPane = new JScrollPane(this.list);
	private final JScrollPane graphikScrollPane = new JScrollPane();
	private final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.listScrollPane,
			this.graphikScrollPane);
	private final Dimension minimumSize = new Dimension(100, 50);

	// ButtonPane und Buttons:
	private final JPanel paneButton = new JPanel();
	private final JButton buttonHoch;
	private final JButton buttonRunter;
	private final JButton buttonHinzu;
	private final JButton buttonEntf;
	private final JButton buttonEigensch;
	private final JButton buttonExport;
	private final JButton buttonEinstDr;
	private final JButton buttonDrucken;
	private final JButton buttonAbbruch;

	private final DlgAuswertungAuswaehlen dlg;

	public DlgContainerAuswertung(final Haushalt haushalt, final Datenbasis db) {
		super(haushalt.getFrame(), res.getString("reports"), true);
		this.haushalt = haushalt;
		this.db = db;

		this.buttonHoch = new JButton(haushalt.bildLaden("Up16.png"));
		this.buttonRunter = new JButton(haushalt.bildLaden("Down16.png"));
		this.buttonHinzu = new JButton(res.getString("button_add"), haushalt.bildLaden("Add16.png"));
		this.buttonEntf = new JButton(res.getString("button_delete"), haushalt.bildLaden("Delete16.png"));
		this.buttonEigensch = new JButton(res.getString("button_properties"), haushalt.bildLaden("Properties16.png"));
		this.buttonExport = new JButton(res.getString("button_csv_export"), haushalt.bildLaden("Export16.png"));
		this.buttonEinstDr = new JButton(res.getString("button_page_setup"), haushalt.bildLaden("PageSetup16.png"));
		this.buttonDrucken = new JButton(res.getString("button_print"), haushalt.bildLaden("Print16.png"));
		this.buttonAbbruch = new JButton(res.getString("button_close"));

		// Liste zur Auswahl der Auswertung
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.list.addListSelectionListener(this);

		// SplitPane enthält links die Liste der Auswertungen und
		// rechts das Fenster der gewählten Auswertung
		this.split.setOneTouchExpandable(true);
		this.split.setDividerLocation(150);
		this.listScrollPane.setMinimumSize(this.minimumSize);
		this.graphikScrollPane.setMinimumSize(this.minimumSize);
		this.graphikScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		// Buttons zur ButtonPane hinzufügen:
		this.paneButton.add(this.buttonHoch, null);
		this.paneButton.add(this.buttonRunter, null);
		this.paneButton.add(this.buttonHinzu, null);
		this.paneButton.add(this.buttonEntf, null);
		this.paneButton.add(this.buttonEigensch, null);
		this.paneButton.add(this.buttonExport, null);
		this.paneButton.add(this.buttonEinstDr, null);
		this.paneButton.add(this.buttonDrucken, null);
		this.paneButton.add(this.buttonAbbruch, null);
		this.buttonEntf.setEnabled(false);
		this.buttonEigensch.setEnabled(false);
		this.buttonExport.setEnabled(false);
		this.buttonDrucken.setEnabled(false);
		this.buttonHoch.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				sortieren(true);
			}
		});
		this.buttonRunter.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				sortieren(false);
			}
		});
		this.buttonHinzu.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				hinzufuegen();
			}
		});
		this.buttonEntf.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				entfernen();
			}
		});
		this.buttonEigensch.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				eigenschaften();
			}
		});
		this.buttonExport.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				export();
			}
		});
		this.buttonEinstDr.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				einstellungenDrucker();
			}
		});
		this.buttonDrucken.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				drucken();
			}
		});
		this.buttonAbbruch.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
			}
		});

		final Container contentPane = getContentPane();
		contentPane.add(this.split, BorderLayout.CENTER);
		this.paneButton.setLayout(new WrapLayout());
		contentPane.add(this.paneButton, BorderLayout.SOUTH);
		getRootPane().setDefaultButton(this.buttonEigensch);

		// Auswahl-Dialog erzeugen
		this.dlg = new DlgAuswertungAuswaehlen(haushalt, db);
	}

	/**
	 * Ist <code>true</code>, wenn die Auswertungen geändert wurden.
	 * 
	 * @return geändert oder nicht geändert
	 */
	public boolean isGeaendert() {
		return this.geaendert;
	}

	public void zeigeDialog() {
		final int anzahl = this.auswertungen.size();
		if (anzahl > 0) {
			this.haushalt.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			for (int i = 0; i < anzahl; i++) {
				final AbstractAuswertung auswertung = this.auswertungen.get(i);
				auswertung.berechneAuswertung();
			}
			this.list.setSelectedIndex(0);
			this.haushalt.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		pack();
		setVisible(true);
	}

	private void sortieren(final boolean raufRunter) {
		final int idx = this.list.getSelectedIndex();
		final int anzahl = this.auswertungen.size();
		if ((idx == -1) || (idx >= anzahl)) {
			JOptionPane.showMessageDialog(null,
					res.getString("no_report_selected"),
					res.getString("reports"),
					JOptionPane.WARNING_MESSAGE);
		}
		else {
			if ((idx < anzahl - 1) && (raufRunter == false)) { // RUNTER
				this.listModel.insertElementAt(this.listModel.remove(idx), idx + 1);
				this.auswertungen.add(idx + 1, this.auswertungen.remove(idx));
				this.list.setSelectedIndex(idx + 1);
			}
			else if ((idx > 0) && (raufRunter == true)) { // RAUF
				this.listModel.insertElementAt(this.listModel.remove(idx), idx - 1);
				this.auswertungen.add(idx - 1, this.auswertungen.remove(idx));
				this.list.setSelectedIndex(idx - 1);
			}
			this.graphikScrollPane.getViewport().getView().repaint();
		}
	}

	protected void hinzufuegen() {
		final AbstractAuswertung auswertung = this.dlg.showDialog();
		if ((auswertung != null) && auswertung.zeigeEigenschaften()) {
			this.auswertungen.add(auswertung);
			this.listModel.addElement(auswertung);
			this.list.setSelectedIndex(this.listModel.getSize() - 1);
			this.graphikScrollPane.getViewport().getView().repaint();
			this.geaendert = true;
		}
	}

	private void entfernen() {
		final int nr = this.list.getSelectedIndex();
		final int anzahl = this.auswertungen.size();
		if ((nr == -1) || (nr >= anzahl)) {
			JOptionPane.showMessageDialog(null,
					res.getString("no_report_selected"),
					res.getString("reports"),
					JOptionPane.WARNING_MESSAGE);
		}
		else {
			this.auswertungen.remove(nr);
			this.listModel.remove(nr);
			if (anzahl > 1) {
				this.list.setSelectedIndex(0);
			}
			else {
				this.graphikScrollPane.getViewport().removeAll();
				this.graphikScrollPane.getViewport().repaint();
			}
			this.geaendert = true;
		}
	}

	private void eigenschaften() {
		final int nr = this.list.getSelectedIndex();
		if ((nr == -1) || (nr >= this.auswertungen.size())) {
			JOptionPane.showMessageDialog(null,
					res.getString("no_report_selected"),
					res.getString("reports"),
					JOptionPane.WARNING_MESSAGE);
		}
		else {
			final AbstractAuswertung auswertung = this.auswertungen.get(nr);
			if (auswertung.zeigeEigenschaften()) {
				this.geaendert = true;
			}
			this.list.repaint();
			this.graphikScrollPane.getViewport().getView().repaint();
		}
	}

	protected void export() {
		final int nr = this.list.getSelectedIndex();
		if ((nr == -1) || (nr >= this.auswertungen.size())) {
			JOptionPane.showMessageDialog(null,
					res.getString("no_report_selected"),
					res.getString("reports"),
					JOptionPane.WARNING_MESSAGE);
		}
		else {
			final AbstractAuswertung auswertung = this.auswertungen.get(nr);
			final String[][] tabelle = auswertung.getTabelle();
			final CsvHandler handler = new CsvHandler(tabelle);
			handler.exportDlg(this.haushalt.getFrame(), this.haushalt.getOrdner());
		}
	}

	private void einstellungenDrucker() {
		this.seitenFormat = PrinterJob.getPrinterJob().pageDialog(this.seitenFormat);
	}

	private void drucken() {
		final int nr = this.list.getSelectedIndex();
		if ((nr == -1) || (nr >= this.auswertungen.size())) {
			JOptionPane.showMessageDialog(null,
					res.getString("no_report_selected"),
					res.getString("reports"),
					JOptionPane.WARNING_MESSAGE);
		}
		else {
			final AbstractAuswertung auswertung = this.auswertungen.get(nr);
			final PrinterJob job = PrinterJob.getPrinterJob();
			job.setJobName("jHaushalt - Report");
			job.setPrintable(auswertung, this.seitenFormat);
			final HashPrintRequestAttributeSet set = new HashPrintRequestAttributeSet();
			final PrinterResolution pr = new PrinterResolution(300, 300, ResolutionSyntax.DPI);
			set.add(pr);
			if (job.printDialog(set)) {
				if (DEBUG) {
					System.out.println("Auswertung drucken: Format " +
							this.seitenFormat.getImageableWidth() + " x " +
							this.seitenFormat.getImageableHeight());
				}
			}
			try {
				job.print();
			}
			catch (final PrinterException e) {
				e.printStackTrace();
			}
		}
	}

	public void valueChanged(final ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			final int nr = this.list.getSelectedIndex();
			if (nr == -1) {
				// Nichts selektiert, disable Eigenschaften + Entfernen
				this.buttonEntf.setEnabled(false);
				this.buttonEigensch.setEnabled(false);
				this.buttonExport.setEnabled(false);
				this.buttonDrucken.setEnabled(false);
			}
			else {
				this.buttonEntf.setEnabled(true);
				this.buttonEigensch.setEnabled(true);
				this.buttonDrucken.setEnabled(true);
				this.buttonExport.setEnabled(false);
				if (nr < this.auswertungen.size()) {
					final AbstractAuswertung auswertung = this.auswertungen.get(nr);
					if (auswertung.getTabelle() != null) {
						this.buttonExport.setEnabled(true);
					}
					this.graphikScrollPane.getViewport().setView(auswertung);
				}
			}
		}
	}

	public void laden(final String dateiname) {
		final File datei = new File(dateiname);
		if (datei.exists()) {
			try {
				final FileInputStream fis = new FileInputStream(datei);
				final DataInputStream in = new DataInputStream(fis);
				int size = 0;
				final String version = in.readUTF();
				if (version.equals(VERSION_AUSWERTUNG) || version.equals("2.5")) {
					size = in.readInt();
				}
				else {
					JOptionPane.showMessageDialog(null,
							res.getString("message_reports1") + "\n" +
									res.getString("message_reports2") + " " + VERSION_AUSWERTUNG + " " +
									res.getString("message_reports3") + "\n" +
									res.getString("message_reports4") + "\n" +
									res.getString("message_reports5") + "\n" +
									res.getString("message_reports6"),
							res.getString("hint"),
							JOptionPane.INFORMATION_MESSAGE);
				}
				for (int i = 0; i < size; i++) {
					final String name = in.readUTF();
					final String klasse = in.readUTF();
					final AbstractAuswertung auswertung = AbstractAuswertung.erzeugeAuswertung(klasse, this.haushalt,
							this.db, name);
					if (auswertung != null) {
						auswertung.laden(in);
						this.auswertungen.add(auswertung);
						this.listModel.addElement(auswertung);
					}
				}
				fis.close();
				if (DEBUG) {
					System.out.println("" + size + " " + res.getString("reports_loaded"));
				}
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
		}
		this.geaendert = false;
	}

	public void speichern(final String dateiname) {
		final File datei = new File(dateiname);
		try {
			final FileOutputStream fos = new FileOutputStream(datei);
			final DataOutputStream out = new DataOutputStream(fos);
			out.writeUTF(VERSION_AUSWERTUNG);
			out.writeInt(this.auswertungen.size());
			for (int i = 0; i < this.auswertungen.size(); i++) {
				final AbstractAuswertung auswertung = this.auswertungen.get(i);
				out.writeUTF("" + auswertung);
				out.writeUTF(auswertung.getClass().getName());
				auswertung.speichern(out);
			}
			out.flush();
			fos.close();
			if (DEBUG) {
				System.out.println("" + this.auswertungen.size() + " " + res.getString("reports_stored"));
			}
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		this.geaendert = false;
	}

}
