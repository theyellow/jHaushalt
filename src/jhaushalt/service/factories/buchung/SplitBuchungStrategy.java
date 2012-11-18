package jhaushalt.service.factories.buchung;

import java.io.DataInputStream;

import jhaushalt.domain.buchung.Buchung;

public class SplitBuchungStrategy implements BuchungStrategy {

	public Buchung loadData(DataInputStream in) {
//		final SplitBuchung splitBuchung = new SplitBuchung();
//		splitBuchung.laden(in, db);
//		einsortierenBuchung(splitBuchung);
//		db.buchungMerken(splitBuchung);
		return null;
	}

	
//	final SplitBuchung buchung = new SplitBuchung(datum, text);
//	for (int j = 0; j < anz; j++) {
//		final String kategorie = in.readUTF();
//		final Euro betrag = new Euro();
//		betrag.laden(in);
//		buchung.add(db.findeOderErzeugeKategorie(kategorie), betrag);
//	}
//	einsortierenBuchung(buchung);
//	db.buchungMerken(buchung);
}
