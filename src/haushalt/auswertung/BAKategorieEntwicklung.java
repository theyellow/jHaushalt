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
import haushalt.auswertung.bloecke.EntwicklungBlock;
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
import haushalt.gui.generischerdialog.BooleanGDP;
import haushalt.gui.generischerdialog.EinOderAlleRegisterGDP;
import haushalt.gui.generischerdialog.MehrereKategorienGDP;
import haushalt.gui.generischerdialog.TextArrayGDP;
import haushalt.gui.generischerdialog.ZahlGDP;
import haushalt.gui.generischerdialog.ZeitraumGDP;

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

	public static final String UEBERSCHRIFT = TextResource.get().getString("chart_development_categories");

	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();


	public BAKategorieEntwicklung(final Haushalt haushalt, final Datenbasis db, final String name) {
		super(haushalt, db, name);
		final AbstractGDPane[] panes = new AbstractGDPane[6];
		panes[0] = new ZeitraumGDP(RES.getString("first_period") + ":", new Jahr(2007));
		panes[1] = new ZahlGDP(RES.getString("number_of_periods") + ":", 4);
		panes[2] = new EinOderAlleRegisterGDP(RES.getString("register") + ":", db, null);
		panes[3] = new MehrereKategorienGDP(RES.getString("categories") + ":", db);
		panes[4] = new TextArrayGDP(RES.getString("color_scheme") + ":", FarbPaletten.getPalettenNamen(), "Slow");
		panes[5] = new BooleanGDP(RES.getString("expenditure"), Boolean.TRUE, RES.getString("show_as_negative_values"));
		erzeugeEigenschaften(haushalt.getFrame(), UEBERSCHRIFT, panes);
	}

	@Override
	protected String berechneAuswertung(final Object[] werte) {
		final AbstractZeitraum zeitraum = (AbstractZeitraum) werte[0];
		final int anzahlZeitraeume = ((Integer) werte[1]).intValue();
		final String register = (String) werte[2];
		final EinzelKategorie[] kategorien = (EinzelKategorie[]) werte[3];
		final String farbschema = (String) werte[4];
		final Boolean negativeWerte = (Boolean) werte[5];

		final boolean unterkategorienVerwenden = ((MehrereKategorienGDP) this.getPanes()[3]).getUnterkategorienVerwenden();
		final int anzahlKategorien = kategorien.length;
		final String[] zeitraumNamen = new String[anzahlZeitraeume];
		final Euro[][] betraege = new Euro[anzahlZeitraeume][anzahlKategorien];
		AbstractZeitraum tmpZeitraum = zeitraum;
		for (int z = 0; z < anzahlZeitraeume; z++) {
			zeitraumNamen[z] = "" + tmpZeitraum;
			betraege[z] = getDb().getKategorieSalden(kategorien, tmpZeitraum, register, unterkategorienVerwenden);
			tmpZeitraum = tmpZeitraum.folgeZeitraum();
		}

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		String titel = RES.getString("category_development")
			+ " ("
			+ zeitraumNamen[0]
			+ " "
			+ RES.getString("to")
				+ " " + zeitraumNamen[anzahlZeitraeume - 1];
		if (register == null) {
			titel += ")";
		}
		else {
			titel += ", " + register + ")";
		}
		loescheBloecke();
		final AbstractBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(this.getHaushalt().getFontname(), Font.BOLD, this.getHaushalt().getFontgroesse() + 6));
		addDokumentenBlock(block1);
		addDokumentenBlock(new LeererBlock(1));
		final AbstractBlock block2 = new EntwicklungBlock(kategorien, zeitraumNamen, betraege, farbschema,
				negativeWerte);
		block2.setFont(new Font(this.getHaushalt().getFontname(), Font.PLAIN, this.getHaushalt().getFontgroesse()));
		addDokumentenBlock(block2);
		addDokumentenBlock(new LeererBlock(1));
		final int anzahl = kategorien.length;
		if (anzahl > 0) {
			setTabelle(new String[anzahl][1]);
			for (int i = 0; i < anzahl; i++) {
				setTabelleContent(i, 0, "" + kategorien[i]);
			}
			final double[] relTabs = { 50.0 };
			final TabellenBlock block3 = new TabellenBlock(getTabelle());
			block3.setFont(new Font(this.getHaushalt().getFontname(), Font.PLAIN, this.getHaushalt().getFontgroesse()));
			block3.setRelTabs(relTabs);
			block3.setHgFarbe(farbschema);
			block3.setLinienFarbe("Weiß");
			addDokumentenBlock(block3);
		}
		else {
			addDokumentenBlock(new TextBlock(RES.getString("no_category_selected")));
		}
		return titel;
	}

}
