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

package jhaushalt.domain.kategorie;

import jhaushalt.domain.Geldbetrag;
import haushalt.gui.TextResource;

/**
 * Verwaltet alle Kategorien.
 * Standardmäßig steht die die IKategorie "Sonstiges" zur Verfügung.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5/2008.01.22
 */

/*
 * 2008.01.22 Internationalisierung
 * 2004.08.22 Version 2.0
 */

public class EinzelKategorie implements Kategorie {

	public static final EinzelKategorie SONSTIGES = new EinzelKategorie(TextResource.get().getString("miscellaneous"), null);

	private final EinzelKategorie hauptkategorie;
	private String name;
	private Geldbetrag summe = new Geldbetrag();

	public EinzelKategorie(final String name, final EinzelKategorie hauptkategorie) {
		this.name = name;
		this.hauptkategorie = hauptkategorie;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Geldbetrag getSumme() {
		return this.summe;
	}

	public void loescheSumme() {
		this.summe = new Geldbetrag();
	}

	public boolean isHauptkategorie() {
		return this.hauptkategorie == null;
	}

	public EinzelKategorie getHauptkategorie() {
		if (isHauptkategorie()) {
			return this;
		}
		return this.hauptkategorie.getHauptkategorie();
	}

	public boolean istInKategorie(final EinzelKategorie kategorie, final boolean unterkategorienVerwenden) {
		if (kategorie == this) {
			return true;
		}
		// wenn die Unterkatgorien NICHT verwendet werden, dann
		// muss überprüft werden, ob die Hauptkategorie passt:
		if ((!unterkategorienVerwenden) && (getHauptkategorie() == kategorie)) {
			return true;
		}
		return false;
	}

	// -- IKategorie-Summe
	// -------------------------------------------------------

	public void addiereWert(final Geldbetrag wert, final boolean unterkat) {
		if (unterkat) {
			this.summe.sum(wert);
		} else {
			getHauptkategorie().summe.sum(wert);
		}
	}

	// -- Methoden fuer Interface: Comparable
	// -----------------------------------

	public int compareTo(final Kategorie kategorie) {
		return toString().compareToIgnoreCase(kategorie.toString());
	}

	public String toString() {
		StringBuilder result = new StringBuilder("[ SingleCategory: ");
		if (this.hauptkategorie != null) {
			result.append(hauptkategorie).append(" -> ");
		}
		result.append(name);
		result.append(" ]");
		return this.name;
	}

	public void setWert(Geldbetrag betrag) {
		summe = betrag;
	}

}
