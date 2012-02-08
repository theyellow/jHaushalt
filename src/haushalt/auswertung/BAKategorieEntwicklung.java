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
import haushalt.auswertung.bloecke.EntwicklungBlock;
import haushalt.auswertung.bloecke.LeererBlock;
import haushalt.auswertung.bloecke.TabellenBlock;
import haushalt.auswertung.bloecke.TextBlock;
import haushalt.daten.Datenbasis;
import haushalt.daten.Euro;
import haushalt.daten.EinzelKategorie;
import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.Jahr;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;
import haushalt.gui.generischerdialog.BooleanGDP;
import haushalt.gui.generischerdialog.EinOderAlleRegisterGDP;
import haushalt.gui.generischerdialog.MehrereKategorienGDP;
import haushalt.gui.generischerdialog.ZeitraumGDP;
import haushalt.gui.generischerdialog.ZahlGDP;
import haushalt.gui.generischerdialog.TextArrayGDP;

import java.awt.Font;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.06.04
 * @since 2.0
 */

/*
 * 2007.06.05 Internationalisierung
 * 2004.08.22 Erste Version
 */
public class BAKategorieEntwicklung extends AbstractBlockAuswertung {	
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();

  public static final String ueberschrift = res.getString("chart_development_categories");

  public BAKategorieEntwicklung(Haushalt haushalt, Datenbasis db, String name) {
    super(haushalt, db, name);
    AbstractGDPane[] panes = new AbstractGDPane[6];
    panes[0] = new ZeitraumGDP(res.getString("first_period")+":", new Jahr(2007));
    panes[1] = new ZahlGDP(res.getString("number_of_periods")+":", new Integer(4));
    panes[2] = new EinOderAlleRegisterGDP(res.getString("register")+":", db, null);
    panes[3] = new MehrereKategorienGDP(res.getString("categories")+":", db);
    panes[4] = new TextArrayGDP(res.getString("color_scheme")+":", FarbPaletten.palettenNamen, "Slow");
		panes[5] = new BooleanGDP(res.getString("expenditure"), new Boolean(true), res.getString("show_as_negative_values"));
    erzeugeEigenschaften(haushalt.getFrame(), ueberschrift, panes);
  }

  protected String berechneAuswertung(Object[] werte) {
  	AbstractZeitraum zeitraum = (AbstractZeitraum) werte[0];
  	int anzahlZeitraeume = ((Integer) werte[1]).intValue();
    String register = (String) werte[2];
    EinzelKategorie[] kategorien = (EinzelKategorie[]) werte[3];
    String farbschema = (String) werte[4];
    Boolean negativeWerte = (Boolean) werte[5];
    
    boolean unterkategorienVerwenden = ((MehrereKategorienGDP)panes[3]).getUnterkategorienVerwenden();
    int anzahlKategorien = kategorien.length;
    String[] zeitraumNamen = new String[anzahlZeitraeume];
    Euro[][] betraege = new Euro[anzahlZeitraeume][anzahlKategorien];
		AbstractZeitraum tmpZeitraum = zeitraum;
    for(int z=0; z < anzahlZeitraeume; z++) {
      zeitraumNamen[z] = ""+tmpZeitraum;
      betraege[z] = db.getKategorieSalden(kategorien, tmpZeitraum, register, unterkategorienVerwenden);
      tmpZeitraum = tmpZeitraum.folgeZeitraum();      
    }

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		String titel = res.getString("category_development")+" ("+zeitraumNamen[0]+" "+res.getString("to")+" "+zeitraumNamen[anzahlZeitraeume-1];
		if(register == null)
		  titel += ")";
		else
		  titel += ", " + register + ")";
		loescheBloecke();
		AbstractBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(haushalt.getFontname(), Font.BOLD, haushalt.getFontgroesse()+6));
		addDokumentenBlock(block1);
		addDokumentenBlock(new LeererBlock(1));
		AbstractBlock block2 = new EntwicklungBlock(kategorien, zeitraumNamen, betraege, farbschema, negativeWerte);
		block2.setFont(new Font(haushalt.getFontname(), Font.PLAIN, haushalt.getFontgroesse()));
		addDokumentenBlock(block2);
		addDokumentenBlock(new LeererBlock(1));
		int anzahl = kategorien.length;
		if(anzahl > 0) {
			tabelle = new String[anzahl][1];
			for(int i=0; i<anzahl; i++)
			  tabelle[i][0] = ""+kategorien[i];
	    final double[] relTabs = {50.0};
	    TabellenBlock block3 = new TabellenBlock(tabelle);
			block3.setFont(new Font(haushalt.getFontname(), Font.PLAIN, haushalt.getFontgroesse()));
	    block3.setRelTabs(relTabs);
	    block3.setHgFarbe(farbschema);
	    block3.setLinienFarbe("Weiß");
	    addDokumentenBlock(block3);
		}
		else
		  addDokumentenBlock(new TextBlock(res.getString("no_category_selected")));
    return titel;
 }

}
