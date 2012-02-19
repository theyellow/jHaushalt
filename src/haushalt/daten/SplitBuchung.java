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
along with jHaushalt; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

/*
 * 2005.03.30 BugFix: Clonen gefixed
 */

package haushalt.daten;

import haushalt.daten.zeitraum.AbstractZeitraum;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Die SplitBuchung ist ähnlich der StandardBuchung. Der Buchungsbetrag wurde
 * lediglich auf mehrere Kategorien aufgeteilt.
 * @author Dr. Lars H. Hahn
 * @version 2.6/2010.02.03
 */

/*
 * 2010.02.03 Funktion clone() korrigiert: new String() verwendet
 * 2009.07.28 BugFix: Nach Löschung aller Kategorien/Beträge, liefert reduziere() jetzt "SONSTIGES"/0€ zurück
 * 2006.02.10 Ergänzung der Methode isInKategorie
 */

public class SplitBuchung extends AbstractBuchung {

	private MehrfachKategorie splitKategorie = new MehrfachKategorie();
	private ArrayList<Euro> splitBetrag  = new ArrayList<Euro>();

	public SplitBuchung() {
		// wird zum Laden benötigt
	}
	
	public SplitBuchung(Datum datum, String text) {
		setDatum(datum);
		setText(text);
	}

	public SplitBuchung(StandardBuchung buchung) {
		setDatum(buchung.getDatum());
		setText(buchung.getText());
		add((EinzelKategorie) buchung.getKategorie(), buchung.getWert());
	}

  public int getAnzahl() {
  	return splitKategorie.size();
  }
  
	public void add() {
		add(EinzelKategorie.SONSTIGES, new Euro());
	}

	public void add(EinzelKategorie kategorie, Euro betrag) {
		splitKategorie.add(kategorie);
		splitBetrag.add(betrag);
	}
	
	public void loesche(int nr) {
		splitKategorie.remove(nr);
		splitBetrag.remove(nr);
	}
	
	public AbstractBuchung reduziere() {
		if(splitKategorie.size() > 1)
		  return this;
		if(splitKategorie.size() < 1)
    	  return new StandardBuchung(getDatum(), getText(), EinzelKategorie.SONSTIGES, new Euro());
		return new StandardBuchung(getDatum(), getText(), splitKategorie.get(0), splitBetrag.get(0));
	}
	
  // -- Kategorie --------------------------------------------------------------

  public void setKategorie(Kategorie kategorie) {
		splitKategorie = (MehrfachKategorie) kategorie;
	}
	
  public void setKategorie(int nr, EinzelKategorie kategorie) {
	 	splitKategorie.set(nr, kategorie);
	}

  public Kategorie getKategorie() {
    return splitKategorie;
  }

  public EinzelKategorie getKategorie(int nr) {
    return splitKategorie.get(nr);
  }
  
  public int ersetzeKategorie(EinzelKategorie alteKategorie, EinzelKategorie neueKategorie) {
    int zaehler = 0;
	  for(int i=0; i<getAnzahl(); i++)
		  if((alteKategorie == splitKategorie.get(i)) ||
          (alteKategorie == null)) {
			  splitKategorie.set(i, neueKategorie);
			  zaehler ++;
		  }
		return zaehler;
	}

  public boolean istInKategorie(EinzelKategorie kategorie, boolean unterkategorienVerwenden) {
    for(int i=0;i<getAnzahl();i++)
      if(splitKategorie.get(i).istInKategorie(kategorie, unterkategorienVerwenden))
        return true;
    return false;
  }

  // -- Buchungswert -----------------------------------------------------------

  public void setWert(Euro wert) {
    double faktor = wert.toDouble() / getWert().toDouble();
    for(int i=0;i<getAnzahl();i++) {
      Euro neuerWert = getWert(i).mal(faktor);
      setWert(i, neuerWert);
    }
  }

  public void setWert(int nr, Euro wert) {
    splitBetrag.set(nr, wert); 
  }
     
  public Euro getWert() {
    Euro summe = new Euro();
    for(int i=0;i<getAnzahl();i++)
      summe.sum(splitBetrag.get(i));
    return summe;
  }
     
  public Euro getWert(int nr) {
    return splitBetrag.get(nr);
  }
   
  // -- Auswertung -------------------------------------------------------------

  public void bildeKategorieSumme(AbstractZeitraum zeitraum, boolean unterkat) {
		for(int i=0;i<getAnzahl();i++)
		  if(getDatum().istImZeitraum(zeitraum))
			  getKategorie(i).addiereWert(getWert(i), unterkat);
  }
  
  public Euro getKategorieWert(EinzelKategorie namekat, boolean unterkat) {
 	  Euro summe = new Euro();
  	for(int i=0;i<getAnzahl();i++)
  	  if(getKategorie(i).istInKategorie(namekat, unterkat))
  		  summe.sum(getWert(i));
  	return summe;
  }

  // -- E/A-Funktionen ---------------------------------------------------------

  public void laden(DataInputStream in, Datenbasis db)
		throws IOException {
		getDatum().laden(in);
		setText(in.readUTF());
		int size = in.readInt();
		splitKategorie = new MehrfachKategorie(size);
		splitBetrag = new ArrayList<Euro>(size);
		for(int i=0; i<size; i++) {
		  String kategorie = in.readUTF();
		  Euro betrag = new Euro();
			betrag.laden(in);
			add(db.findeOderErzeugeKategorie(kategorie), betrag);
		}
	}

	public void speichern(DataOutputStream out)
	  throws IOException {
		out.writeUTF("SplitBuchung");
		getDatum().speichern(out);
		out.writeUTF(getText());
		out.writeInt(splitKategorie.size());
		for(int i=0; i<getAnzahl(); i++) {
		  out.writeUTF(""+splitKategorie.get(i));
			splitBetrag.get(i).speichern(out);
		}
	 }

  // -- Methode des Interface 'Cloneable' --------------------------------------

  final public Object clone() {
    SplitBuchung kopie = new SplitBuchung((Datum)getDatum().clone(), new String(getText()));
    for(int i=0;i<this.getAnzahl();i++)
      // Kategorien NICHT clonen, da dann nicht in der Kategorie-Liste
      kopie.add(splitKategorie.get(i), (Euro)splitBetrag.get(i).clone());
    return kopie;
  }

}
