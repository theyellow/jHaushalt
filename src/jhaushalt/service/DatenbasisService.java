package jhaushalt.service;

import java.io.FileNotFoundException;

import jhaushalt.domain.Datenbasis;
import jhaushalt.service.factories.io.DataInputFacade;
import jhaushalt.service.factories.io.DataOutputFacade;

public interface DatenbasisService {
	
	Datenbasis getDatenbasis();
	
	void loadDatabase(DataInputFacade holder) throws FileNotFoundException, CouldNotLoadDatabaseException;
	
	void saveDatabase(DataOutputFacade holder);
	
}