package jhaushalt.service.factories;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.buchung.Buchung;
import jhaushalt.service.factories.buchung.BuchungStrategy;

public class BuchungFactory {
	
	public static Buchung getInstance(DataInputStream in) throws IOException, UnknownBuchungTypeException, ParseException {
		final String typ = in.readUTF();
		BuchungType buchungType = BuchungType.getBuchungTpeByFileRepresentation(typ);
		System.out.println("BuchungType: " + buchungType);
		BuchungStrategy strategy = buchungType.getBuchungStrategy();
		return strategy.loadData(in);
	}
	
}
