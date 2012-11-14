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
import java.util.logging.Logger;

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

	public static final String UEBERSCHRIFT = TextResource.get().getString("table_selected_bookings");

	private static final boolean DEBUG = false;
	private static final Logger LOGGER = Logger.getLogger(BAKategorieAusgabe.class.getName());
	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();

	public BAKategorieAusgabe(final Haushalt haushalt, final Datenbasis db, final String name) {
		super(haushalt, db, name);
		final AbstractGDPane[] panes = new AbstractGDPane[3];
		panes[0] = new ZeitraumGDP(RES.getString("period") + ":", new Jahr(2007));
		panes[1] = new EinOderAlleRegisterGDP(RES.getString("register") + ":", db, null);
		panes[2] = new MehrereKategorienGDP(RES.getString("categories") + ":", db);
		erzeugeEigenschaften(haushalt.getFrame(), UEBERSCHRIFT, panes);
	}

	@Override
	protected String berechneAuswertung(final Object[] werte) {
		final AbstractZeitraum zeitraum = (AbstractZeitraum) werte[0];
		final String register = (String) werte[1];
		final EinzelKategorie[] kategorien = (EinzelKategorie[]) werte[2];

		final boolean unterkategorienVerwenden = ((MehrereKategorienGDP) this.getPanes()[2]).getUnterkategorienVerwenden();
		final ArrayList<String[]> buchungen = getDb().getBuchungen(zeitraum, register, kategorien, unterkategorienVerwenden);
		final int anzahl = buchungen.size();
		setTabelle(new String[anzahl][4]);
		final Euro summe = new Euro();
		for (int i = 0; i < anzahl; i++) {
			summe.sum(new Euro(buchungen.get(i)[3]));
			setTabelleLine(i, buchungen.get(i));
		}
		if (DEBUG) {
			LOGGER.info("" + anzahl + " Buchungen im Zeitraum " + zeitraum);
		}

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		String titel = RES.getString("selected_bookings") + " (" + zeitraum;
		if (register == null) {
			titel += " / " + RES.getString("all_registers") + ")";
		} else {
			titel += " / " + register + ")";
		}
		loescheBloecke();
		final AbstractBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(this.getHaushalt().getFontname(), Font.BOLD, this.getHaushalt().getFontgroesse() + 6));
		addDokumentenBlock(block1);
		addDokumentenBlock(new LeererBlock(1));
		if (anzahl == 0) {
			final String hinweis = RES.getString("no_bookings_with_this_properties");
			final AbstractBlock block2 = new TextBlock(hinweis);
			addDokumentenBlock(block2);
		} else {
			final double[] relTabs = {0.0, 12.0, 50.0, 80.0};
			final TabellenBlock.Ausrichtung[] attribute = {
					TabellenBlock.Ausrichtung.LINKS, TabellenBlock.Ausrichtung.LINKS, TabellenBlock.Ausrichtung.LINKS,
					TabellenBlock.Ausrichtung.RECHTS};
			final TabellenBlock block2 = new TabellenBlock(getTabelle());
			block2.setFont(new Font(this.getHaushalt().getFontname(), Font.PLAIN, this.getHaushalt().getFontgroesse()));
			block2.setRelTabs(relTabs);
			block2.setLinienFarbe("Weiß");
			block2.setAusrichtung(attribute);
			addDokumentenBlock(block2);
			final String[][] text = {{"", RES.getString("total") + ":", "", "" + summe}};
			final TabellenBlock block3 = new TabellenBlock(text);
			block3.setFont(new Font(this.getHaushalt().getFontname(), Font.ITALIC, this.getHaushalt().getFontgroesse()));
			block3.setRelTabs(relTabs);
			block3.setHgFarbe("Grau");
			block3.setLinienFarbe("Grau");
			block3.setAusrichtung(attribute);
			addDokumentenBlock(block3);
		}
		return titel;
	}

}
