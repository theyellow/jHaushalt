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

import haushalt.auswertung.bloecke.LeererBlock;
import haushalt.auswertung.bloecke.TabellenBlock;
import haushalt.auswertung.bloecke.TextBlock;
import haushalt.daten.Datenbasis;
import haushalt.daten.Euro;
import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.Jahr;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;
import haushalt.gui.generischerdialog.EinOderAlleRegisterGDP;
import haushalt.gui.generischerdialog.ZeitraumGDP;
import haushalt.gui.generischerdialog.ZahlGDP;

import java.awt.Font;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5.1/2008.03.10
 */

/*
 * 2008.03.10 BugFix: Fehlender Resource-String
 * 2007.06.04 Internationalisierung
 * 2006.02.14 BugFix: Titel richtig erzeugt
 * 2004.08.22 Erste Version
 */

public class BAEinnahmenAusgaben extends AbstractBlockAuswertung {
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();

  public static final String ueberschrift = res.getString("table_income_expenditure");

  public BAEinnahmenAusgaben(Haushalt haushalt, Datenbasis db, String name) {
    super(haushalt, db, name);
    AbstractGDPane[] panes = new AbstractGDPane[3];
    panes[0] = new ZeitraumGDP(res.getString("first_period")+":", new Jahr(2007));
    panes[1] = new ZahlGDP(res.getString("number_of_periods")+":", new Integer(4));
    panes[2] = new EinOderAlleRegisterGDP(res.getString("register")+":", db, null);
		erzeugeEigenschaften(haushalt.getFrame(), ueberschrift, panes);
  }

  private final String[] kopf = {
      res.getString("period"),
      res.getString("income"),
      res.getString("expenditure"),
      res.getString("difference")
  };

  protected String berechneAuswertung(Object[] werte) {
  	AbstractZeitraum zeitraum = (AbstractZeitraum) werte[0];
  	int anzahlZeitraeume = ((Integer) werte[1]).intValue();
    String register = (String) werte[2];
    tabelle = new String[anzahlZeitraeume+2][4];
    AbstractZeitraum tmpZeitraum = zeitraum;
    Euro summeEinnahmen = new Euro();
    Euro summeAusgaben = new Euro();
    tabelle[0] = kopf;
    for(int i=1; i<=anzahlZeitraeume; i++) {
      Euro einnahmen = db.getEinnahmen(tmpZeitraum, register);
      Euro ausgaben = db.getAusgaben(tmpZeitraum, register);
      tabelle[i][0] = ""+tmpZeitraum;
      tabelle[i][1] = ""+einnahmen;
      tabelle[i][2] = ""+ausgaben;
      tabelle[i][3] = ""+einnahmen.sub(ausgaben);
      summeEinnahmen.sum(einnahmen);
      summeAusgaben.sum(ausgaben);
      tmpZeitraum = tmpZeitraum.folgeZeitraum();
    }
    tabelle[anzahlZeitraeume+1][0] = res.getString("average");
    tabelle[anzahlZeitraeume+1][1] = ""+summeEinnahmen.durch(anzahlZeitraeume);
    tabelle[anzahlZeitraeume+1][2] = ""+summeAusgaben.durch(anzahlZeitraeume);
    tabelle[anzahlZeitraeume+1][3] = ""+summeEinnahmen.sub(summeAusgaben).durch(anzahlZeitraeume);

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		String titel = res.getString("income_expenditure")+" ("+tabelle[1][0]+" "+res.getString("to")+" "+tabelle[anzahlZeitraeume][0];
		if(register == null)
		  titel += ")";
		else
		  titel += ", " + register + ")";
		loescheBloecke();
		TextBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(haushalt.getFontname(), Font.BOLD, haushalt.getFontgroesse()+6));
		addDokumentenBlock(block1);
		addDokumentenBlock(new LeererBlock(1));
		TabellenBlock block2 = new TabellenBlock(tabelle);
		block2.setFont(new Font(haushalt.getFontname(), Font.PLAIN, haushalt.getFontgroesse()));
    final TabellenBlock.Ausrichtung[] attribute = {
        TabellenBlock.Ausrichtung.LINKS, 
        TabellenBlock.Ausrichtung.RECHTS, 
        TabellenBlock.Ausrichtung.RECHTS, 
        TabellenBlock.Ausrichtung.RECHTS};
    block2.setAusrichtung(attribute);
    addDokumentenBlock(block2);
    return titel;
  }
  
}
