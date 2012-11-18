package jhaushalt.service.factories;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.buchung.Buchung;

public class BuchungFactory {
	
	public static Buchung getInstance(DataInputStream in) throws IOException, UnknownBuchungTypeException, ParseException {
		final String typ = in.readUTF();
		BuchungType buchungType = 
				BuchungType.getBuchungTpeByFileRepresentation(typ);
		return buchungType.getBuchungStrategy().loadData(in);
	}
	
}
