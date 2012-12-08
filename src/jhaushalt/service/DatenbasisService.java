package jhaushalt.service;

import java.io.FileNotFoundException;

import jhaushalt.domain.Datenbasis;
import jhaushalt.service.factories.DataSourceHolder;

public interface DatenbasisService {
	
	Datenbasis getDatenbasis();
	
	void loadDatabase(DataSourceHolder holder) throws FileNotFoundException, CouldNotLoadDatabaseException;
	
}