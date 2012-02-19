/*

This file is part of jHaushalt.

jHaushalt is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

jHaushalt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.


(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

package haushalt.daten;

import haushalt.daten.zeitraum.AbstractZeitraum;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Die StandardBuchung ist die "normale" Buchung. Sie besteht aus Datum, Name,
 * Kategorie und Betrag.
 * @author Dr. Lars H. Hahn
 * @version 2.6/2010.02.03
 */

/*
 * 2010.02.03 Funktion clone() korrigiert: new String() verwendet
 * 2006.02.10 Ergänzung der Methode isInKategorie
 * 2004.08.22 Erste Version
 */

public class StandardBuchung extends AbstractBuchung {

  private EinzelKategorie kategorie = null;
  private Euro betrag;
  
  public StandardBuchung() {
		setKategorie(EinzelKategorie.SONSTIGES);
		this.betrag = new Euro();
  }

  public StandardBuchung(Datum datum, String text, EinzelKategorie kategorie, Euro betrag) {
    setDatum(datum);
    setText(text);
    setKategorie(kategorie);
    this.betrag = betrag;
  }

  // -- Kategorie --------------------------------------------------------------

  public Kategorie getKategorie() {
  	return kategorie;
  }
  
  public void setKategorie(Kategorie neueKategorie) {
  	kategorie = (EinzelKategorie)neueKategorie;
  }
  
  public int ersetzeKategorie(EinzelKategorie alteKategorie, EinzelKategorie neueKategorie) {
    if((kategorie == alteKategorie) || (alteKategorie == null)) {
      kategorie = neueKategorie;
      return 1;
    }
    return 0;
  }

  public boolean istInKategorie(EinzelKategorie kategorie, boolean unterkategorienVerwenden) {
    return this.kategorie.istInKategorie(kategorie, unterkategorienVerwenden);
  }

  // -- Buchungswert -----------------------------------------------------------

  public Euro getWert() {
    return betrag;
  }

  public void setWert(Euro wert) {
    betrag = wert;
  }

  // -- Auswertung -------------------------------------------------------------

  public void bildeKategorieSumme(AbstractZeitraum zeitraum, boolean unterkat) {
    if((zeitraum == null) || getDatum().istImZeitraum(zeitraum))
      kategorie.addiereWert(betrag, unterkat);
  }
  
	public Euro getKategorieWert(EinzelKategorie namekat, boolean unterkat) {
		if(kategorie.istInKategorie(namekat, unterkat))
				return betrag;
		return Euro.NULL_EURO;
	}

  // -- E/A-Funktionen ---------------------------------------------------------

  public void laden(DataInputStream in, Datenbasis db)
    throws IOException {
		getDatum().laden(in);
		setText(in.readUTF());
		setKategorie(db.findeOderErzeugeKategorie(in.readUTF()));
		betrag.laden(in);
  }

  public void speichern(DataOutputStream out)
    throws IOException {
    // Bis zur Version 1.2 gab es keine Unterscheidung zwischen StandardBuchung
    // und SplitBuchung. Damit Daten von alten Versionen gelesen werden können,
    // muss die neue StandardBuchung mit einem anderen Namen gespeichert werden.
    out.writeUTF("StandardBuchung2");
		getDatum().speichern(out);
		out.writeUTF(getText());
		out.writeUTF(""+kategorie);
		betrag.speichern(out);
  }

  // -- Methode des Interface 'Cloneable' --------------------------------------

  final public Object clone() {
    return new StandardBuchung((Datum)getDatum().clone(), new String(getText()), kategorie, (Euro)betrag.clone());
  }

}