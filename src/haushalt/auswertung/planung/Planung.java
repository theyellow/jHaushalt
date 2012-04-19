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

package haushalt.auswertung.planung;

import haushalt.daten.Datenbasis;
import haushalt.daten.Datum;
import haushalt.daten.EinzelKategorie;
import haushalt.daten.Euro;
import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.FreierZeitraum;
import haushalt.daten.zeitraum.Jahr;
import haushalt.gui.TextResource;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5.1/2008.03.10
 */

/*
 * 2008.03.10 BugFix: Fehlender Resource-String
 * 2007.07.03 Internationalisierung
 * 2006.04.21 BugFix: Bei Wechsel auf 'nur Hauptkatgorien'
 * müssen die Unterkategorien deaktiviert werden
 * 2006.02.04 Erste Version
 */

public class Planung {

	private static final TextResource res = TextResource.get();

	private AbstractZeitraum zeitraum;
	private final ArrayList<EinzelKategorie> kategorien = new ArrayList<EinzelKategorie>();
	private final ArrayList<Euro> betrag = new ArrayList<Euro>();
	private final ArrayList<Boolean> verwenden = new ArrayList<Boolean>();
	private final Datenbasis db;
	private boolean unterkategorien = true;
	private boolean hochrechnen = true;
	private int[] hauptkategorien;

	public Planung(final Datenbasis db) {
		this.zeitraum = new Jahr(2006);
		this.db = db;
		kategorienAbgleichen();
	}

	public void laden(final DataInputStream in) throws IOException {
		this.zeitraum = AbstractZeitraum.laden(in);
		this.unterkategorien = in.readBoolean();
		this.hochrechnen = in.readBoolean();
		this.kategorien.clear();
		this.betrag.clear();
		this.verwenden.clear();
		final int size = in.readInt();
		for (int i = 0; i < size; i++) {
			final String kategorie = in.readUTF();
			this.kategorien.add(this.db.findeOderErzeugeKategorie(kategorie));
			this.betrag.add(new Euro());
			this.betrag.get(i).laden(in);
			this.verwenden.add(Boolean.valueOf(in.readBoolean()));
		}
		kategorienAbgleichen();
	}

	public void speichern(final DataOutputStream out) throws IOException {
		this.zeitraum.speichern(out);
		out.writeBoolean(this.unterkategorien);
		out.writeBoolean(this.hochrechnen);
		out.writeInt(this.kategorien.size());
		for (int i = 0; i < this.kategorien.size(); i++) {
			out.writeUTF("" + this.kategorien.get(i));
			this.betrag.get(i).speichern(out);
			out.writeBoolean(this.verwenden.get(i).booleanValue());
		}
		out.flush();
	}

	public int getAnzahlKategorien() {
		return (this.unterkategorien) ? this.kategorien.size() : this.hauptkategorien.length;
	}

	public Boolean kategorieVerwenden(final int idx) {
		return (this.unterkategorien) ? this.verwenden.get(idx) : this.verwenden.get(this.hauptkategorien[idx]);
	}

	public Euro getSumme() {
		final Euro summe = new Euro();
		for (int i = 0; i < this.betrag.size(); i++) {
			if (this.verwenden.get(i).booleanValue()) {
				summe.sum(this.betrag.get(i));
			}
		}
		return summe;
	}

	public EinzelKategorie getKategorie(final int idx) {
		return (this.unterkategorien) ? this.kategorien.get(idx) : this.kategorien.get(this.hauptkategorien[idx]);
	}

	public Object getBetrag(final int idx) {
		return (this.unterkategorien) ? this.betrag.get(idx) : this.betrag.get(this.hauptkategorien[idx]);
	}

	public void setBetrag(final int idx, final Euro wert) {
		if (this.unterkategorien) {
			this.betrag.set(idx, wert);
		}
		else {
			this.betrag.set(this.hauptkategorien[idx], wert);
		}
	}

	public void setVerwenden(final int idx, final Boolean wert) {
		if (this.unterkategorien) {
			this.verwenden.set(idx, wert);
		}
		else {
			this.verwenden.set(this.hauptkategorien[idx], wert);
		}
	}

	public AbstractZeitraum getZeitraum() {
		return this.zeitraum;
	}

	public void setZeitraum(final AbstractZeitraum zeitraum) {
		this.zeitraum = zeitraum;
	}

	public void alleVerwenden(final boolean b) {
		for (int i = 0; i < this.verwenden.size(); i++) {
			this.verwenden.set(i, Boolean.valueOf(b));
		}
	}

	public void setUnterkategorien(final boolean b) {
		this.unterkategorien = b;
		if (!this.unterkategorien) {
			for (int i = 0; i < this.hauptkategorien.length; i++) {
				int count = this.hauptkategorien[i];
				final Euro summe = new Euro();
				// Der Hauptkategotrie werden die Beträge der Unter-
				// kategorien hinzuaddiert; die Unterkategorien werden
				// deaktiviert
				while ((count < this.kategorien.size())
						&&
						(this.kategorien.get(count).istInKategorie(this.kategorien.get(this.hauptkategorien[i]), false))) {
					summe.sum(this.betrag.get(count));
					if (!this.kategorien.get(count).isHauptkategorie()) {
						this.verwenden.set(count, Boolean.FALSE);
					}
					this.betrag.set(count++, new Euro());
				}
				this.betrag.set(this.hauptkategorien[i], summe);
			}
		}
	}

	public boolean isUnterkategorien() {
		return this.unterkategorien;
	}

	public String[][] getVergleich() {
		final ArrayList<String[]> tabelle = new ArrayList<String[]>();
		final Euro[] summen = this.db.getKategorieSalden(this.zeitraum, this.unterkategorien);
		String[] zeile = {
				res.getString("category"),
				res.getString("forecast"),
				res.getString("actual"),
				res.getString("difference")
		};
		tabelle.add(zeile);
		final Euro summe = new Euro();
		double faktor = 1.0D;
		final Datum heute = new Datum();
		if (this.hochrechnen && heute.istImZeitraum(this.zeitraum)) {
			final double anzahlGesamttage = this.zeitraum.getAnzahlTage();
			final AbstractZeitraum istZeitraum = new FreierZeitraum(this.zeitraum.getStartDatum(), heute);
			faktor = anzahlGesamttage / istZeitraum.getAnzahlTage();
		}
		for (int i = 0; i < this.kategorien.size(); i++) {
			if (this.verwenden.get(i)) {
				final Euro hochgerechneterBetrag = summen[i].mal(faktor);
				zeile = new String[4];
				zeile[0] = "" + this.kategorien.get(i);
				zeile[1] = "" + this.betrag.get(i);
				zeile[2] = "" + hochgerechneterBetrag;
				zeile[3] = "" + this.betrag.get(i).sub(hochgerechneterBetrag);
				tabelle.add(zeile);
				summe.sum(hochgerechneterBetrag);
			}
		}
		zeile = new String[4];
		zeile[0] = res.getString("total");
		zeile[1] = "" + getSumme();
		zeile[2] = "" + summe;
		zeile[3] = "" + getSumme().sub(summe);
		tabelle.add(zeile);
		return tabelle.toArray(new String[tabelle.size()][4]);
	}

	public void kategorienAbgleichen() {
		final EinzelKategorie[] kat = this.db.getKategorien(true);
		// Kategorien die es noch nicht gibt werden hinzugefügt
		for (int i = 0; i < kat.length; i++) {
			if ((i >= this.kategorien.size()) ||
					(kat[i] != this.kategorien.get(i))) {
				this.kategorien.add(i, kat[i]);
				this.betrag.add(i, new Euro());
				this.verwenden.add(i, Boolean.FALSE);
			}
		}
		// Index auf die Hauptkategorien aufbauen
		int anz = 0;
		for (int i = 0; i < this.kategorien.size(); i++) {
			if (this.kategorien.get(i).isHauptkategorie()) {
				anz++;
			}
		}
		this.hauptkategorien = new int[anz];
		anz = 0;
		for (int i = 0; i < this.kategorien.size(); i++) {
			if (this.kategorien.get(i).isHauptkategorie()) {
				this.hauptkategorien[anz++] = i;
			}
		}
	}

	public boolean isHochrechnen() {
		return this.hochrechnen;
	}

	public void setHochrechnen(final boolean hochrechnen) {
		this.hochrechnen = hochrechnen;
	}

	public String getTextHochrechnen() {
		if (!this.hochrechnen) {
			return res.getString("extrapolation_used");
		}
		final int anzahlGesamttage = this.zeitraum.getAnzahlTage();
		final AbstractZeitraum istZeitraum = new FreierZeitraum(this.zeitraum.getStartDatum(), new Datum());
		return res.getString("extrapolation_unused") + " (" +
				istZeitraum.getAnzahlTage() + " --> " +
				anzahlGesamttage + ")";
	}
}
