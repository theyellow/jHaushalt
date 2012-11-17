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
import jhaushalt.domain.kategorie.UmbuchungKategorie;
import jhaushalt.domain.zeitraum.Zeitraum;
import jhaushalt.domain.zeitraum.Datum;

/**
 * Eine Umbuchung verschiebt Geld von einem Register in ein anderes. Es
 * entstehen
 * keine Einnahmen oder Ausgaben. Umbuchungen sind deshalb keiner Kategorie
 * zugeordnet. Statt der IKategorie besitzen Umbuchungen ein Register-Paar
 * (Quell- und Zielregister). Umbuchungen werden (bei Änderungen) automatisch in
 * das Quell- und das Zielregister einsortiert.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.6/2010.02.03
 */

/*
 * 2010.02.03 Funktion clone() korrigiert: new String() verwendet
 * 2006.06.19 Speichern von Umbuchungen ohne IKategorie (für auto. Umbuchungen)
 * 2006.02.13 Überarbeitung: Umstellung auf Klasse 'UmbuchungKategorie' als
 * IKategorie für eine Umbuchung
 * 2006.02.10 Ergänzung der Methode isInKategorie
 * 2004.08.22 Erste Version
 */

public class Umbuchung extends Buchung {

	private static final Logger LOGGER = Logger.getLogger(Umbuchung.class.getName());
	private static final boolean DEBUG = false;

	private Geldbetrag wert = new Geldbetrag();
	private UmbuchungKategorie kategorie = new UmbuchungKategorie(null, null);

	public Umbuchung() {
		// OK. Wird beim Laden benötigt.
	}

	public Umbuchung(final Datum datum, final String text, final UmbuchungKategorie kategorie, final Geldbetrag wert) {
		setDatum(datum);
		setText(text);
		setWert(wert);
		setKategorie(kategorie);
	}

	// -- IKategorie
	// --------------------------------------------------------------

	@Override
	public Kategorie getKategorie() {
		return this.kategorie;
	}

	@Override
	public void setKategorie(final Kategorie neueKategorie) {
		// Wenn sich das Partner-Register ändert, muss die Buchung entfernt und
		// wieder eingefügt werden. Vorsicht bei Selbstbuchungen!
		if (DEBUG) {
			LOGGER.info("Umbuchung.setKategorie: NEU " + neueKategorie + "; ALT " + this.kategorie);
		}

		// Schritt 1: Alte Umbuchung entfernen
		if (this.kategorie.getQuelle() != null) {
			this.kategorie.getQuelle().loescheUmbuchung(this);
		}
		if (!this.kategorie.isSelbstbuchung() && (this.kategorie.getZiel() != null)) {
			this.kategorie.getZiel().loescheUmbuchung(this);
			// Wenn die alte Umbuchung eine Selbstbuchung war, darf nur 1x
			// gelöscht
			// werden.
		}

		// Schritt 2: Neue Umbuchung einsortieren
		((UmbuchungKategorie) neueKategorie).getQuelle().einsortierenBuchung(this);
		if (!((UmbuchungKategorie) neueKategorie).isSelbstbuchung()) {
			((UmbuchungKategorie) neueKategorie).getZiel().einsortierenBuchung(this);
		}
		// Wenn eine Buchung eine Selbstbuchung wird, muss sie einmal nur
		// einsortiert werden.
		this.kategorie = (UmbuchungKategorie) neueKategorie;
	}

	@Override
	public int ersetzeKategorie(final EinzelKategorie alteKategorie, final EinzelKategorie neueKategorie) {
		// Umbuchungen haben keine IKategorie!
		return 0;
	}

	@Override
	public boolean istInKategorie(final EinzelKategorie kategorie, final boolean unterkategorienVerwenden) {
		// Umbuchungen haben keine IKategorie!
		return false;
	}

	// -- Buchungswert
	// -----------------------------------------------------------

	@Override
	public void setWert(final Geldbetrag wert) {
		this.wert = wert;
	}

	@Override
	public Geldbetrag getWert() {
		return this.wert;
	}

	// -- Auswertung
	// -------------------------------------------------------------

	@Override
	public void bildeKategorieSumme(final Zeitraum zeitraum, final boolean unterkat) {
		// Umbuchungen haben keine IKategorie!
	}

	@Override
	public Geldbetrag getKategorieWert(final EinzelKategorie namekat, final boolean unterkat) {
		// Umbuchungen haben keine IKategorie!
		return Geldbetrag.NULL_EURO;
	}


	// -- Methode des Interface 'Cloneable'
	// --------------------------------------

	@Override
	public final Object clone() {
		final Datum clonedDatum = (Datum) getDatum().clone();
		final String text = new String(getText());
		final UmbuchungKategorie clonedKategorie = (UmbuchungKategorie) this.kategorie.clone();
		final Geldbetrag clonedWert = (Geldbetrag) getWert().clone();
		Umbuchung neueUmbuchung = new Umbuchung();

		try {
			neueUmbuchung = (Umbuchung) super.clone();
		} catch (final CloneNotSupportedException e) {
			LOGGER.warning("Cloning error. This should never happen.");
		}

		neueUmbuchung.setDatum(clonedDatum);
		neueUmbuchung.setText(text);
		neueUmbuchung.kategorie = clonedKategorie;
		neueUmbuchung.setWert(clonedWert);
		return neueUmbuchung;
	}

}