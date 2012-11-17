package jhaushalt.service.factories.buchung;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.buchung.Buchung;

public interface BuchungStrategy {
	
	public Buchung loadData(DataInputStream in) throws IOException, ParseException;

}
