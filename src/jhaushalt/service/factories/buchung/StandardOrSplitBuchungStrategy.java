package jhaushalt.service.factories.buchung;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.Geldbetrag;
import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.SplitBuchung;
import jhaushalt.domain.buchung.StandardBuchung;
import jhaushalt.domain.kategorie.EinzelKategorie;
import jhaushalt.domain.zeitraum.Datum;
import jhaushalt.service.factories.CategoryFactory;
import jhaushalt.service.factories.DatumFactory;
import jhaushalt.service.factories.GeldbetragFactory;

public class StandardOrSplitBuchungStrategy implements BuchungStrategy {

	public Buchung loadData(DataInputStream in) throws IOException, ParseException {
		Buchung buchung = null;
		Datum datum  = DatumFactory.getInstance(in);
		String text = in.readUTF();
		int numberOfEntries = in.readInt();
		if (numberOfEntries > 1) {
			SplitBuchung splitBuchung = new SplitBuchung(datum, text);
			for (int j = 0; j < numberOfEntries; j++) {
				final String kategorie = in.readUTF();
				GeldbetragFactory.getInstance(in); // where to put it in?
//				buchung.add(db.findeOderErzeugeKategorie(kategorie), betrag);
			}
//			einsortierenBuchung(buchung);
//			db.buchungMerken(buchung);
			
		} else if (numberOfEntries == 1) {
			final EinzelKategorie kategorie = (EinzelKategorie) CategoryFactory.getInstance(in);
			Geldbetrag betrag = GeldbetragFactory.getInstance(in); // where to put it in?
			StandardBuchung standardBuchung = new StandardBuchung(datum, text, kategorie, betrag);
//			einsortierenBuchung(standardBuchung);
//			db.buchungMerken(standardBuchung);
		} else {
			// TODO nothing here?
		}
		return buchung;
	}

}
