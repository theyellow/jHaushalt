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

package haushalt.auswertung.planung;

import java.awt.Font;

import haushalt.auswertung.AbstractBlockAuswertung;
import haushalt.auswertung.bloecke.LeererBlock;
import haushalt.auswertung.bloecke.TabellenBlock;
import haushalt.auswertung.bloecke.TextBlock;
import haushalt.daten.Datenbasis;
import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.Jahr;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;
import haushalt.gui.generischerdialog.ZeitraumGDP;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.07.03
 */

/*
 * 2007.07.03 Internationalisierung
 */
public class BAPlanung extends AbstractBlockAuswertung {
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();

  public static final String ueberschrift = res.getString("forecast");
  
  public BAPlanung(Haushalt haushalt, Datenbasis db, String name) {
    super(haushalt, db, name);
    AbstractGDPane[] panes = new AbstractGDPane[2];
    panes[0] = new ZeitraumGDP(res.getString("period")+":", new Jahr(2008));
    panes[1] = new PlanungGDP(res.getString("forecast")+":", new Planung(db));
    erzeugeEigenschaften(haushalt.getFrame(), ueberschrift, panes);
  }

  protected String berechneAuswertung(Object[] werte) {
    AbstractZeitraum zeitraum = (AbstractZeitraum) werte[0];
    Planung planung = (Planung) werte[1];
    planung.setZeitraum(zeitraum);
    tabelle = planung.getVergleich();

    // Vorhandene Blöcke löschen und neu berechnete einfügen
    String titel = res.getString("forecast")+" ("+zeitraum+")";
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
    addDokumentenBlock(new LeererBlock(1));
    TextBlock block3 = new TextBlock(planung.getTextHochrechnen());
    block3.setFont(new Font(haushalt.getFontname(), Font.PLAIN, haushalt.getFontgroesse()-2));
    addDokumentenBlock(block3);
    return titel;
  }

}
