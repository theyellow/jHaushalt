package jhaushalt.service.factories;

import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.buchung.Buchung;
import jhaushalt.service.factories.buchung.BuchungStrategy;
import jhaushalt.service.factories.io.DataInputFacade;

public class BuchungFactory {
	
	public static Buchung getInstance(DataInputFacade in) throws IOException, UnknownBuchungTypeException, ParseException {
		final String typ = in.getDataString();
		BuchungType buchungType = BuchungType.getBuchungTpeByFileRepresentation(typ);
		BuchungStrategy strategy = buchungType.getBuchungStrategy();
		return strategy.loadData(in);
	}
	
}
