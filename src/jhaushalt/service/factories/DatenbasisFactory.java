package jhaushalt.service.factories;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.Datenbasis;
import jhaushalt.domain.Register;
import jhaushalt.service.factories.io.DataInputFacade;
import jhaushalt.service.factories.io.DataOutputFacade;

public class DatenbasisFactory {
	
	private RegisterFactory registerFactory;
	
	public void setRegisterFactory(RegisterFactory registerFactory) {
		this.registerFactory = registerFactory;
	}
	
	public Datenbasis getInstance(DataInputFacade input) throws IOException, UnknownBuchungTypeException, ParseException {
		Datenbasis datenbasis = new Datenbasis();
		
		datenbasis.setVersionInfo(input.getDataString());
		datenbasis.setRegisterList(loadRegisters(input));
		
		// FIXME aggregate category tree
		return datenbasis;
	}
	
	public void saveData(DataOutputFacade output, Datenbasis datenbasis) throws IOException {
		output.writeString(datenbasis.getVersionInfo()); // "jHaushalt" + VERSION_DATENBASIS) !!!
		List<Register> registersList = datenbasis.getRegisterList();
		output.writeInt(registersList.size());
	}
	
	private void saveRegistersList(DataOutputFacade output, List<Register> registerList) {
		for (Register register: registerList) {
			// do register factory
		}
	}
	
	private List<Register> loadRegisters(DataInputFacade inputFacade) throws IOException, UnknownBuchungTypeException, ParseException {
		int numberOfRegisters = inputFacade.getInt();
		List<Register> registerList = new ArrayList<Register>();
		for (int i = 0; i < numberOfRegisters; i++) {
			registerList.add(registerFactory.getInstance(inputFacade, inputFacade.getDataString()));
		}
		return registerList;
	}

}
