package jhaushalt.service.factories.buchung;

import java.io.DataInputStream;

import jhaushalt.domain.buchung.Buchung;

public class StandardOrSplitBuchungStrategy implements BuchungStrategy {

	public Buchung loadData(DataInputStream in) {
//		final Datum datum = new Datum();
//		datum.laden(in);
//		final String text = in.readUTF();
//		final int anz = in.readInt();
//		if (anz == 1) {
//			final EinzelKategorie kategorie = db.findeOderErzeugeKategorie(in.readUTF());
//			final Euro betrag = new Euro();
//			betrag.laden(in);
//			final StandardBuchung standardBuchung = new StandardBuchung(datum, text, kategorie, betrag);
//			einsortierenBuchung(standardBuchung);
//			db.buchungMerken(standardBuchung);
//		} else {
//			final SplitBuchung buchung = new SplitBuchung(datum, text);
//			for (int j = 0; j < anz; j++) {
//				final String kategorie = in.readUTF();
//				final Euro betrag = new Euro();
//				betrag.laden(in);
//				buchung.add(db.findeOderErzeugeKategorie(kategorie), betrag);
//			}
//			einsortierenBuchung(buchung);
//			db.buchungMerken(buchung);
//		}
		return null;
	}

}
