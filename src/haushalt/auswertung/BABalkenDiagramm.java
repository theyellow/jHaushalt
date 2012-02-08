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
import haushalt.auswertung.bloecke.BalkenBlock;
import haushalt.auswertung.bloecke.LeererBlock;
import haushalt.auswertung.bloecke.TextBlock;
import haushalt.daten.Datenbasis;
import haushalt.daten.Euro;
import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.Jahr;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;
import haushalt.gui.generischerdialog.BooleanGDP;
import haushalt.gui.generischerdialog.EinOderAlleRegisterGDP;
import haushalt.gui.generischerdialog.FarbwahlGDP;
import haushalt.gui.generischerdialog.ZeitraumGDP;
import haushalt.gui.generischerdialog.ZahlGDP;

import java.awt.Color;
import java.awt.Font;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.06.04
 */

/*
 * 2007.06.04 Internationalisierung
 * 2006.02.03 Setzten der Farben hinzugefügt
 * 2004.08.22 Erste Version
 */
public class BABalkenDiagramm extends AbstractBlockAuswertung {
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();
  
	public static final String ueberschrift = res.getString("headline_bar_chart");

  public BABalkenDiagramm(Haushalt haushalt, Datenbasis db, String name) {
    super(haushalt, db, name);
    AbstractGDPane[] panes = new AbstractGDPane[6];
    panes[0] = new ZeitraumGDP(res.getString("first_period")+":", new Jahr(2007));
		panes[1] = new ZahlGDP(res.getString("number_of_periods")+":", new Integer(4));
    panes[2] = new EinOderAlleRegisterGDP(res.getString("register")+":", db, null);
    panes[3] = new BooleanGDP(res.getString("average"), Boolean.TRUE, res.getString("display"));
    panes[4] = new FarbwahlGDP(res.getString("color_income"), haushalt.getFrame(), Color.BLUE);
    panes[5] = new FarbwahlGDP(res.getString("color_expenditure"), haushalt.getFrame(), Color.RED);
		erzeugeEigenschaften(haushalt.getFrame(), ueberschrift, panes);
  }
 
  protected String berechneAuswertung(Object[] werte) {
  	AbstractZeitraum zeitraum = (AbstractZeitraum) werte[0];
  	int anzahlZeitraeume = ((Integer) werte[1]).intValue();
    String register = (String) werte[2];
    Boolean durchschnitt = (Boolean) werte[3];

    AbstractZeitraum tmpZeitraum = zeitraum;
    String[] zeitraumNamen = new String[anzahlZeitraeume];
    Euro[] einnahmen = new Euro[anzahlZeitraeume];
    Euro[] ausgaben = new Euro[anzahlZeitraeume];
		for(int i=0; i<anzahlZeitraeume; i++) {
      zeitraumNamen[i] = ""+tmpZeitraum;
      einnahmen[i] = db.getEinnahmen(tmpZeitraum, register);
      ausgaben[i] = db.getAusgaben(tmpZeitraum, register);
      tmpZeitraum = tmpZeitraum.folgeZeitraum();
		}		 

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		String titel = res.getString("income_expenditure")+" ("+zeitraumNamen[0]+" "+res.getString("to")+" "+zeitraumNamen[anzahlZeitraeume-1];
		if(register == null)
		  titel += ")";
		else
		  titel += ", " + register + ")";
		loescheBloecke();
		AbstractBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(haushalt.getFontname(), Font.BOLD, haushalt.getFontgroesse()+6));
		addDokumentenBlock(block1);
		addDokumentenBlock(new LeererBlock(1));
		BalkenBlock block2 = new BalkenBlock(zeitraumNamen, einnahmen, ausgaben, durchschnitt.booleanValue());
		block2.setFont(new Font(haushalt.getFontname(), Font.PLAIN, haushalt.getFontgroesse()));
    block2.setFarbeEinnahmen((Color) werte[4]);
    block2.setFarbeAusgaben((Color) werte[5]);
		addDokumentenBlock(block2);
    return titel;
	}

}
