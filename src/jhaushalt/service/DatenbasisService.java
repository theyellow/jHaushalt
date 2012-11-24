package jhaushalt.service;

import java.util.ArrayList;

import jhaushalt.domain.gui.BookEntry;
import jhaushalt.domain.kategorie.EinzelKategorie;
import jhaushalt.domain.zeitraum.Zeitraum;

public interface DatenbasisService {
	
	/**
	 * Liefert alle Buchungen einer Kategorie in einem Zeitraum.
	 * 
	 * @param zeitraum
	 *            Zeitraum
	 * @param regname
	 *            Name des Registers
	 * @param kategorien
	 *            Liste mit Kategorien
	 * @return Liste der Buchungen
	 */
	public ArrayList<BookEntry> getBuchungen(
			final Zeitraum zeitraum, final String regname, final EinzelKategorie[] kategorien,
			final boolean unterkategorienVerwenden);
}