package jhaushalt.service.factories.buchung;

import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.StandardBuchung;
import jhaushalt.service.factories.CategoryFactory;
import jhaushalt.service.factories.DatumFactory;
import jhaushalt.service.factories.GeldbetragFactory;
import jhaushalt.service.factories.io.DataInputFacade;

public class StandardBuchungStrategy implements BuchungStrategy {
	
	public Buchung loadData(DataInputFacade in) throws IOException, ParseException {
		StandardBuchung standardBuchung = new StandardBuchung();
		standardBuchung.setDatum(DatumFactory.getInstance(in));
		standardBuchung.setText(in.getDataString());
		standardBuchung.setKategorie(CategoryFactory.getInstance(in));
		standardBuchung.setWert(GeldbetragFactory.getInstance(in));
		// for EinzelKategorie: do I have to set the summary as well?
		return standardBuchung;
	}
}
