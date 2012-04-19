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
import haushalt.gui.generischerdialog.ZahlGDP;
import haushalt.gui.generischerdialog.ZeitraumGDP;

import java.awt.Font;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5.3/2008.03.19
 * @since 2.0
 */

/*
 * 2008.03.19 BugFix: Breite erste Spalte jetzt Integer
 * 2007.06.26 Internationalisierung
 * 2007.02.21 Erweiterung: Festlegen der Breite der ersten Spalte
 * 2004.08.22 Erste Version
 */
public class BAKategorieSummen extends AbstractBlockAuswertung {

	private static final boolean DEBUG = false;
	private static final long serialVersionUID = 1L;
	private static final TextResource res = TextResource.get();

	public static final String ueberschrift = res.getString("table_category_totals");

	public BAKategorieSummen(final Haushalt haushalt, final Datenbasis db, final String name) {
		super(haushalt, db, name);
		final AbstractGDPane[] panes = new AbstractGDPane[5];
		panes[0] = new ZeitraumGDP(res.getString("first_period") + ":", new Jahr(2007));
		panes[1] = new ZahlGDP(res.getString("number_of_periods") + ":", new Integer(4));
		panes[2] = new EinOderAlleRegisterGDP(res.getString("register") + ":", db, null);
		panes[3] = new MehrereKategorienGDP(res.getString("categories") + ":", db);
		panes[4] = new ProzentGDP(res.getString("width_first_column") + ":");
		erzeugeEigenschaften(haushalt.getFrame(), ueberschrift, panes);
	}

	@Override
	protected String berechneAuswertung(final Object[] werte) {
		final AbstractZeitraum zeitraum = (AbstractZeitraum) werte[0];
		final int anzahlZeitraeume = ((Integer) werte[1]).intValue();
		final String register = (String) werte[2];
		final EinzelKategorie[] posKategorien = (EinzelKategorie[]) werte[3];
		final double prozentErsteSpalte = ((Integer) werte[4]).doubleValue();

		AbstractZeitraum tmpZeitraum = zeitraum;
		final int anzahlKategorien = posKategorien.length;
		this.tabelle = new String[anzahlKategorien + 2][anzahlZeitraeume + 1];
		this.tabelle[0][0] = res.getString("category");
		this.tabelle[anzahlKategorien + 1][0] = res.getString("total");

		for (int i = 0; i < anzahlKategorien; i++) {
			this.tabelle[i + 1][0] = "" + posKategorien[i];
		}

		final boolean unterkategorienVerwenden = ((MehrereKategorienGDP) this.panes[3]).getUnterkategorienVerwenden();
		for (int i = 0; i < anzahlZeitraeume; i++) {
			this.tabelle[0][i + 1] = "" + tmpZeitraum;
			final Euro[] katSummen = this.db.getKategorieSalden(posKategorien, tmpZeitraum, register,
					unterkategorienVerwenden);
			final Euro summe = new Euro();
			for (int j = 0; j < anzahlKategorien; j++) {
				this.tabelle[j + 1][i + 1] = "" + katSummen[j];
				summe.sum(katSummen[j]);
			}
			this.tabelle[anzahlKategorien + 1][i + 1] = "" + summe;
			tmpZeitraum = tmpZeitraum.folgeZeitraum();
		}

		if (DEBUG) {
			System.out.println("Kategorie-Summen berechnet.");
			System.out.println("" + anzahlKategorien + " Kategorien.");
			System.out.println("" + anzahlZeitraeume + " Zeiträume.");
		}

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		String titel = res.getString("category_totals") + " (";
		titel += this.tabelle[0][1] + " " + res.getString("to") + " " + this.tabelle[0][anzahlZeitraeume];
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
		final TabellenBlock.Ausrichtung[] attribute = new TabellenBlock.Ausrichtung[anzahlZeitraeume + 1];
		for (int i = 0; i <= anzahlZeitraeume; i++) {
			if (i == 0) {
				attribute[i] = TabellenBlock.Ausrichtung.LINKS;
			}
			else {
				attribute[i] = TabellenBlock.Ausrichtung.RECHTS;
			}
		}
		block2.setAusrichtung(attribute);
		if (prozentErsteSpalte > 0.0D) {
			final double rest = (100.0D - prozentErsteSpalte) / anzahlZeitraeume;
			final double[] relTabs = new double[anzahlZeitraeume + 1];
			relTabs[0] = 0.0D;
			for (int i = 0; i < anzahlZeitraeume; i++) {
				relTabs[i + 1] = prozentErsteSpalte + rest * i;
			}
			block2.setRelTabs(relTabs);
		}
		addDokumentenBlock(block2);
		return titel;
	}

}
