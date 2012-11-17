package jhaushalt.service.factories;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.Register;
import jhaushalt.domain.buchung.Buchung;

public class RegisterFactory {
	
	private Register register;
	private ArrayList<Buchung> buchungen = new ArrayList<Buchung> (); 
	
	public RegisterFactory(DataInputStream in, String registerName) throws IOException, UnknownBuchungTypeException {
		register = new Register(registerName);
		loadBuchungen(in);
	}
	
	public Register getRegister() {
		return register;
	}

	private void loadBuchungen(final DataInputStream in) throws IOException, UnknownBuchungTypeException {
		final int numberOfBuchungen = in.readInt();
		this.buchungen.ensureCapacity(numberOfBuchungen);
		for (int i = 0; i < numberOfBuchungen; i++) {
			BuchungFactory buchungFactory = new BuchungFactory(in);
			buchungFactory.getBuchung(); // FIXME what to do with returned buchung
		}
	}

}
