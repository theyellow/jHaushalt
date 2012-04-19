/*
 * This file is part of jHaushalt.
 * jHaushalt is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * jHaushalt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with jHaushalt; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
 */

package haushalt.auswertung;

import haushalt.auswertung.bloecke.LeererBlock;
import haushalt.auswertung.bloecke.TabellenBlock;
import haushalt.auswertung.bloecke.TextBlock;
import haushalt.daten.Datenbasis;
import haushalt.daten.EinzelKategorie;
import haushalt.daten.Euro;
import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.Jahr;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;
import haushalt.gui.generischerdialog.EinOderAlleRegisterGDP;
import haushalt.gui.generischerdialog.MehrereKategorienGDP;
import haushalt.gui.generischerdialog.ProzentGDP;
import haushalt.gui.generischerdialog.ZeitraumGDP;

import java.awt.Font;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5.3/2008.03.19
 */

/*
 * 2008.03.19 BugFix: Breite erste Spalte jetzt Integer
 * 2007.06.26 Internationalisierung
 * 2007.02.21 Erweiterung: Festlegen der Breite der ersten Spalte
 * 2006.06.11 BugFix: Nur die ausgewählten Kategorien werden
 * berechnet
 * 2004.08.22 Erste Version
 */
public class BARelativerVergleich extends AbstractBlockAuswertung {

	private static final long serialVersionUID = 1L;
	private static final TextResource res = TextResource.get();

	public static final String ueberschrift = res.getString("headline_relative_comparison");

	public BARelativerVergleich(final Haushalt haushalt, final Datenbasis db, final String name) {
		super(haushalt, db, name);
		final AbstractGDPane[] panes = new AbstractGDPane[5];
		panes[0] = new ZeitraumGDP(res.getString("first_period") + ":", new Jahr(2006));
		panes[1] = new ZeitraumGDP(res.getString("second_period") + ":", new Jahr(2007));
		panes[2] = new EinOderAlleRegisterGDP(res.getString("register") + ":", db, null);
		panes[3] = new MehrereKategorienGDP(res.getString("categories") + ":", db);
		panes[4] = new ProzentGDP(res.getString("width_first_column") + ":");
		erzeugeEigenschaften(haushalt.getFrame(), ueberschrift, panes);
	}

	@Override
	protected String berechneAuswertung(final Object[] werte) {
		final AbstractZeitraum zeitraum1 = (AbstractZeitraum) werte[0];
		final AbstractZeitraum zeitraum2 = (AbstractZeitraum) werte[1];
		final String register = (String) werte[2];
		final double faktor1 = 30.0D / zeitraum1.getAnzahlTage();
		final double faktor2 = 30.0D / zeitraum2.getAnzahlTage();
		final EinzelKategorie[] kategorien = (EinzelKategorie[]) werte[3];
		final double prozentErsteSpalte = ((Integer) werte[4]).doubleValue();
		final int anzKat = kategorien.length;
		final boolean unterkategorienVerwenden = ((MehrereKategorienGDP) this.panes[3]).getUnterkategorienVerwenden();
		final Euro[] summen1 = this.db.getKategorieSalden(kategorien, zeitraum1, register, unterkategorienVerwenden);
		final Euro[] summen2 = this.db.getKategorieSalden(kategorien, zeitraum2, register, unterkategorienVerwenden);
		this.tabelle = new String[anzKat + 2][4];
		this.tabelle[0][0] = res.getString("category");
		this.tabelle[0][1] = "" + zeitraum1;
		this.tabelle[0][2] = "" + zeitraum2;
		this.tabelle[0][3] = res.getString("difference");
		this.tabelle[anzKat + 1][0] = res.getString("total");

		final Euro summe1 = new Euro();
		final Euro summe2 = new Euro();
		for (int i = 0; i < anzKat; i++) {
			final Euro relWert1 = summen1[i].mal(faktor1);
			final Euro relWert2 = summen2[i].mal(faktor2);
			this.tabelle[i + 1][0] = "" + kategorien[i];
			this.tabelle[i + 1][1] = "" + relWert1;
			this.tabelle[i + 1][2] = "" + relWert2;
			this.tabelle[i + 1][3] = "" + relWert1.sub(relWert2);
			summe1.sum(relWert1);
			summe2.sum(relWert2);
		}
		this.tabelle[anzKat + 1][1] = "" + summe1;
		this.tabelle[anzKat + 1][2] = "" + summe2;
		this.tabelle[anzKat + 1][3] = "" + summe1.sub(summe2);

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		String titel = res.getString("relative_comparison") + " (" + this.tabelle[0][1] + " " + res.getString("and")
				+ " "
				+ this.tabelle[0][2];
		if (register == null) {
			titel += ")";
		}
		else {
			titel += ", " + register + ")";
		}
		loescheBloecke();
		final TextBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(this.haushalt.getFontname(), Font.BOLD, this.haushalt.getFontgroesse() + 6));
		addDokumentenBlock(block1);
		addDokumentenBlock(new LeererBlock(1));
		final TabellenBlock block2 = new TabellenBlock(this.tabelle);
		block2.setFont(new Font(this.haushalt.getFontname(), Font.PLAIN, this.haushalt.getFontgroesse()));
		final TabellenBlock.Ausrichtung[] attribute = {
				TabellenBlock.Ausrichtung.LINKS,
				TabellenBlock.Ausrichtung.RECHTS,
				TabellenBlock.Ausrichtung.RECHTS,
				TabellenBlock.Ausrichtung.RECHTS };
		block2.setAusrichtung(attribute);
		if (prozentErsteSpalte > 0.0D) {
			final double rest = (100.0D - prozentErsteSpalte) / 3.0D;
			final double[] relTabs = {
					0.0D,
					prozentErsteSpalte,
					prozentErsteSpalte + rest,
					prozentErsteSpalte + rest * 2.0D,
			};
			block2.setRelTabs(relTabs);
		}
		addDokumentenBlock(block2);
		addDokumentenBlock(new LeererBlock(1));
		final TextBlock block3 = new TextBlock(res.getString("calculated_amounts"));
		block3.setFont(new Font(this.haushalt.getFontname(), Font.PLAIN, this.haushalt.getFontgroesse() - 2));
		addDokumentenBlock(block3);
		return titel;
	}

}
