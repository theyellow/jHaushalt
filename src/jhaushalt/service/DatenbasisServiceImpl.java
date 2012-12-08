package jhaushalt.service;

import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.Datenbasis;
import jhaushalt.service.factories.DataSourceHolder;
import jhaushalt.service.factories.DatenbasisFactory;
import jhaushalt.service.factories.UnknownBuchungTypeException;

public class DatenbasisServiceImpl implements DatenbasisService {

	private Datenbasis datenbasis = new Datenbasis();
	private DatenbasisFactory datenbasisFactory;
	
	
	public void setDatenbasisFactory(DatenbasisFactory datenbasisFactory) {
		this.datenbasisFactory = datenbasisFactory;
	}
	
	public synchronized Datenbasis getDatenbasis() {
		return datenbasis;	
	}

	public synchronized void loadDatabase(DataSourceHolder holder) throws CouldNotLoadDatabaseException {
		try {
			datenbasis = datenbasisFactory.getInstance(holder);
		} catch (IOException e) {
			throw new CouldNotLoadDatabaseException("Unexpected file operation problem:" + e.getMessage());
		} catch (UnknownBuchungTypeException e) {
			throw new CouldNotLoadDatabaseException("Unexpected Booking Type:" + e.getMessage());
		} catch (ParseException e) {
			throw new CouldNotLoadDatabaseException("Unexpected parsing problem:" + e.getMessage());
		}
	}

}
