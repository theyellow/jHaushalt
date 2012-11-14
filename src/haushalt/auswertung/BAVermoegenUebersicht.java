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
import haushalt.daten.Datum;
import haushalt.daten.Euro;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;
import haushalt.gui.generischerdialog.DatumGDP;

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
public class BAVermoegenUebersicht extends AbstractBlockAuswertung {

	public static final String UEBERSCHRIFT = TextResource.get().getString("table_fortune_overview");

	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();

	public BAVermoegenUebersicht(final Haushalt haushalt, final Datenbasis db, final String name) {
		super(haushalt, db, name);
		final AbstractGDPane[] panes = new AbstractGDPane[1];
		panes[0] = new DatumGDP(RES.getString("date") + ":", new Datum());
		erzeugeEigenschaften(haushalt.getFrame(), UEBERSCHRIFT, panes);
	}

	@Override
	protected String berechneAuswertung(final Object[] werte) {
		final Datum datum = (Datum) werte[0];
		final String[] register = getDb().getRegisterNamen();
		setTabelle(new String[register.length + 1][2]);
		final Euro summe = new Euro();
		for (int i = 0; i < register.length; i++) {
			final Euro saldo = getDb().getRegisterSaldo(register[i], datum);
			setTabelleContent(i, 0, register[i]);
			setTabelleContent(i, 1, "" + saldo);
			summe.sum(saldo);
		}
		setTabelleContent(register.length, 0, RES.getString("total"));
		setTabelleContent(register.length, 1, "" + summe);

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		final String titel = RES.getString("fortune_overview") + " (" + datum + ")";
		loescheBloecke();
		final TextBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(this.getHaushalt().getFontname(), Font.BOLD, this.getHaushalt().getFontgroesse() + 6));
		addDokumentenBlock(block1);
		addDokumentenBlock(new LeererBlock(1));
		final TabellenBlock block2 = new TabellenBlock(getTabelle());
		block2.setFont(new Font(this.getHaushalt().getFontname(), Font.PLAIN, this.getHaushalt().getFontgroesse()));
		final TabellenBlock.Ausrichtung[] attribute = {TabellenBlock.Ausrichtung.LINKS, TabellenBlock.Ausrichtung.RECHTS};
		block2.setAusrichtung(attribute);
		addDokumentenBlock(block2);
		return titel;
	}

}
