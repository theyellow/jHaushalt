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

/*
 * 2005.03.30 BugFix: Clonen gefixed
 */

package haushalt.daten;

import haushalt.daten.zeitraum.AbstractZeitraum;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Die SplitBuchung ist ähnlich der StandardBuchung. Der Buchungsbetrag wurde
 * lediglich auf mehrere Kategorien aufgeteilt.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.6/2010.02.03
 */

/*
 * 2010.02.03 Funktion clone() korrigiert: new String() verwendet
 * 2009.07.28 BugFix: Nach Löschung aller Kategorien/Beträge, liefert
 * reduziere() jetzt "SONSTIGES"/0€ zurück
 * 2006.02.10 Ergänzung der Methode isInKategorie
 */

public class SplitBuchung extends AbstractBuchung {

	private static final Logger LOGGER = Logger.getLogger(SplitBuchung.class.getName());

	private MehrfachKategorie splitKategorie = new MehrfachKategorie();
	private ArrayList<Euro> splitBetrag = new ArrayList<Euro>();

	public SplitBuchung() {
		// wird zum Laden benötigt
	}

	public SplitBuchung(final Datum datum, final String text) {
		setDatum(datum);
		setText(text);
	}

	public SplitBuchung(final StandardBuchung buchung) {
		setDatum(buchung.getDatum());
		setText(buchung.getText());
		add((EinzelKategorie) buchung.getKategorie(), buchung.getWert());
	}

	public int getAnzahl() {
		return this.splitKategorie.size();
	}

	public void add() {
		add(EinzelKategorie.SONSTIGES, new Euro());
	}

	public void add(final EinzelKategorie kategorie, final Euro betrag) {
		this.splitKategorie.add(kategorie);
		this.splitBetrag.add(betrag);
	}

	public void loesche(final int nr) {
		this.splitKategorie.remove(nr);
		this.splitBetrag.remove(nr);
	}

	public AbstractBuchung reduziere() {
		if (this.splitKategorie.size() > 1) {
			return this;
		}
		if (this.splitKategorie.size() < 1) {
			return new StandardBuchung(getDatum(), getText(), EinzelKategorie.SONSTIGES, new Euro());
		}
		return new StandardBuchung(getDatum(), getText(), this.splitKategorie.get(0), this.splitBetrag.get(0));
	}

	// -- IKategorie
	// --------------------------------------------------------------

	@Override
	public void setKategorie(final IKategorie kategorie) {
		this.splitKategorie = (MehrfachKategorie) kategorie;
	}

	public void setKategorie(final int nr, final EinzelKategorie kategorie) {
		this.splitKategorie.set(nr, kategorie);
	}

	@Override
	public IKategorie getKategorie() {
		return this.splitKategorie;
	}

	public EinzelKategorie getKategorie(final int nr) {
		return this.splitKategorie.get(nr);
	}

	@Override
	public int ersetzeKategorie(final EinzelKategorie alteKategorie, final EinzelKategorie neueKategorie) {
		int zaehler = 0;
		for (int i = 0; i < getAnzahl(); i++) {
			if ((alteKategorie == this.splitKategorie.get(i)) ||
					(alteKategorie == null)) {
				this.splitKategorie.set(i, neueKategorie);
				zaehler++;
			}
		}
		return zaehler;
	}

	@Override
	public boolean istInKategorie(final EinzelKategorie kategorie, final boolean unterkategorienVerwenden) {
		for (int i = 0; i < getAnzahl(); i++) {
			if (this.splitKategorie.get(i).istInKategorie(kategorie, unterkategorienVerwenden)) {
				return true;
			}
		}
		return false;
	}

	// -- Buchungswert
	// -----------------------------------------------------------

	@Override
	public void setWert(final Euro wert) {
		final double faktor = wert.toDouble() / getWert().toDouble();
		for (int i = 0; i < getAnzahl(); i++) {
			final Euro neuerWert = getWert(i).mal(faktor);
			setWert(i, neuerWert);
		}
	}

	public void setWert(final int nr, final Euro wert) {
		this.splitBetrag.set(nr, wert);
	}

	@Override
	public Euro getWert() {
		final Euro summe = new Euro();
		for (int i = 0; i < getAnzahl(); i++) {
			summe.sum(this.splitBetrag.get(i));
		}
		return summe;
	}

	public Euro getWert(final int nr) {
		return this.splitBetrag.get(nr);
	}

	// -- Auswertung
	// -------------------------------------------------------------

	@Override
	public void bildeKategorieSumme(final AbstractZeitraum zeitraum, final boolean unterkat) {
		for (int i = 0; i < getAnzahl(); i++) {
			if (getDatum().istImZeitraum(zeitraum)) {
				getKategorie(i).addiereWert(getWert(i), unterkat);
			}
		}
	}

	@Override
	public Euro getKategorieWert(final EinzelKategorie namekat, final boolean unterkat) {
		final Euro summe = new Euro();
		for (int i = 0; i < getAnzahl(); i++) {
			if (getKategorie(i).istInKategorie(namekat, unterkat)) {
				summe.sum(getWert(i));
			}
		}
		return summe;
	}

	// -- E/A-Funktionen
	// ---------------------------------------------------------

	public void laden(final DataInputStream in, final Datenbasis db)
			throws IOException {
		getDatum().laden(in);
		setText(in.readUTF());
		final int size = in.readInt();
		this.splitKategorie = new MehrfachKategorie(size);
		this.splitBetrag = new ArrayList<Euro>(size);
		for (int i = 0; i < size; i++) {
			final String kategorie = in.readUTF();
			final Euro betrag = new Euro();
			betrag.laden(in);
			add(db.findeOderErzeugeKategorie(kategorie), betrag);
		}
	}

	@Override
	public void speichern(final DataOutputStream out)
			throws IOException {
		out.writeUTF("SplitBuchung");
		getDatum().speichern(out);
		out.writeUTF(getText());
		out.writeInt(this.splitKategorie.size());
		for (int i = 0; i < getAnzahl(); i++) {
			out.writeUTF("" + this.splitKategorie.get(i));
			this.splitBetrag.get(i).speichern(out);
		}
	}

	// -- Methode des Interface 'Cloneable'
	// --------------------------------------

	@Override
	public final Object clone() {
		final Datum clonedDatum = (Datum) getDatum().clone();
		final String text = new String(getText());
		SplitBuchung kopie = new SplitBuchung();
		try {
			kopie = (SplitBuchung) super.clone();
		} catch (final CloneNotSupportedException e) {
			LOGGER.warning("Cloning error. This should never happen.");
		}
		for (int i = 0; i < getAnzahl(); i++) {
			// Kategorien NICHT clonen, da dann nicht in der IKategorie-Liste
			kopie.add(this.splitKategorie.get(i), (Euro) this.splitBetrag.get(i).clone());
		}
		setDatum(clonedDatum);
		setText(text);
		return kopie;
	}

}
