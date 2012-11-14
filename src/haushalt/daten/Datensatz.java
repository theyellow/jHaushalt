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

/**
 * Fasst ein Register und eine Buchung zu einem vollständigen Datensatz
 * zusammen.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.1/2006.02.10
 */

/*
 * 2006.02.10 Erste Version
 */

public class Datensatz {

	private final Register register;
	private final AbstractBuchung buchung;

	public Datensatz(final Register register, final AbstractBuchung buchung) {
		this.register = register;
		this.buchung = buchung;
	}

	public AbstractBuchung getBuchung() {
		return this.buchung;
	}

	public Register getRegister() {
		return this.register;
	}

}
