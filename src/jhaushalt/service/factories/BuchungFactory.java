package jhaushalt.service.factories;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.buchung.Buchung;

public class BuchungFactory {

	private Buchung buchung;
	
	public BuchungFactory(DataInputStream in) throws IOException, UnknownBuchungTypeException, ParseException {
		final String typ = in.readUTF();
		BuchungType buchungType = 
				BuchungType.getBuchungTpeByFileRepresentation(typ);
		buchung = buchungType.getBuchungStrategy().loadData(in);
	}
			
	public Buchung getBuchung() {
		return buchung;
	}
		
}
