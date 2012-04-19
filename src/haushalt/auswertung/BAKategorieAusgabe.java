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

import haushalt.auswertung.bloecke.AbstractBlock;
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
import haushalt.gui.generischerdialog.ZeitraumGDP;

import java.awt.Font;
import java.util.ArrayList;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.06.04
 * @since 2.0
 */

/*
 * 2007.06.05 Internationalisierung
 * 2004.08.22 Erste Version
 */
public class BAKategorieAusgabe extends AbstractBlockAuswertung {

	private static final boolean DEBUG = false;
	private static final long serialVersionUID = 1L;
	private static final TextResource res = TextResource.get();

	public static final String ueberschrift = res.getString("table_selected_bookings");

	public BAKategorieAusgabe(final Haushalt haushalt, final Datenbasis db, final String name) {
		super(haushalt, db, name);
		final AbstractGDPane[] panes = new AbstractGDPane[3];
		panes[0] = new ZeitraumGDP(res.getString("period") + ":", new Jahr(2007));
		panes[1] = new EinOderAlleRegisterGDP(res.getString("register") + ":", db, null);
		panes[2] = new MehrereKategorienGDP(res.getString("categories") + ":", db);
		erzeugeEigenschaften(haushalt.getFrame(), ueberschrift, panes);
	}

	@Override
	protected String berechneAuswertung(final Object[] werte) {
		final AbstractZeitraum zeitraum = (AbstractZeitraum) werte[0];
		final String register = (String) werte[1];
		final EinzelKategorie[] kategorien = (EinzelKategorie[]) werte[2];

		final boolean unterkategorienVerwenden = ((MehrereKategorienGDP) this.panes[2]).getUnterkategorienVerwenden();
		final ArrayList<String[]> buchungen = this.db.getBuchungen(zeitraum, register, kategorien,
				unterkategorienVerwenden);
		final int anzahl = buchungen.size();
		this.tabelle = new String[anzahl][4];
		final Euro summe = new Euro();
		for (int i = 0; i < anzahl; i++) {
			summe.sum(new Euro(buchungen.get(i)[3]));
			this.tabelle[i] = buchungen.get(i);
		}
		if (DEBUG) {
			System.out.println("" + anzahl + " Buchungen im Zeitraum " + zeitraum);
		}

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		String titel = res.getString("selected_bookings") + " (" + zeitraum;
		if (register == null) {
			titel += " / " + res.getString("all_registers") + ")";
		}
		else {
			titel += " / " + register + ")";
		}
		loescheBloecke();
		final AbstractBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(this.haushalt.getFontname(), Font.BOLD, this.haushalt.getFontgroesse() + 6));
		addDokumentenBlock(block1);
		addDokumentenBlock(new LeererBlock(1));
		if (anzahl == 0) {
			final String hinweis = res.getString("no_bookings_with_this_properties");
			final AbstractBlock block2 = new TextBlock(hinweis);
			addDokumentenBlock(block2);
		}
		else {
			final double[] relTabs = { 0.0, 12.0, 50.0, 80.0 };
			final TabellenBlock.Ausrichtung[] attribute = {
					TabellenBlock.Ausrichtung.LINKS,
					TabellenBlock.Ausrichtung.LINKS,
					TabellenBlock.Ausrichtung.LINKS,
					TabellenBlock.Ausrichtung.RECHTS };
			final TabellenBlock block2 = new TabellenBlock(this.tabelle);
			block2.setFont(new Font(this.haushalt.getFontname(), Font.PLAIN, this.haushalt.getFontgroesse()));
			block2.setRelTabs(relTabs);
			block2.setLinienFarbe("Weiß");
			block2.setAusrichtung(attribute);
			addDokumentenBlock(block2);
			final String[][] text = { { "", res.getString("total") + ":", "", "" + summe } };
			final TabellenBlock block3 = new TabellenBlock(text);
			block3.setFont(new Font(this.haushalt.getFontname(), Font.ITALIC, this.haushalt.getFontgroesse()));
			block3.setRelTabs(relTabs);
			block3.setHgFarbe("Grau");
			block3.setLinienFarbe("Grau");
			block3.setAusrichtung(attribute);
			addDokumentenBlock(block3);
		}
		return titel;
	}

}
