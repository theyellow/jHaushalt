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

import haushalt.auswertung.bloecke.AbstractBlock;
import haushalt.auswertung.bloecke.LeererBlock;
import haushalt.auswertung.bloecke.TabellenBlock;
import haushalt.auswertung.bloecke.TextBlock;
import haushalt.daten.Datenbasis;
import haushalt.daten.Datum;
import haushalt.daten.Euro;
import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.Jahr;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;
import haushalt.gui.generischerdialog.GenerischerDialog;
import haushalt.gui.generischerdialog.TextGDP;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.standard.PrinterResolution;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Abstrakte Klasse, die Auswertungen standardisiert.
 * Die Auswertung wird hierzu in einzelne Blöcke aufgeteilt, diese
 * können für unterschiedliche Auswertungen wiederverwertet werden.
 * Darüber hinaus werden die Parameter der Auswertung über einen
 * generischen Dialog eingestellt, der ebenfalls wiederverwendet werden
 * kann. In der abgeleiteten Auswertung muss lediglich die Methode
 * 'berechneAuswertung' überladen werden.
 * @see AbstractBlockAuswertung#berechneAuswertung(Object[])
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.6/2011.02.08
 * @since 2.0
 */

/*
 * 2011.02.08 BuxFix: Laden/Speichern von Auswertungen ohne spezielle Eigenschaften
 *            ermöglicht
 * 2010.09.19 Graphics2D in der Methode 'print' verwendet; DoubleBuffering 
 *            beim Drucken ausgeschaltet
 * 2009.08.10 Auswertungen ohne spezifische Eigenschaften ermöglicht
 * 2008.01.17 Internationalisierung
 * 2007.05.24 BugFix: Letzte Zeile eines Blocks wurde nicht ausgedruckt
 * 2004.08.22 Erste Version (2.0)
 */
public abstract class AbstractBlockAuswertung extends AbstractAuswertung {
  private static final long serialVersionUID = 1L;
  private static final boolean DEBUG = false;
  private static final TextResource res = TextResource.get();

  protected final Haushalt haushalt;
  private final ArrayList<AbstractBlock> bloecke = new ArrayList<AbstractBlock>();
  private final Image wasserzeichen;

  public AbstractBlockAuswertung(Haushalt haushalt, Datenbasis db, String name) {
    super(db, name);
    this.haushalt = haushalt;
		URLClassLoader urlLoader = (URLClassLoader)getClass().getClassLoader();
		URL fileLoc = urlLoader.findResource("res/jhh-grau.gif");
		wasserzeichen = Toolkit.getDefaultToolkit().createImage(fileLoc);
  }

  public void addDokumentenBlock(AbstractBlock block) {
    bloecke.add(block);
    if(DEBUG)
      System.out.println("AbstractBlockAuswertung: Block hinzugefügt.");
  }
  
  public void loescheBloecke() {
    bloecke.clear();
    if(DEBUG)
      System.out.println("AbstractBlockAuswertung: Alle Blöcke gelöscht.");
  }

  public void paintComponent(Graphics g) {
    setBackground(Color.white);
    super.paintComponent(g); // paint background
    int breite = getSize().width - 2;
    int y = 0;
    for(int i=0; i<bloecke.size(); i++) {
      AbstractBlock block = (AbstractBlock) bloecke.get(i);
      y += block.paint(g, 0, y, breite);
    }
    setPreferredSize(new Dimension(breite, y));
    revalidate();
  }
  
  private int seitenZaehler = 0;
  private int blockZaehler = 0;
  private int zeilenZaehler = 0;
  private int blockStart = 0;
  private int zeilenStart = 0;

  public int print(Graphics g, PageFormat seitenFormat, int gesuchteSeite) throws PrinterException {
    if(DEBUG) {
        System.out.println("AbstractBlockAuswertung: Drucke Seite "+gesuchteSeite);
        System.out.println("AbstractBlockAuswertung: Pagesize = "+
        		seitenFormat.getImageableWidth()+" x "+seitenFormat.getImageableHeight());
        System.out.println("AbstractBlockAuswertung: PanelSize = "+getSize());
    }
    if(gesuchteSeite == 0) {
      // Die erste Seite ist gewünscht => alle Zähler zurücksetzen
      seitenZaehler = 0;
      blockZaehler = 0;
      zeilenZaehler = 0;
      blockStart = 0;
      zeilenStart = 0;
    }
    else if(gesuchteSeite == seitenZaehler) {
      // Die aktuelle Seite wird wiederholt abgefragt => Zähler wieder auf die
      // Anfangswerte
      blockZaehler = blockStart;
      zeilenZaehler = zeilenStart;
    }
    else if(gesuchteSeite == (seitenZaehler+1)) {
      // Die nächste Seite ist gewünscht
      seitenZaehler ++;
      blockStart = blockZaehler;
      zeilenStart = zeilenZaehler;
    }
    else
      throw new PrinterException("Seiten nicht in der richtigen Reihenfolge angefodert.");
    
    if(blockZaehler == bloecke.size())
      return NO_SUCH_PAGE;
   
    int hoehe = (int)seitenFormat.getImageableHeight() - 2;
    int breite = (int)seitenFormat.getImageableWidth() - 2;
    int xStart = (int)seitenFormat.getImageableX() + 1;
    int yStart = (int)seitenFormat.getImageableY() + 1;

    setDoubleBuffered(false);
    Graphics2D g2d = (Graphics2D)g;

    if(DEBUG) {
      g2d.setColor(Color.RED);
      g2d.drawRect(xStart, yStart, breite, hoehe);
      g2d.drawLine(xStart, yStart, xStart+breite, yStart+hoehe);
      g2d.drawLine(xStart, yStart+hoehe, xStart+breite, yStart);
      g2d.setColor(Color.BLACK);
    }
    
    String fontname = "SansSerif";
    int fontgroesse = 12;
    if(haushalt != null) {
      fontname = haushalt.getFontname();
      fontgroesse = haushalt.getFontgroesse();
    }
    // Fusszeile drucken
    g2d.setFont(new Font(fontname, Font.PLAIN, fontgroesse-2));
    int textHoehe = g2d.getFontMetrics().getHeight();
    int bildBreite = textHoehe*169 / 32;
    String seitenzahl = ""+new Datum()+" / Seite "+(gesuchteSeite+1);
    int y = yStart + hoehe - g2d.getFontMetrics().getDescent();
    int x = xStart + breite - g2d.getFontMetrics().stringWidth(seitenzahl);
    g2d.drawImage(wasserzeichen, xStart, yStart+hoehe-textHoehe, bildBreite, textHoehe, Color.white, null);
    g2d.drawString(seitenzahl, x, y);
    hoehe -= textHoehe;
    if(DEBUG){
      g2d.setColor(Color.BLUE);
      g2d.drawRect(xStart, y-textHoehe, breite, textHoehe);
      g2d.setColor(Color.BLACK);
    }
    
    y = 0;
    int verbrauchteHoehe;
    do {
      if(DEBUG)
        System.out.println("AbstractBlockAuswertung: Block "+blockZaehler+" wird gedruckt.");
      AbstractBlock block = bloecke.get(blockZaehler);
      // Aktuelle Zeile ausgeben:
      verbrauchteHoehe = block.print(g2d, zeilenZaehler, xStart, yStart+y, hoehe-y, breite);
      if(DEBUG) {
        g2d.setColor(Color.GREEN);
        g2d.drawRect(xStart, yStart+y, hoehe-y-1, breite-1);
        g2d.drawString("#"+blockZaehler, xStart, yStart+y);
        g2d.setColor(Color.BLACK);
      }
      
      if(verbrauchteHoehe == -1) {
        // keine Zeilen mehr in diesem Block
        blockZaehler ++;
        zeilenZaehler = 0;
      }
      else if(verbrauchteHoehe == 0) {
        // Zeile passt nicht in die (Rest-)Höhe
        if (y == 0)
        // ... und wir sind am Anfang der Seite!
          throw new PrinterException("Block ist zu hoch für die Seite.");
        // Wir versuchen es auf der nächsten Seite. Achtung: Zeilenzäler nicht inkrementieren!
      }
      else {
        // PRIMA! Nächste Zeile ...
        y += verbrauchteHoehe;
        zeilenZaehler ++;
      }
    } while((blockZaehler < bloecke.size()) && (verbrauchteHoehe != 0));
            // Es gibt noch Blöcke und es wurde etwas ausgegeben --> also weiter!

    setDoubleBuffered(true);
    return PAGE_EXISTS;
  }

  protected AbstractGDPane[] panes;
  private TextGDP namenPane;
  private GenerischerDialog dlgEigenschaften;
  
	abstract protected String berechneAuswertung(Object[] werte);

  /**
   * Ermöglicht die Auswertung neu zu berechnen, wenn sich die allgemeinen
   * Eigenschaften ändern.
   */
	public final String berechneAuswertung() {
    Object[] werte = null;
    long start = new Date().getTime();
    if(panes != null) {
      werte = new Object[panes.length];
      for(int i=0; i<panes.length; i++)
        werte[i] = panes[i].getWert();
    }
    String titel = berechneAuswertung(werte);
    if(DEBUG)
      System.out.println(toString()+" in "+(new Date().getTime()-start)+" ms berechnet.");
    return titel;
  }
	
	public final void erzeugeEigenschaften(JFrame frame, String ueberschrift, AbstractGDPane[] gdPanes) {
		this.panes = gdPanes;
    dlgEigenschaften = new GenerischerDialog(ueberschrift, frame);
		if(gdPanes != null) {
      Object[] werte = new Object[panes.length];
  		for(int i=0; i<panes.length; i++) {
  		  dlgEigenschaften.addPane(panes[i]);   
  		  werte[i] = panes[i].getWert();
  		}
		}
		namenPane = new TextGDP(res.getString("report_name"), toString());
		dlgEigenschaften.addPane(namenPane);
  }
  
  public final boolean zeigeEigenschaften() {
		if(panes != null)
		  for(int i=0; i<panes.length; i++)
		    panes[i].refreshRegisterUndKategorien();
    if(dlgEigenschaften.showDialog()) {
      String titel = berechneAuswertung();
      // Wenn der Benutzer die Auswertung nicht benannt hat, wird sie mit
      // einem automatischen Namen versehen.
      if(namenPane.getWert().toString().startsWith(res.getString("unnamed"))) {
        ((JTextField)namenPane.getZentraleKomponente()).setText(titel);
        setAuswertungName(titel);
      }
      else
        setAuswertungName(""+namenPane.getWert());
      return true;
    }
    return false;
  }

  public final void laden(DataInputStream in) throws IOException {
    if(panes != null)
  		for(int i=0; i<panes.length; i++)
	  	  panes[i].laden(in);
  }

  public final void speichern(DataOutputStream out) throws IOException {
    if(panes != null)
      for(int i=0; i<panes.length; i++)
	  	  panes[i].speichern(out);
  }
  
  // -- TEST-MAIN -------------------------------------------------------------------
  
  public static void main(String[] args) {
    class BATest extends AbstractBlockAuswertung {
      private static final long serialVersionUID = 1L;

      public BATest() {
        super(null, null, "TEST");
      }

      protected String berechneAuswertung(Object[] werte) {
        AbstractZeitraum zeitraum = new Jahr(2010);
        String register = "Testregister";
        String[][] tabelle = {
            {"Datum 1", "Buchungstext 1", "Kategorie 1", "Wert 1"},
            {"Datum 2", "Buchungstext 2", "Kategorie 2", "Wert 2"},
            {"Datum 3", "Buchungstext 3", "Kategorie 3", "Wert 3"},
            {"Datum 4", "Buchungstext 4", "Kategorie 4", "Wert 4"},
            {"Datum 5", "Buchungstext 5", "Kategorie 5", "Wert 5"},
            {"Datum 6", "Buchungstext 6", "Kategorie 6", "Wert 6"},
            {"Datum 7", "Buchungstext 7", "Kategorie 7", "Wert 7"},
            {"Datum 8", "Buchungstext 8", "Kategorie 8", "Wert 8"},
            {"Datum 9", "Buchungstext 9", "Kategorie 9", "Wert 9"}
        };
        String fontname = "SansSerif";
        Euro summe = new Euro(99.99);
        String titel = "Ausgewählte Buchungen ("+zeitraum;
        titel += " / "+register+")";
        loescheBloecke();
        AbstractBlock block1 = new TextBlock(titel);
        Font font = new Font(fontname, Font.BOLD, 18);
        block1.setFont(font);
        addDokumentenBlock(block1);
        addDokumentenBlock(new LeererBlock(1));
        final double[] relTabs = {0.0, 12.0, 50.0, 80.0};
        final TabellenBlock.Ausrichtung[] attribute = {
            TabellenBlock.Ausrichtung.LINKS, 
            TabellenBlock.Ausrichtung.LINKS, 
            TabellenBlock.Ausrichtung.LINKS, 
            TabellenBlock.Ausrichtung.RECHTS};
        TabellenBlock block2 = new TabellenBlock(tabelle);
        block2.setFont(new Font(fontname, Font.PLAIN, 12));
        block2.setRelTabs(relTabs);
        block2.setLinienFarbe("Weiß");
        block2.setAusrichtung(attribute);
        addDokumentenBlock(block2);
        String[][] text = {{"", res.getString("total")+":", "", ""+summe}};
        TabellenBlock block3 = new TabellenBlock(text);
        block3.setFont(new Font(fontname, Font.ITALIC, 12));
        block3.setRelTabs(relTabs);
        block3.setHgFarbe("Grau");
        block3.setLinienFarbe("Grau");
        block3.setAusrichtung(attribute);
        addDokumentenBlock(block3);
        return titel;
      }
    }
    
    final BATest test = new BATest();
    final PageFormat seitenFormat = PrinterJob.getPrinterJob().defaultPage();
    test.berechneAuswertung();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame();
        frame.getContentPane().add(test);
        frame.pack();
        frame.setVisible(true);
       }
    });
    PrinterJob job = PrinterJob.getPrinterJob();
    job.setJobName("Test - Report");
    job.setPrintable(test, seitenFormat);
    HashPrintRequestAttributeSet set = new HashPrintRequestAttributeSet();
    PrinterResolution pr = new PrinterResolution(300, 300, ResolutionSyntax.DPI);
    set.add(pr);
    if(job.printDialog(set)) {
      try {
        job.print();
      } catch (PrinterException e) {
        e.printStackTrace();
      }
    }
  }
  
}
