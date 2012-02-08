/*

This file is part of jHaushalt.

jHaushalt is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

jHaushalt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.

(C)opyright 2002-2010 Dr. Lars H. Hahn

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
 * 2011.01.21 BugFix: Auflösung (Resolution) des Druckers gesetzt, um fehlerhafte
 *            Ausdrucke auf einigen Druckern zu verhindern
 * 2009.08.15 BugFix: Fehler beim Laden von Auswertungen ignoriert; ermöglicht
 *            das Laden von neuen Auswertungen in alten Versionen 
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
  private final JList list = new JList(listModel);
  private final JScrollPane listScrollPane = new JScrollPane(list);
  private final JScrollPane graphikScrollPane = new JScrollPane();
  private final JSplitPane split= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, graphikScrollPane);
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
  
  public DlgContainerAuswertung(Haushalt haushalt, Datenbasis db) {
    super(haushalt.getFrame(), res.getString("reports"), true);
    this.haushalt = haushalt;
    this.db = db;

    buttonHoch = new JButton(haushalt.bildLaden("Up16.gif"));
    buttonRunter = new JButton(haushalt.bildLaden("Down16.gif"));
    buttonHinzu = new JButton(res.getString("button_add"), haushalt.bildLaden("Add16.gif"));
  	buttonEntf = new JButton(res.getString("button_delete"), haushalt.bildLaden("Delete16.gif"));
  	buttonEigensch = new JButton(res.getString("button_properties"), haushalt.bildLaden("Properties16.gif"));
    buttonExport = new JButton(res.getString("button_csv_export"), haushalt.bildLaden("Export16.gif"));
    buttonEinstDr = new JButton(res.getString("button_page_setup"), haushalt.bildLaden("PageSetup16.gif"));
    buttonDrucken = new JButton(res.getString("button_print"), haushalt.bildLaden("Print16.gif"));
  	buttonAbbruch = new JButton(res.getString("button_close"));
    
  	// Liste zur Auswahl der Auswertung
  	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  	list.addListSelectionListener(this);
		
    // SplitPane enthält links die Liste der Auswertungen und
    // rechts das Fenster der gewählten Auswertung
    split.setOneTouchExpandable(true);
    split.setDividerLocation(150);
    listScrollPane.setMinimumSize(minimumSize);
    graphikScrollPane.setMinimumSize(minimumSize);
    graphikScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    // Buttons zur ButtonPane hinzufügen:
    paneButton.add(buttonHoch, null);
    paneButton.add(buttonRunter, null);
    paneButton.add(buttonHinzu, null);
    paneButton.add(buttonEntf, null);
    paneButton.add(buttonEigensch, null);
    paneButton.add(buttonExport, null);
    paneButton.add(buttonEinstDr, null);
    paneButton.add(buttonDrucken, null);
    paneButton.add(buttonAbbruch, null);
    buttonEntf.setEnabled(false);
    buttonEigensch.setEnabled(false);
    buttonExport.setEnabled(false);
    buttonDrucken.setEnabled(false);
    buttonHoch.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        sortieren(true);
      }
    });
    buttonRunter.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        sortieren(false);
      }
    });
    buttonHinzu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hinzufuegen();
      }
    });
  	buttonEntf.addActionListener(new ActionListener() {
  		public void actionPerformed(ActionEvent e) {
  			entfernen();
  		}
  	});
  	buttonEigensch.addActionListener(new ActionListener() {
  		public void actionPerformed(ActionEvent e) {
  			eigenschaften();
  		}
  	});
  	buttonExport.addActionListener(new ActionListener() {
  		public void actionPerformed(ActionEvent e) {
  			export();
  		}
  	});
    buttonEinstDr.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        einstellungenDrucker();
      }
    });
    buttonDrucken.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        drucken();
      }
    });
  	buttonAbbruch.addActionListener(new ActionListener() {
  		public void actionPerformed(ActionEvent e) {
  			setVisible(false);
  		}
  	});

    Container contentPane = getContentPane();
    contentPane.add(split, BorderLayout.CENTER);
    paneButton.setLayout(new WrapLayout());
    contentPane.add(paneButton, BorderLayout.SOUTH);
    getRootPane().setDefaultButton(buttonEigensch);
	
  	// Auswahl-Dialog erzeugen 
  	dlg = new DlgAuswertungAuswaehlen(haushalt, db);
  }
  
  /**
   * Ist <code>true</code>, wenn die Auswertungen geändert wurden.
   * @return geändert oder nicht geändert
   */
  public boolean isGeaendert() {
    return geaendert;
  }
  
  public void zeigeDialog() {
    final int anzahl = auswertungen.size();
    if(anzahl > 0) {
      haushalt.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			for(int i=0; i<anzahl; i++) {
				AbstractAuswertung auswertung = (AbstractAuswertung)auswertungen.get(i);
				auswertung.berechneAuswertung();
			}
      list.setSelectedIndex(0);
      haushalt.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    pack();
    setVisible(true);
  }

  private void sortieren(boolean raufRunter) {
    final int idx = list.getSelectedIndex();
    final int anzahl = auswertungen.size();
    if((idx == -1) || (idx >= anzahl))
      JOptionPane.showMessageDialog(null,
        res.getString("no_report_selected"),
        res.getString("reports"),
        JOptionPane.WARNING_MESSAGE);
    else {
      if((idx < anzahl-1) && (raufRunter == false)) {  // RUNTER
        listModel.insertElementAt(listModel.remove(idx),idx+1);
        auswertungen.add(idx+1, auswertungen.remove(idx));
        list.setSelectedIndex(idx+1);
      }
      else if((idx > 0) && (raufRunter == true)) {    // RAUF
        listModel.insertElementAt(listModel.remove(idx),idx-1);
        auswertungen.add(idx-1, auswertungen.remove(idx));
        list.setSelectedIndex(idx-1);
      }
      graphikScrollPane.getViewport().getView().repaint();
    }
  }
  
  protected void hinzufuegen() {
    AbstractAuswertung auswertung = dlg.showDialog();
    if((auswertung != null) && auswertung.zeigeEigenschaften()) {
    	auswertungen.add(auswertung);
    	listModel.addElement(auswertung);
    	list.setSelectedIndex(listModel.getSize()-1);
			graphikScrollPane.getViewport().getView().repaint();
      geaendert = true;
    }
  }
  
  private void entfernen() {
		final int nr = list.getSelectedIndex();
		final int anzahl = auswertungen.size();
		if((nr == -1) || (nr >= anzahl))
			JOptionPane.showMessageDialog(null,
				res.getString("no_report_selected"),
				res.getString("reports"),
				JOptionPane.WARNING_MESSAGE);
		else {
			auswertungen.remove(nr);
			listModel.remove(nr);
			if(anzahl > 1)
			  list.setSelectedIndex(0);
      else {
        graphikScrollPane.getViewport().removeAll();
        graphikScrollPane.getViewport().repaint();
      }
      geaendert = true;
		}
	}

  private void eigenschaften() {
		int nr = list.getSelectedIndex();
		if((nr == -1) || (nr >= auswertungen.size())){
			JOptionPane.showMessageDialog(null,
        res.getString("no_report_selected"),
        res.getString("reports"),
				JOptionPane.WARNING_MESSAGE);
		}
		else {
			AbstractAuswertung auswertung = (AbstractAuswertung)auswertungen.get(nr);
			if(auswertung.zeigeEigenschaften())
        geaendert = true;
      list.repaint();
			graphikScrollPane.getViewport().getView().repaint();
		}
	}
	
  protected void export() {
		int nr = list.getSelectedIndex();
		if((nr == -1) || (nr >= auswertungen.size())){
			JOptionPane.showMessageDialog(null,
        res.getString("no_report_selected"),
        res.getString("reports"),
				JOptionPane.WARNING_MESSAGE);
		}
		else {
			AbstractAuswertung auswertung = (AbstractAuswertung)auswertungen.get(nr);
	    String[][] tabelle = auswertung.getTabelle();
      CsvHandler handler = new CsvHandler(tabelle);
      handler.exportDlg(haushalt.getFrame(), haushalt.getOrdner());
		}
  }
  
  private void einstellungenDrucker() {
    seitenFormat = PrinterJob.getPrinterJob().pageDialog(seitenFormat);
  }

  private void drucken() { 
		int nr = list.getSelectedIndex();
		if((nr == -1) || (nr >= auswertungen.size())){
			JOptionPane.showMessageDialog(null,
        res.getString("no_report_selected"),
        res.getString("reports"),
				JOptionPane.WARNING_MESSAGE);
		}
		else {
			AbstractAuswertung auswertung = (AbstractAuswertung)auswertungen.get(nr);
			PrinterJob job = PrinterJob.getPrinterJob();
			job.setJobName("jHaushalt - Report");
			job.setPrintable(auswertung, seitenFormat);
	    HashPrintRequestAttributeSet set = new HashPrintRequestAttributeSet();
	    PrinterResolution pr = new PrinterResolution(300, 300, ResolutionSyntax.DPI);
	    set.add(pr);
	    if(job.printDialog(set))
	      if(DEBUG)
	        System.out.println("Auswertung drucken: Format " + 
	            seitenFormat.getImageableWidth() + " x " +
	            seitenFormat.getImageableHeight());
  			try {
          job.print();
        } catch (PrinterException e) {
          e.printStackTrace();
        }
		}
	}
	
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			int nr = list.getSelectedIndex();
			if (nr == -1) {
				//Nichts selektiert, disable Eigenschaften + Entfernen
				buttonEntf.setEnabled(false);
				buttonEigensch.setEnabled(false);
				buttonExport.setEnabled(false);
				buttonDrucken.setEnabled(false);
			}
			else {
				buttonEntf.setEnabled(true);
				buttonEigensch.setEnabled(true);
				buttonDrucken.setEnabled(true);
				buttonExport.setEnabled(false);
				if(nr < auswertungen.size()) {
				  AbstractAuswertung auswertung = (AbstractAuswertung)auswertungen.get(nr);
				  if(auswertung.getTabelle() != null)
				    buttonExport.setEnabled(true);
				  graphikScrollPane.getViewport().setView(auswertung);
				}
			}
		}
	}
	
	public void laden(String dateiname) {
    File datei = new File(dateiname);
    if(datei.exists())
      try {
		    FileInputStream fis = new FileInputStream(datei);
		    DataInputStream in = new DataInputStream(fis);
        int size = 0;
        String version = in.readUTF();
        if(version.equals(VERSION_AUSWERTUNG) || version.equals("2.5"))
          size = in.readInt();
        else
          JOptionPane.showMessageDialog(null,
              res.getString("message_reports1")+"\n"+
              res.getString("message_reports2")+" "+VERSION_AUSWERTUNG+" "+
              res.getString("message_reports3")+"\n"+
              res.getString("message_reports4")+"\n"+
              res.getString("message_reports5")+"\n"+
              res.getString("message_reports6"),
              res.getString("hint"),
              JOptionPane.INFORMATION_MESSAGE);
		    for(int i=0; i<size; i++) {
		      String name = in.readUTF();
		      String klasse = in.readUTF();
		      AbstractAuswertung auswertung = AbstractAuswertung.erzeugeAuswertung(klasse, haushalt, db, name);
          if(auswertung != null) {
            auswertung.laden(in);
            auswertungen.add(auswertung);
            listModel.addElement(auswertung);
          }
		    }
		    fis.close();
		    if(DEBUG)
		      System.out.println(""+size+" "+res.getString("reports_loaded"));
      }
    	catch(IOException e) {
    	  e.printStackTrace();
    	}
      geaendert = false;
	}
	
	public void speichern(String dateiname) {
    File datei = new File(dateiname);
		try {
      FileOutputStream fos = new FileOutputStream(datei);
      DataOutputStream out = new DataOutputStream(fos);
      out.writeUTF(VERSION_AUSWERTUNG);
      out.writeInt(auswertungen.size());
			for(int i=0; i<auswertungen.size(); i++) {
				AbstractAuswertung auswertung = (AbstractAuswertung)auswertungen.get(i);
        out.writeUTF(""+auswertung);
        out.writeUTF(auswertung.getClass().getName());
				auswertung.speichern(out);
			}
      out.flush();
      fos.close();
      if(DEBUG)
        System.out.println(""+auswertungen.size()+" "+res.getString("reports_stored"));
		}
		catch(IOException e) {
		  e.printStackTrace();
		}
    geaendert = false;
	}

}
