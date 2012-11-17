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

package jhaushalt.domain.zeitraum;

public abstract class Zeitraum {

	/**
	 * Liefert den ersten Tag des Zeitraums.
	 * @return erster Tag des Zeitraums
	 */
	public abstract Datum getStartDatum();

	/**
	 * Liefert den letzten Tag des Zeitraums.
	 * @return letzter Tag des Zeitraums
	 */
	public abstract Datum getEndDatum();

	/**
	 * Liefert den auf diesen Zeitraum folgenden Zeitraum mit
	 * gleicher Länge.
	 * @return nächster Zeitraum
	 */
	public abstract Zeitraum folgeZeitraum();

	/**
	 * Liefert die Anzahl der Tage des Zeitraums.
	 * @return Anzahl der Tage
	 */
	public final int getAnzahlTage() {
		return (int) getEndDatum().sub(getStartDatum());
	}

	/**
	 * Liefert eine textuelle Beschreibung des Zeitraums.
	 * @return Textbeschreibung des Zeitraums
	 */
	@Override
	public abstract String toString();

	/**
	 * Liefert den String der zum Speichern des Zeitraums verwendet wird.
	 * In der Regel entspricht dies 'toString()'.
	 * @return String zum Speichern
	 * @see Zeitraum#toString()
	 */
	public String getDatenString() {
		return toString();
	}

	public final boolean equals(final Zeitraum zeitraum) {
		if (zeitraum == null) {
			return false;
		}
		if (getStartDatum().compareTo(zeitraum.getStartDatum()) != 0) {
			return false;
		}
		if (getEndDatum().compareTo(zeitraum.getEndDatum()) != 0) {
			return false;
		}
		return true;
	}

	@Override
	public final boolean equals(final Object zeitraum) {
		return equals((Zeitraum) zeitraum);
	}

	@Override
	public int hashCode() {
		assert false : "hashCode not designed";
		return 0;
	}

}
