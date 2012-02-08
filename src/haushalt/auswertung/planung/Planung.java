/*

This file is part of jHaushalt.

jHaushalt is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

jHaushalt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.


(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

package haushalt.auswertung.planung;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import haushalt.daten.Datenbasis;
import haushalt.daten.Datum;
import haushalt.daten.Euro;
import haushalt.daten.EinzelKategorie;
import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.FreierZeitraum;
import haushalt.daten.zeitraum.Jahr;
import haushalt.gui.TextResource;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5.1/2008.03.10
 */

/*
 * 2008.03.10 BugFix: Fehlender Resource-String
 * 2007.07.03 Internationalisierung
 * 2006.04.21 BugFix: Bei Wechsel auf 'nur Hauptkatgorien'
 *            müssen die Unterkategorien deaktiviert werden
 * 2006.02.04 Erste Version
 */

public class Planung {
  private static final TextResource res = TextResource.get();

  private AbstractZeitraum zeitraum;
  private ArrayList<EinzelKategorie> kategorien = new ArrayList<EinzelKategorie>();
  private ArrayList<Euro> betrag = new ArrayList<Euro>();
  private ArrayList<Boolean> verwenden = new ArrayList<Boolean>();
  private final Datenbasis db;
  private boolean unterkategorien = true;
  private boolean hochrechnen = true;
  private int[] hauptkategorien;
  
  public Planung(Datenbasis db) {
    this.zeitraum = new Jahr(2006);
    this.db = db;
    kategorienAbgleichen();
  }
  
  
  public void laden(DataInputStream in) throws IOException {
    zeitraum = AbstractZeitraum.laden(in);
    unterkategorien = in.readBoolean();
    hochrechnen = in.readBoolean();
    kategorien.clear();
    betrag.clear();
    verwenden.clear();
    int size = in.readInt();
    for (int i = 0; i < size; i++) {
      String kategorie = in.readUTF();
      kategorien.add(db.findeOderErzeugeKategorie(kategorie));
      betrag.add(new Euro());
      betrag.get(i).laden(in);
      verwenden.add(Boolean.valueOf(in.readBoolean()));
    }
    kategorienAbgleichen();
  }
  
  public void speichern(DataOutputStream out) throws IOException {
    zeitraum.speichern(out);
    out.writeBoolean(unterkategorien);
    out.writeBoolean(hochrechnen);
    out.writeInt(kategorien.size());
    for(int i=0; i<kategorien.size(); i++) {
      out.writeUTF(""+kategorien.get(i));
      betrag.get(i).speichern(out);
      out.writeBoolean(verwenden.get(i).booleanValue());
    }
    out.flush();
  }

  public int getAnzahlKategorien() {
    return (unterkategorien)?kategorien.size():hauptkategorien.length;
  }

  public Boolean kategorieVerwenden(int idx) {
    return (unterkategorien)?verwenden.get(idx):verwenden.get(hauptkategorien[idx]);
  }

  public Euro getSumme() {
    Euro summe = new Euro();
    for (int i = 0; i < betrag.size(); i++)
      if(verwenden.get(i).booleanValue())
        summe.sum(betrag.get(i));
    return summe;
  }

  public EinzelKategorie getKategorie(int idx) {
    return (unterkategorien)?kategorien.get(idx):kategorien.get(hauptkategorien[idx]);
  }

  public Object getBetrag(int idx) {
    return (unterkategorien)?betrag.get(idx):betrag.get(hauptkategorien[idx]);
  }

  public void setBetrag(int idx, Euro wert) {
    if(unterkategorien)
      betrag.set(idx, wert);
    else
      betrag.set(hauptkategorien[idx], wert);
  }

  public void setVerwenden(int idx, Boolean wert) {
    if(unterkategorien)
      verwenden.set(idx, wert);
    else
      verwenden.set(hauptkategorien[idx], wert);
  }

  public AbstractZeitraum getZeitraum() {
    return zeitraum;
  }

  public void setZeitraum(AbstractZeitraum zeitraum) {
    this.zeitraum = zeitraum;
  }


  public void alleVerwenden(boolean b) {
    for (int i = 0; i < verwenden.size(); i++)
      verwenden.set(i, Boolean.valueOf(b));
  }

  public void setUnterkategorien(boolean b) {
    unterkategorien = b;
    if(!unterkategorien)
      for (int i = 0; i < hauptkategorien.length; i++) {
        int count = hauptkategorien[i];
        Euro summe = new Euro();
        // Der Hauptkategotrie werden die Beträge der Unter-
        // kategorien hinzuaddiert; die Unterkategorien werden
        // deaktiviert
        while((count < kategorien.size()) &&
            (kategorien.get(count).istInKategorie(kategorien.get(hauptkategorien[i]), false))) {
          summe.sum(betrag.get(count));
          if(!kategorien.get(count).isHauptkategorie())
            verwenden.set(count, Boolean.FALSE);
          betrag.set(count++, new Euro());
        }
        betrag.set(hauptkategorien[i], summe);
      }
  }

  public boolean isUnterkategorien() {
    return unterkategorien;
  }
  
  public String[][] getVergleich() {
    ArrayList<String[]> tabelle = new ArrayList<String[]>();
    Euro[] summen = db.getKategorieSalden(zeitraum, unterkategorien);
    String[] zeile = {
        res.getString("category"),
        res.getString("forecast"),
        res.getString("actual"),
        res.getString("difference")
    };
    tabelle.add(zeile);
    Euro summe = new Euro();
    double faktor = 1.0D;
    Datum heute = new Datum();
    if(hochrechnen && heute.istImZeitraum(zeitraum)) {
      double anzahlGesamttage = zeitraum.getAnzahlTage();
      AbstractZeitraum istZeitraum = new FreierZeitraum(zeitraum.getStartDatum(), heute);
      faktor = anzahlGesamttage / istZeitraum.getAnzahlTage();
    }
    for(int i=0;i<kategorien.size();i++)
      if(verwenden.get(i)){
        Euro hochgerechneterBetrag = summen[i].mal(faktor);
        zeile = new String[4];
        zeile[0] = ""+kategorien.get(i);
        zeile[1] = ""+betrag.get(i);
        zeile[2] = ""+hochgerechneterBetrag;
        zeile[3] = ""+betrag.get(i).sub(hochgerechneterBetrag);
        tabelle.add(zeile);
        summe.sum(hochgerechneterBetrag);
      }
    zeile = new String[4];
    zeile[0] = res.getString("total");
    zeile[1] = ""+getSumme();
    zeile[2] = ""+summe;
    zeile[3] = ""+getSumme().sub(summe);
    tabelle.add(zeile);
    return tabelle.toArray(new String[tabelle.size()][4]);
  }

  public void kategorienAbgleichen() {
    EinzelKategorie[] kat = db.getKategorien(true);
    // Kategorien die es noch nicht gibt werden hinzugefügt
    for (int i = 0; i < kat.length; i++) 
      if((i >= kategorien.size()) ||
          (kat[i] != kategorien.get(i))) {
        kategorien.add(i, kat[i]);
        betrag.add(i, new Euro());
        verwenden.add(i, Boolean.FALSE);
      }
    // Index auf die Hauptkategorien aufbauen
    int anz = 0;
    for (int i = 0; i < kategorien.size(); i++)
      if(kategorien.get(i).isHauptkategorie())
        anz++;
    this.hauptkategorien = new int[anz];
    anz = 0;
    for (int i = 0; i < kategorien.size(); i++)
      if(kategorien.get(i).isHauptkategorie())
        hauptkategorien[anz++] = i;
  }


  public boolean isHochrechnen() {
    return hochrechnen;
  }


  public void setHochrechnen(boolean hochrechnen) {
    this.hochrechnen = hochrechnen;
  }

  public String getTextHochrechnen() {
    if(!hochrechnen)
      return res.getString("extrapolation_used");
    int anzahlGesamttage = zeitraum.getAnzahlTage();
    AbstractZeitraum istZeitraum = new FreierZeitraum(zeitraum.getStartDatum(), new Datum());
    return res.getString("extrapolation_unused")+" ("+
      istZeitraum.getAnzahlTage()+" --> "+
      anzahlGesamttage+")";
  }
}
