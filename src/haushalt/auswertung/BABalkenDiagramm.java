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
import haushalt.auswertung.bloecke.BalkenBlock;
import haushalt.auswertung.bloecke.LeererBlock;
import haushalt.auswertung.bloecke.TextBlock;
import haushalt.daten.Datenbasis;
import haushalt.daten.Euro;
import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.Jahr;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;
import haushalt.gui.generischerdialog.BooleanGDP;
import haushalt.gui.generischerdialog.EinOderAlleRegisterGDP;
import haushalt.gui.generischerdialog.FarbwahlGDP;
import haushalt.gui.generischerdialog.ZahlGDP;
import haushalt.gui.generischerdialog.ZeitraumGDP;

import java.awt.Color;
import java.awt.Font;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.06.04
 */

/*
 * 2007.06.04 Internationalisierung
 * 2006.02.03 Setzten der Farben hinzugefügt
 * 2004.08.22 Erste Version
 */
public class BABalkenDiagramm extends AbstractBlockAuswertung {

	public static final String UEBERSCHRIFT = TextResource.get().getString("headline_bar_chart");

	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();

	public BABalkenDiagramm(final Haushalt haushalt, final Datenbasis db, final String name) {
		super(haushalt, db, name);
		final AbstractGDPane[] panes = new AbstractGDPane[6];
		panes[0] = new ZeitraumGDP(RES.getString("first_period") + ":", new Jahr(2007));
		panes[1] = new ZahlGDP(RES.getString("number_of_periods") + ":", new Integer(4));
		panes[2] = new EinOderAlleRegisterGDP(RES.getString("register") + ":", db, null);
		panes[3] = new BooleanGDP(RES.getString("average"), Boolean.TRUE, RES.getString("display"));
		panes[4] = new FarbwahlGDP(RES.getString("color_income"), haushalt.getFrame(), Color.BLUE);
		panes[5] = new FarbwahlGDP(RES.getString("color_expenditure"), haushalt.getFrame(), Color.RED);
		erzeugeEigenschaften(haushalt.getFrame(), UEBERSCHRIFT, panes);
	}

	@Override
	protected String berechneAuswertung(final Object[] werte) {
		final AbstractZeitraum zeitraum = (AbstractZeitraum) werte[0];
		final int anzahlZeitraeume = ((Integer) werte[1]).intValue();
		final String register = (String) werte[2];
		final Boolean durchschnitt = (Boolean) werte[3];

		AbstractZeitraum tmpZeitraum = zeitraum;
		final String[] zeitraumNamen = new String[anzahlZeitraeume];
		final Euro[] einnahmen = new Euro[anzahlZeitraeume];
		final Euro[] ausgaben = new Euro[anzahlZeitraeume];
		for (int i = 0; i < anzahlZeitraeume; i++) {
			zeitraumNamen[i] = "" + tmpZeitraum;
			einnahmen[i] = getDb().getEinnahmen(tmpZeitraum, register);
			ausgaben[i] = getDb().getAusgaben(tmpZeitraum, register);
			tmpZeitraum = tmpZeitraum.folgeZeitraum();
		}

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		String titel = RES.getString("income_expenditure")
			+ " ("
			+ zeitraumNamen[0]
			+ " "
			+ RES.getString("to")
			+ " "
			+ zeitraumNamen[anzahlZeitraeume - 1];
		if (register == null) {
			titel += ")";
		} else {
			titel += ", " + register + ")";
		}
		loescheBloecke();
		final AbstractBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(this.getHaushalt().getFontname(), Font.BOLD, this.getHaushalt().getFontgroesse() + 6));
		addDokumentenBlock(block1);
		addDokumentenBlock(new LeererBlock(1));
		final BalkenBlock block2 = new BalkenBlock(zeitraumNamen, einnahmen, ausgaben, durchschnitt.booleanValue());
		block2.setFont(new Font(this.getHaushalt().getFontname(), Font.PLAIN, this.getHaushalt().getFontgroesse()));
		block2.setFarbeEinnahmen((Color) werte[4]);
		block2.setFarbeAusgaben((Color) werte[5]);
		addDokumentenBlock(block2);
		return titel;
	}

}
