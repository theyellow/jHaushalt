package jhaushalt.service.factories.buchung;

import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.buchung.Buchung;
import jhaushalt.service.factories.io.DataInputFacade;

public interface BuchungStrategy {
	
	public Buchung loadData(DataInputFacade in) throws IOException, ParseException;

}
