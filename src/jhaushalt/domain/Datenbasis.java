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

package jhaushalt.domain;

import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.StandardBuchung;
import jhaushalt.domain.buchung.Umbuchung;
import jhaushalt.domain.kategorie.EinzelKategorie;
import jhaushalt.domain.kategorie.UmbuchungKategorie;
import jhaushalt.domain.zeitraum.Zeitraum;
import jhaushalt.domain.zeitraum.Datum;

/**
 * Die Datenbasis beinhaltet alle Buchungen und dient zum
 * Zugriff auf alle Daten. Sie verwaltet die Register und
 * Kategorien. In der Datenbasis sind auch die gemerkten
 * Buchungen gespeichert.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5.3/2008.03.31
 */
public class Datenbasis {

	public static final String VERSION_DATENBASIS = "2.1.2";
	private static final String[] LEGACY_INTERVALL_NAMEN = {"Woche", "Monat", "Quartal", "Halbjahr", "Jahr"};

	private static int cacheHit = 0;
	private static int cacheMiss = 0;
	
	private boolean geaendert = false;

	// -- Kategorien --------------------------------------------
	private final ArrayList<EinzelKategorie> kategorieListe;

	// -- Register ----------------------------------------------
	private List<Register> registerListe;

	// -- Suchen und Ersetzen -----------------------------------
	private int registerSuchIdx = 0;
	private int buchungSuchIdx = 0;

	private Zeitraum zeitraumImCache;
	private String registerImCache;
	private boolean cacheAktuell = false;
	private boolean cacheMitUnterkategorien = true;

	// -- gemerkte Buchungen ------------------------------------
	private Datum startDatumGemerkteBuchungen = new Datum();
	private final List<Buchung> gemerkteBuchungen = new ArrayList<Buchung>();
	private final List<String> gemerkteBuchungenText = new ArrayList<String>();

	// -- Auto-Buchung ------------------------------------------
	private List<Buchung> autoStandardBuchungen = new ArrayList<Buchung>();
	private final List<Register> autoStandardBuchungRegister = new ArrayList<Register>();
	private final List<Integer> autoStandardBuchungIntervalle = new ArrayList<Integer>();
	private final List<Umbuchung> autoUmbuchungen = new ArrayList<Umbuchung>();
	private final List<UmbuchungKategorie> autoUmbuchungRegister = new ArrayList<UmbuchungKategorie>();
	private final List<Integer> autoUmbuchungIntervalle = new ArrayList<Integer>();

	private String filename;
	private String versionInfo;

	public Datenbasis() {
		this.kategorieListe = new ArrayList<EinzelKategorie>();
		this.kategorieListe.add(EinzelKategorie.SONSTIGES);
	}

	public static boolean givenVersionEqualsDatabaseVersion(String version) {
		 return version.equals("jHaushalt" + VERSION_DATENBASIS);
	}
	
	/**
	 * Ist <code>true</code>, wenn Daten geändert wurden.
	 * 
	 * @return geändert oder nicht geändert
	 */
	public boolean isGeaendert() {
		return this.geaendert;
	}

	/**
	 * Wird aufgerufen, wenn die Daten geaendert wurden
	 */
	public void setGeaendert() {
		this.geaendert = true;
	}


	public static int getCacheHit() {
		return cacheHit;
	}

	public static void setCacheHit(final int hit) {
		cacheHit = hit;
	}

	public static int getCacheMiss() {
		return cacheMiss;
	}

	public static void setCacheMiss(final int miss) {
		cacheMiss = miss;
	}

	private Integer getLegacyIntervallIndex(final String name) {
		for (int i = 0; i < LEGACY_INTERVALL_NAMEN.length; i++) {
			if (name.equals(LEGACY_INTERVALL_NAMEN[i])) {
				return new Integer(i);
			}
		}
		return new Integer(0);
	}

	/**
	 * Liefert die Anzahl der vorhandenen wiederkehrenden Standard-Buchungen.
	 * 
	 * @return Anzahl
	 */
	public int getAnzahlAutoStandardBuchungen() {
		return this.autoStandardBuchungen.size();
	}

	/**
	 * Liefert die Anzahl der vorhandenen wiederkehrenden Umbuchungen.
	 * 
	 * @return Anzahl
	 */
	public int getAnzahlAutoUmbuchungen() {
		return this.autoUmbuchungen.size();
	}

	/**
	 * Liefert eine wiederkehrende Standard-Buchung.
	 * 
	 * @param index
	 *            Nummer der Buchung
	 * @return Buchung
	 */
	public StandardBuchung getAutoStandardBuchung(final int index) {
		// FIXME und was jetzt??? das können auch andere Buchungstypen sein
		return (StandardBuchung) this.autoStandardBuchungen.get(index);
	}

	/**
	 * Liefert eine wiederkehrende Umuchung.
	 * 
	 * @param index
	 *            Nummer der Buchung
	 * @return Buchung
	 */
	public Umbuchung getAutoUmbuchung(final int index) {
		return this.autoUmbuchungen.get(index);
	}

	/**
	 * Liefert das Register zu einer wiederkehrenden Standard-Buchung.
	 * 
	 * @param index
	 *            Nummer der Buchung
	 * @return Name des Registers
	 */
	public String getAutoStandardBuchungRegister(final int index) {
		return "" + this.autoStandardBuchungRegister.get(index);
	}

	/**
	 * Liefert die Register zu einer wiederkehrenden Umbuchung.
	 * 
	 * @param index
	 *            Nummer der Buchung
	 * @return Register-Paar
	 */
	public UmbuchungKategorie getAutoUmbuchungRegister(final int index) {
		return this.autoUmbuchungRegister.get(index);
	}


	public void setFileName(String absolutePathAndFileName) {
		this.filename = absolutePathAndFileName;
	}

	public String getFilename() {
		return filename;
	}




	/**
	 * Verschiebt ein Register innerhalb der Register-Liste.
	 * 
	 * @param register
	 *            Register
	 * @param indexNeu
	 *            Neuer Index des Registers
	 */
	private void aendereRegisterIndex(final Register register, final int indexNeu) {
		this.registerListe.remove(register);
		this.registerListe.add(indexNeu, register);
	}

	/**
	 * Verschiebt ein Register innerhalb der Register-Liste.
	 * 
	 * @param regname
	 *            Name des Registers
	 * @param indexNeu
	 *            Neuer Index des Registers
	 */
	public void aendereRegisterIndex(final String regname, final int indexNeu) {
		Register register = null;
		for (int i = 0; i < this.registerListe.size(); i++) {
			if (regname.equals("" + this.registerListe.get(i))) {
				register = this.registerListe.get(i);
			}
		}
		if (register != null) {
			aendereRegisterIndex(register, indexNeu);
		}
	}


	/// VERSION INFO
	
	public void setVersionInfo(String versionInfo) {
		this.versionInfo = versionInfo;		
	}

	public String getVersionInfo() {
		return this.versionInfo;
	}

	/// REGISTER LIST

	public List<Register> getRegisterList() {
		return this.registerListe;
	}
	
	public void setRegisterList(List<Register> registerList) {
		this.registerListe = registerList;
		
	}

	public void setAutomatedEntries(List<Buchung> automatedEntries) {
		this.autoStandardBuchungen = automatedEntries;
		
	}
}
