package jhaushalt.service;

import jhaushalt.domain.Geldbetrag;
import jhaushalt.domain.Register;
import jhaushalt.domain.buchung.StandardBuchung;
import jhaushalt.domain.kategorie.EinzelKategorie;
import jhaushalt.domain.zeitraum.Datum;

public class RegisterBuilder {

	private Register register;
	private EinzelKategorie kategorie;

	public RegisterBuilder(String registerName, EinzelKategorie kategorie) {
		register = new Register(registerName);
		this.kategorie = kategorie;
	}

	public RegisterBuilder addBooking(int day, int month, int year, String beschreibung, double betrag) {
		register.addBooking(createBuchung(day, month, year, beschreibung, betrag));
		return this;
	}

	public Register getRegister() {
		return register;
	}

	private StandardBuchung createBuchung(int day, int month, int year, String beschreibung, double betrag) {
		return new StandardBuchung(new Datum(day, month, year), beschreibung, kategorie, new Geldbetrag(betrag));
	}

}
