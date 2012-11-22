package jhaushalt.service.factories.buchung;

import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.buchung.Buchung;
import jhaushalt.service.factories.DataSourceHolder;

public interface BuchungStrategy {
	
	public Buchung loadData(DataSourceHolder in) throws IOException, ParseException;

}
