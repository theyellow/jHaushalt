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

package haushalt.daten;

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

public class EinzelKategorie implements IKategorie {

	public static final EinzelKategorie SONSTIGES = new EinzelKategorie(TextResource.get().getString("miscellaneous"), null);

	private final EinzelKategorie hauptkategorie;
	private String name;
	private Euro summe = new Euro();

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

	@Override
	public String toString() {
		if (this.hauptkategorie != null) {
			return "" + this.hauptkategorie + ":" + this.name;
		}
		return this.name;
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

	public void addiereWert(final Euro wert, final boolean unterkat) {
		if (unterkat) {
			this.summe.sum(wert);
		} else {
			getHauptkategorie().summe.sum(wert);
		}
	}

	public Euro getSumme() {
		return this.summe;
	}

	public void loescheSumme() {
		this.summe = new Euro();
	}

	// -- Methoden fuer Interface: Comparable
	// -----------------------------------

	public int compareTo(final IKategorie kategorie) {
		return toString().compareToIgnoreCase(kategorie.toString());
	}

}
