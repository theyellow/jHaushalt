package jhaushalt.service.factories;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.Datenbasis;
import jhaushalt.domain.Register;

public class DatenbasisFactory {
	public Datenbasis getInstance(DataSourceHolder input) throws IOException, UnknownBuchungTypeException, ParseException {
		Datenbasis datenbasis = new Datenbasis();
		
		datenbasis.setVersionInfo(getStringDataFromFile(input));
		datenbasis.setRegisterList(loadRegisters(input));
		
		// FIXME aggregate category tree
		return datenbasis;
	}
		
	private static List<Register> loadRegisters(DataSourceHolder in) throws IOException, UnknownBuchungTypeException, ParseException {
		int numberOfRegisters = in.getInt();
		List<Register> registerList = new ArrayList<Register>();
		for (int i = 0; i < numberOfRegisters; i++) {
			String registerName = createUniqueRegisterName(registerList, getStringDataFromFile(in));
			registerList.add(RegisterFactory.getInstance(in, registerName));
		}
		return registerList;
	}

	private static String createUniqueRegisterName(List<Register> registerList, final String regname) {
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
	
	private static String getStringDataFromFile(DataSourceHolder in) throws IOException {
		return in.getDataString();
	}
	
}
