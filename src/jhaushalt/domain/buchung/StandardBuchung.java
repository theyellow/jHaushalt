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

package jhaushalt.domain.buchung;

import java.util.logging.Logger;

import jhaushalt.domain.Geldbetrag;
import jhaushalt.domain.kategorie.EinzelKategorie;
import jhaushalt.domain.kategorie.Kategorie;
import jhaushalt.domain.zeitraum.Zeitraum;
import jhaushalt.domain.zeitraum.Datum;

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

public class StandardBuchung extends Buchung {

	private static final Logger LOGGER = Logger.getLogger(StandardBuchung.class.getName());

	private EinzelKategorie kategorie = EinzelKategorie.SONSTIGES;
	private Geldbetrag betrag = new Geldbetrag();

	public StandardBuchung() {
	}

	public StandardBuchung(final Datum datum, final String text, final EinzelKategorie kategorie, final Geldbetrag betrag) {
		setDatum(datum);
		setText(text);
		setKategorie(kategorie);
		this.betrag = betrag;
	}

	@Override
	public Kategorie getKategorie() {
		return this.kategorie;
	}

	@Override
	public void setKategorie(final Kategorie neueKategorie) {
		this.kategorie = (EinzelKategorie) neueKategorie;
	}

	@Override
	public Geldbetrag getWert() {
		return this.betrag;
	}

	@Override
	public void setWert(final Geldbetrag wert) {
		this.betrag = wert;
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
	
	
	// -- Auswertung
	// -------------------------------------------------------------

	@Override
	public void bildeKategorieSumme(final Zeitraum zeitraum, final boolean unterkat) {
		if ((zeitraum == null) || getDatum().istImZeitraum(zeitraum)) {
			this.kategorie.addiereWert(this.betrag, unterkat);
		}
	}

	@Override
	public Geldbetrag getKategorieWert(final EinzelKategorie namekat, final boolean unterkat) {
		if (this.kategorie.istInKategorie(namekat, unterkat)) {
			return this.betrag;
		}
		return Geldbetrag.NULL_EURO;
	}


	
	
	// -- Methode des Interface 'Cloneable'
	// --------------------------------------

	@Override
	public final Object clone() {
		final Datum clonedDatum = (Datum) getDatum().clone();
		final String text = new String(getText());
		final Geldbetrag clonedBetrag = (Geldbetrag) this.betrag.clone();
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