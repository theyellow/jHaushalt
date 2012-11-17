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

import haushalt.auswertung.bloecke.LeererBlock;
import haushalt.auswertung.bloecke.TabellenBlock;
import haushalt.auswertung.bloecke.TextBlock;
import haushalt.daten.Datenbasis;
import haushalt.daten.Euro;
import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.Jahr;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;
import haushalt.gui.generischerdialog.EinOderAlleRegisterGDP;
import haushalt.gui.generischerdialog.ZahlGDP;
import haushalt.gui.generischerdialog.ZeitraumGDP;

import java.awt.Font;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5.1/2008.03.10
 */

/*
 * 2008.03.10 BugFix: Fehlender Resource-String
 * 2007.06.04 Internationalisierung
 * 2006.02.14 BugFix: Titel richtig erzeugt
 * 2004.08.22 Erste Version
 */

public class BAEinnahmenAusgaben extends AbstractBlockAuswertung {

	public static final String UEBERSCHRIFT = TextResource.get().getString("table_income_expenditure");

	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();

	private final String[] kopf = {
			RES.getString("period"), RES.getString("income"), RES.getString("expenditure"), RES.getString("difference")};

	public BAEinnahmenAusgaben(final Haushalt haushalt, final Datenbasis db, final String name) {
		super(haushalt, db, name);
		final AbstractGDPane[] panes = new AbstractGDPane[3];
		panes[0] = new ZeitraumGDP(RES.getString("first_period") + ":", new Jahr(2007));
		panes[1] = new ZahlGDP(RES.getString("number_of_periods") + ":", 4);
		panes[2] = new EinOderAlleRegisterGDP(RES.getString("register") + ":", db, null);
		erzeugeEigenschaften(haushalt.getFrame(), UEBERSCHRIFT, panes);
	}


	@Override
	protected String berechneAuswertung(final Object[] werte) {
		final AbstractZeitraum zeitraum = (AbstractZeitraum) werte[0];
		final int anzahlZeitraeume = ((Integer) werte[1]).intValue();
		final String register = (String) werte[2];
		setTabelle(new String[anzahlZeitraeume + 2][4]);
		AbstractZeitraum tmpZeitraum = zeitraum;
		final Euro summeEinnahmen = new Euro();
		final Euro summeAusgaben = new Euro();
		setTabelleLine(0, this.kopf);
		for (int i = 1; i <= anzahlZeitraeume; i++) {
			final Euro einnahmen = getDb().getEinnahmen(tmpZeitraum, register);
			final Euro ausgaben = getDb().getAusgaben(tmpZeitraum, register);
			setTabelleContent(i, 0, "" + tmpZeitraum);
			setTabelleContent(i, 1, "" + einnahmen);
			setTabelleContent(i, 2, "" + ausgaben);
			setTabelleContent(i, 3, "" + einnahmen.sub(ausgaben));
			summeEinnahmen.sum(einnahmen);
			summeAusgaben.sum(ausgaben);
			tmpZeitraum = tmpZeitraum.folgeZeitraum();
		}
		setTabelleContent(anzahlZeitraeume + 1, 0, RES.getString("average"));
		setTabelleContent(anzahlZeitraeume + 1, 1, "" + summeEinnahmen.durch(anzahlZeitraeume));
		setTabelleContent(anzahlZeitraeume + 1, 2, "" + summeAusgaben.durch(anzahlZeitraeume));
		setTabelleContent(anzahlZeitraeume + 1, 3, "" + summeEinnahmen.sub(summeAusgaben).durch(anzahlZeitraeume));

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		String titel = RES.getString("income_expenditure")
			+ " ("
			+ getTabelle()[1][0]
			+ " "
			+ RES.getString("to")
			+ " "
			+ getTabelle()[anzahlZeitraeume][0];
		if (register == null) {
			titel += ")";
		} else {
			titel += ", " + register + ")";
		}
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
		return titel;
	}

}
