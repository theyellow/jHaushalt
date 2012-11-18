package jhaushalt.service.factories;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.Register;
import jhaushalt.domain.buchung.Buchung;

public class RegisterFactory {
	
	public static Register getInstance (DataInputStream in, String registerName) throws IOException, UnknownBuchungTypeException, ParseException {
		Register register = new Register(registerName);
		loadBuchungen(in);
		// FIXME how to handle these buchungen(entries)?
		return register;
	}

	private static List<Buchung> loadBuchungen(final DataInputStream in) throws IOException, UnknownBuchungTypeException, ParseException {
		final int numberOfBuchungen = in.readInt();
		List<Buchung> buchungen = new ArrayList<Buchung>();
		for (int i = 0; i < numberOfBuchungen; i++) {
			buchungen.add(BuchungFactory.getInstance(in));
		}
		return buchungen;
	}

}
