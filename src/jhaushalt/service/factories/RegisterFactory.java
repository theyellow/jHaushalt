package jhaushalt.service.factories;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.Register;
import jhaushalt.domain.buchung.Buchung;
import jhaushalt.service.factories.io.DataInputFacade;

public class RegisterFactory {
	
	public static Register getInstance (DataInputFacade in, String registerName) throws IOException, UnknownBuchungTypeException, ParseException {
		Register register = new Register(registerName);
		List<Buchung> bookingList = loadBuchungen(in);
		register.insertBookingList(bookingList);
		return register;
	}

	private static List<Buchung> loadBuchungen(final DataInputFacade in) throws IOException, UnknownBuchungTypeException, ParseException {
		final int numberOfBuchungen = in.getInt();
		List<Buchung> buchungen = new ArrayList<Buchung>();
		for (int i = 0; i < numberOfBuchungen; i++) {
			buchungen.add(BuchungFactory.getInstance(in));
		}
		return buchungen;
	}

}
