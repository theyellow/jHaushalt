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
package jhaushalt.domain;

import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.buchung.Buchung;

public class Register implements Comparable<Register> {

	private String name;
	private final ArrayList<Buchung> buchungen;

	public Register(final String name) {
		this.name = name;
		this.buchungen = new ArrayList<Buchung>();
	}

	public String getName() {
		return name;
	}
	
	public void setName(final String neuerName) {
		this.name = neuerName;
	}

	public List<Buchung> getBookings() {
		return this.buchungen;
	}

	public int getAnzahlBuchungen() {
		return buchungen.size();
	}

	public void insertBookingList(List<Buchung> bookingList) {
		buchungen.addAll(bookingList);
	}

	public void addBooking(final Buchung buchung) {
		this.buchungen.add(buchung);
	}


	
	public int compareTo(final Register register) {
		return this.name.compareTo(register.name);
	}
	
	public String toString() {
		return "Register: "+this.name;
	}

}
