/*

This file is part of jHaushalt.

jHaushalt is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

jHaushalt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jHaushalt; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

package haushalt.auswertung;


import java.awt.Font;

import haushalt.auswertung.bloecke.LeererBlock;
import haushalt.auswertung.bloecke.TabellenBlock;
import haushalt.auswertung.bloecke.TextBlock;
import haushalt.daten.Datenbasis;
import haushalt.daten.Datum;
import haushalt.daten.Euro;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;
import haushalt.gui.generischerdialog.DatumGDP;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.07.02
 * @since 2.0
 */

/*
 * 2007.07.02 Internationalisierung
 * 2004.08.22 Erste Version
 */
public class BAVermoegenUebersicht extends AbstractBlockAuswertung {
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();

  public static final String ueberschrift = res.getString("table_fortune_overview");

  public BAVermoegenUebersicht(Haushalt haushalt, Datenbasis db, String name) {
    super(haushalt, db, name);
    AbstractGDPane[] panes = new AbstractGDPane[1];
    panes[0] = new DatumGDP(res.getString("date")+":", new Datum());
		erzeugeEigenschaften(haushalt.getFrame(), ueberschrift, panes);
  }
  
  protected String berechneAuswertung(Object[] werte) {
    Datum datum = (Datum)werte[0];
		String[] register = db.getRegisterNamen();
		tabelle = new String[register.length+1][2];
		Euro summe = new Euro();
		for(int i=0; i<register.length; i++) {
			Euro saldo = db.getRegisterSaldo(register[i], datum);
			tabelle[i][0] = register[i];
			tabelle[i][1] = ""+saldo;
			summe.sum(saldo);
		}
		tabelle[register.length][0] = res.getString("total");
		tabelle[register.length][1] = ""+summe;

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		String titel = res.getString("fortune_overview")+" ("+datum+")";
		loescheBloecke();
		TextBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(haushalt.getFontname(), Font.BOLD, haushalt.getFontgroesse()+6));
		addDokumentenBlock(block1);
		addDokumentenBlock(new LeererBlock(1));
		TabellenBlock block2 = new TabellenBlock(tabelle);
		block2.setFont(new Font(haushalt.getFontname(), Font.PLAIN, haushalt.getFontgroesse()));
    final TabellenBlock.Ausrichtung[] attribute = {
        TabellenBlock.Ausrichtung.LINKS, 
        TabellenBlock.Ausrichtung.RECHTS};
    block2.setAusrichtung(attribute);
		addDokumentenBlock(block2);
    return titel;
  }

}
