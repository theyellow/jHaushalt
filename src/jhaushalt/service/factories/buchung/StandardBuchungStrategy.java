package jhaushalt.service.factories.buchung;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.StandardBuchung;
import jhaushalt.service.factories.CategoryFactory;
import jhaushalt.service.factories.DatumFactory;
import jhaushalt.service.factories.GeldbetragFactory;

public class StandardBuchungStrategy implements BuchungStrategy {
	
	public Buchung loadData(DataInputStream in) throws IOException, ParseException {
		StandardBuchung standardBuchung = new StandardBuchung();
		standardBuchung.setDatum(DatumFactory.getInstance(in));
		standardBuchung.setText(in.readUTF());
		standardBuchung.setKategorie(CategoryFactory.getInstance(in));
		standardBuchung.setWert(GeldbetragFactory.getInstance(in));
		return standardBuchung;
	}
}
