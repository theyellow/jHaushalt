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

package haushalt.auswertung.planung;

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

import java.awt.Font;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.07.03
 */

/*
 * 2007.07.03 Internationalisierung
 */
public class BAPlanung extends AbstractBlockAuswertung {

	public static final String UEBERSCHRIFT = TextResource.get().getString("forecast");

	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();

	public BAPlanung(final Haushalt haushalt, final Datenbasis db, final String name) {
		super(haushalt, db, name);
		final AbstractGDPane[] panes = new AbstractGDPane[2];
		panes[0] = new ZeitraumGDP(RES.getString("period") + ":", new Jahr(2008));
		panes[1] = new PlanungGDP(RES.getString("forecast") + ":", new Planung(db));
		erzeugeEigenschaften(haushalt.getFrame(), UEBERSCHRIFT, panes);
	}

	@Override
	protected String berechneAuswertung(final Object[] werte) {
		final AbstractZeitraum zeitraum = (AbstractZeitraum) werte[0];
		final Planung planung = (Planung) werte[1];
		planung.setZeitraum(zeitraum);
		setTabelle(planung.getVergleich());

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		final String titel = RES.getString("forecast") + " (" + zeitraum + ")";
		loescheBloecke();
		final TextBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(this.getHaushalt().getFontname(), Font.BOLD, this.getHaushalt().getFontgroesse() + 6));
		addDokumentenBlock(block1);
		addDokumentenBlock(new LeererBlock(1));
		final TabellenBlock block2 = new TabellenBlock(getTabelle());
		block2.setFont(new Font(this.getHaushalt().getFontname(), Font.PLAIN, this.getHaushalt().getFontgroesse()));
		final TabellenBlock.Ausrichtung[] attribute = {
				TabellenBlock.Ausrichtung.LINKS, TabellenBlock.Ausrichtung.RECHTS, TabellenBlock.Ausrichtung.RECHTS,
				TabellenBlock.Ausrichtung.RECHTS};
		block2.setAusrichtung(attribute);
		addDokumentenBlock(block2);
		addDokumentenBlock(new LeererBlock(1));
		final TextBlock block3 = new TextBlock(planung.getTextHochrechnen());
		block3.setFont(new Font(this.getHaushalt().getFontname(), Font.PLAIN, this.getHaushalt().getFontgroesse() - 2));
		addDokumentenBlock(block3);
		return titel;
	}

}
