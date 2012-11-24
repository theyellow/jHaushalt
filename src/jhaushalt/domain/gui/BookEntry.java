package jhaushalt.domain.gui;

import jhaushalt.domain.Geldbetrag;
import jhaushalt.domain.kategorie.Kategorie;
import jhaushalt.domain.zeitraum.Datum;

public class BookEntry {

	private Datum date;
	private String description;
	private Kategorie kategorie;
	private Geldbetrag value;
	
	public BookEntry(Datum date, String description, Kategorie kategorie, Geldbetrag value) {
		this.date = date;
		this.description = description;
		this.kategorie = kategorie;
		this.value = value;
	}
	
	public Datum getDate() {
		return date;
	}
	
	public String getDescription() {
		return description;
	}
	
	
	public Kategorie getKategorie() {
		return kategorie;
	}
	
	public Geldbetrag getValue() {
		return value;
	}
}
