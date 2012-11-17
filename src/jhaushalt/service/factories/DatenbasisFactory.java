package jhaushalt.service.factories;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import jhaushalt.domain.Datenbasis;
import jhaushalt.domain.Register;

public class DatenbasisFactory {
	private static final Logger LOGGER = Logger.getLogger(DatenbasisFactory.class.getName());
	private static final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN);

	private DataInputStream in;
	private Datenbasis datenbasis;
	
	DatenbasisFactory(DataInputStream input) throws IOException, UnknownBuchungTypeException {
		in = input;
		datenbasis = new Datenbasis();
		
		loadVersionInfo();
		loadRegisters();
	}
	
	
	private void loadRegisters() throws IOException, UnknownBuchungTypeException {
		int numberOfRegisters = in.readInt();
		List<Register> registerList = new ArrayList<Register>();
		for (int i = 0; i < numberOfRegisters; i++) {
			String registerName = createUniqueRegisterName(registerList, getStringDataFromFile());
			RegisterFactory registerFactory = new RegisterFactory(in, registerName);
			registerList.add(registerFactory.getRegister());
		}
		datenbasis.setRegisterList(registerList);
	}

	private String createUniqueRegisterName(List<Register> registerList, final String regname) {
		String generierterName = regname;
		boolean nameVorhanden;
		int count = 0;
		do {
			nameVorhanden = false;
			for (int i = 0; i < registerList.size(); i++) {
				if (generierterName.equalsIgnoreCase("" + registerList.get(i))) {
					nameVorhanden = true;
				}
			}
			if (nameVorhanden) {
				generierterName = regname + " (" + ++count + ")";
			}
		} while (nameVorhanden);
		return generierterName;
	}

	private void loadVersionInfo() throws IOException {
		datenbasis.setVersionInfo(getStringDataFromFile());
	}
	
	private String getStringDataFromFile() throws IOException {
		return in.readUTF();
	}
	
}
