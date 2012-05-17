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
 * along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
 */

package haushalt.daten;

import haushalt.daten.zeitraum.AbstractZeitraum;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Die StandardBuchung ist die "normale" Buchung. Sie besteht aus Datum, Name,
 * IKategorie und Betrag.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.6/2010.02.03
 */

/*
 * 2010.02.03 Funktion clone() korrigiert: new String() verwendet
 * 2006.02.10 Ergänzung der Methode isInKategorie
 * 2004.08.22 Erste Version
 */

public class StandardBuchung extends AbstractBuchung {

	private static final Logger LOGGER = Logger.getLogger(StandardBuchung.class.getName());

	private EinzelKategorie kategorie = null;
	private Euro betrag;

	public StandardBuchung() {
		setKategorie(EinzelKategorie.SONSTIGES);
		this.betrag = new Euro();
	}

	public StandardBuchung(final Datum datum, final String text, final EinzelKategorie kategorie, final Euro betrag) {
		setDatum(datum);
		setText(text);
		setKategorie(kategorie);
		this.betrag = betrag;
	}

	// -- IKategorie
	// --------------------------------------------------------------

	@Override
	public IKategorie getKategorie() {
		return this.kategorie;
	}

	@Override
	public void setKategorie(final IKategorie neueKategorie) {
		this.kategorie = (EinzelKategorie) neueKategorie;
	}

	@Override
	public int ersetzeKategorie(final EinzelKategorie alteKategorie, final EinzelKategorie neueKategorie) {
		if ((this.kategorie == alteKategorie) || (alteKategorie == null)) {
			this.kategorie = neueKategorie;
			return 1;
		}
		return 0;
	}

	@Override
	public boolean istInKategorie(final EinzelKategorie kategorie, final boolean unterkategorienVerwenden) {
		return this.kategorie.istInKategorie(kategorie, unterkategorienVerwenden);
	}

	// -- Buchungswert
	// -----------------------------------------------------------

	@Override
	public Euro getWert() {
		return this.betrag;
	}

	@Override
	public void setWert(final Euro wert) {
		this.betrag = wert;
	}

	// -- Auswertung
	// -------------------------------------------------------------

	@Override
	public void bildeKategorieSumme(final AbstractZeitraum zeitraum, final boolean unterkat) {
		if ((zeitraum == null) || getDatum().istImZeitraum(zeitraum)) {
			this.kategorie.addiereWert(this.betrag, unterkat);
		}
	}

	@Override
	public Euro getKategorieWert(final EinzelKategorie namekat, final boolean unterkat) {
		if (this.kategorie.istInKategorie(namekat, unterkat)) {
			return this.betrag;
		}
		return Euro.NULL_EURO;
	}

	// -- E/A-Funktionen
	// ---------------------------------------------------------

	public void laden(final DataInputStream in, final Datenbasis db)
			throws IOException {
		getDatum().laden(in);
		setText(in.readUTF());
		setKategorie(db.findeOderErzeugeKategorie(in.readUTF()));
		this.betrag.laden(in);
	}

	@Override
	public void speichern(final DataOutputStream out)
			throws IOException {
		// Bis zur Version 1.2 gab es keine Unterscheidung zwischen
		// StandardBuchung
		// und SplitBuchung. Damit Daten von alten Versionen gelesen werden
		// können,
		// muss die neue StandardBuchung mit einem anderen Namen gespeichert
		// werden.
		out.writeUTF("StandardBuchung2");
		getDatum().speichern(out);
		out.writeUTF(getText());
		out.writeUTF("" + this.kategorie);
		this.betrag.speichern(out);
	}

	// -- Methode des Interface 'Cloneable'
	// --------------------------------------

	@Override
	public final Object clone() {
		final Datum clonedDatum = (Datum) getDatum().clone();
		final String text = new String(getText());
		final Euro clonedBetrag = (Euro) this.betrag.clone();
		StandardBuchung standardBuchung = new StandardBuchung();

		try {
			standardBuchung = (StandardBuchung) super.clone();
		} catch (final CloneNotSupportedException e) {
			LOGGER.warning("Cloning error. This should never happen.");
		}

		setDatum(clonedDatum);
		setText(text);
		setKategorie(this.kategorie);
		standardBuchung.betrag = clonedBetrag;
		return standardBuchung;
	}

}