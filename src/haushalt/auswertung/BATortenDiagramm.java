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

import haushalt.auswertung.bloecke.AbstractBlock;
import haushalt.auswertung.bloecke.AbstractTabelleBlock;
import haushalt.auswertung.bloecke.DoppelteTabelleBlock;
import haushalt.auswertung.bloecke.LeererBlock;
import haushalt.auswertung.bloecke.TextBlock;
import haushalt.auswertung.bloecke.TortenBlock;
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
 * @version 2.5/2007.07.02
 * @since 2.0
 */

/*
 * 2007.07.02 Internationalisierung
 * 2004.08.22 Erste Version
 */
public class BATortenDiagramm extends AbstractBlockAuswertung {

	public static final String UEBERSCHRIFT = TextResource.get().getString("headline_pie_chart");

	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();

	public BATortenDiagramm(final Haushalt haushalt, final Datenbasis db, final String name) {
		super(haushalt, db, name);
		final AbstractGDPane[] panes = new AbstractGDPane[6];
		panes[0] = new ZeitraumGDP(RES.getString("first_period") + ":", new Jahr(2007));
		panes[1] = new ZahlGDP(RES.getString("number_of_categories") + ":", 10);
		panes[2] = new EinOderAlleRegisterGDP(RES.getString("register") + ":", db, null);
		panes[3] = new MehrereKategorienGDP(RES.getString("categories") + ":", db);
		panes[4] = new TextArrayGDP(RES.getString("color_scheme") + ":", FarbPaletten.getPalettenNamen(), "Standard");
		panes[5] = new BooleanGDP(RES.getString("display_values"), Boolean.FALSE, RES.getString("in_percent"));
		erzeugeEigenschaften(haushalt.getFrame(), UEBERSCHRIFT, panes);
	}

	@Override
	protected String berechneAuswertung(final Object[] werte) {
		final AbstractZeitraum zeitraum = (AbstractZeitraum) werte[0];
		final int maxAnzahlWerte = ((Integer) werte[1]).intValue();
		final String register = (String) werte[2];
		final EinzelKategorie[] kategorien = (EinzelKategorie[]) werte[3];
		final int anzahlKategorien = kategorien.length;
		final String farbschema = (String) werte[4];
		final Boolean prozent = (Boolean) werte[5];

		// ---------------------------------------------------------------------------

		final Euro[] einnahmen = new Euro[maxAnzahlWerte];
		final EinzelKategorie[] kategorienEinnahmen = new EinzelKategorie[maxAnzahlWerte];
		final Euro[] ausgaben = new Euro[maxAnzahlWerte];
		final EinzelKategorie[] kategorienAusgaben = new EinzelKategorie[maxAnzahlWerte];
		final Euro gesamtEinnahmen = new Euro();
		final Euro gesamtAusgaben = new Euro();
		Euro kategorieSumme;
		final boolean unterkategorienVerwenden = ((MehrereKategorienGDP) this.getPanes()[3]).getUnterkategorienVerwenden();
		int anzahlEinnahmen = 0;
		int anzahlAusgaben = 0;

		for (int i = 0; i < anzahlKategorien; i++) {
			kategorieSumme = getDb().getKategorieSaldo(kategorien[i], zeitraum, register, unterkategorienVerwenden);
			if (kategorieSumme.compareTo(Euro.NULL_EURO) > 0) { // Einnahmen:
				gesamtEinnahmen.sum(kategorieSumme);
				for (int j = 0; j < maxAnzahlWerte; j++) {
					if (einnahmen[j] == null) {
						einnahmen[j] = kategorieSumme; // Prima! Noch ein Platz
														// frei!
						kategorienEinnahmen[j] = kategorien[i];
						j = maxAnzahlWerte;
						anzahlEinnahmen++;
					} else if (kategorieSumme.compareTo(einnahmen[j]) > 0) {
						for (int k = maxAnzahlWerte - 1; k > j; k--) {
							einnahmen[k] = einnahmen[k - 1];
							kategorienEinnahmen[k] = kategorienEinnahmen[k - 1];
						}
						einnahmen[j] = kategorieSumme;
						kategorienEinnahmen[j] = kategorien[i];
						j = maxAnzahlWerte;
						anzahlEinnahmen++;
					}
				}
			} else if (kategorieSumme.compareTo(Euro.NULL_EURO) < 0) { // Ausgaben:
				kategorieSumme = Euro.NULL_EURO.sub(kategorieSumme);
				gesamtAusgaben.sum(kategorieSumme);
				for (int j = 0; j < maxAnzahlWerte; j++) {
					if (ausgaben[j] == null) {
						ausgaben[j] = kategorieSumme; // Prima! Noch ein Platz
														// frei!
						kategorienAusgaben[j] = kategorien[i];
						j = maxAnzahlWerte;
						anzahlAusgaben++;
					} else if (kategorieSumme.compareTo(ausgaben[j]) > 0) {
						for (int k = maxAnzahlWerte - 1; k > j; k--) {
							ausgaben[k] = ausgaben[k - 1];
							kategorienAusgaben[k] = kategorienAusgaben[k - 1];
						}
						ausgaben[j] = kategorieSumme;
						kategorienAusgaben[j] = kategorien[i];
						j = maxAnzahlWerte;
						anzahlAusgaben++;
					}
				}
			}
		}

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		String titel = RES.getString("distribution_income_expenditure") + " (" + zeitraum;
		loescheBloecke();
		if (register == null) {
			titel += ")";
		} else {
			titel += ", " + register + ")";
		}
		final AbstractBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(this.getHaushalt().getFontname(), Font.BOLD, this.getHaushalt().getFontgroesse() + 6));
		addDokumentenBlock(block1);
		addDokumentenBlock(new LeererBlock(1));
		final AbstractBlock block2 = new TortenBlock(farbschema, einnahmen, gesamtEinnahmen, ausgaben, gesamtAusgaben);
		block2.setFont(new Font(this.getHaushalt().getFontname(), Font.PLAIN, this.getHaushalt().getFontgroesse()));
		addDokumentenBlock(block2);
		addDokumentenBlock(new LeererBlock(1));

		anzahlEinnahmen = (maxAnzahlWerte < anzahlEinnahmen) ? maxAnzahlWerte : anzahlEinnahmen;
		anzahlAusgaben = (maxAnzahlWerte < anzahlAusgaben) ? maxAnzahlWerte : anzahlAusgaben;
		final String[][] tabelleEinnahmen = new String[anzahlEinnahmen][2];
		final String[][] tabelleAusgaben = new String[anzahlAusgaben][2];
		for (int j = 0; j < anzahlEinnahmen; j++) {
			tabelleEinnahmen[j][0] = "" + kategorienEinnahmen[j];
			if (prozent) {
				tabelleEinnahmen[j][1] = String.format("%1$.1f%%", einnahmen[j].toDouble() * 100.0 / gesamtEinnahmen.toDouble());
			} else {
				tabelleEinnahmen[j][1] = "" + einnahmen[j];
			}
		}

		for (int j = 0; j < anzahlAusgaben; j++) {
			tabelleAusgaben[j][0] = "" + kategorienAusgaben[j];
			if (prozent) {
				tabelleAusgaben[j][1] = ""
					+ String.format("%1$.1f%%", ausgaben[j].toDouble() * 100.0 / gesamtAusgaben.toDouble());
			} else {
				tabelleAusgaben[j][1] = "" + ausgaben[j];
			}
		}

		final double[] relTabs = {0.0D, 70.0D};
		final AbstractTabelleBlock.Ausrichtung[] attribute2 = {
				AbstractTabelleBlock.Ausrichtung.LINKS, AbstractTabelleBlock.Ausrichtung.RECHTS};
		final DoppelteTabelleBlock block3 = new DoppelteTabelleBlock(tabelleEinnahmen, tabelleAusgaben);
		block3.setFont(new Font(this.getHaushalt().getFontname(), Font.PLAIN, this.getHaushalt().getFontgroesse()));
		block3.setRelTabs(relTabs, true);
		block3.setRelTabs(relTabs, false);
		block3.setAusrichtung(attribute2, true);
		block3.setAusrichtung(attribute2, false);
		block3.setHgFarbe(farbschema);
		block3.setLinienFarbe("Weiß");
		block3.setRelRand(0.05D);
		addDokumentenBlock(block3);

		return titel;
	}

}
