package haushalt.service.data;

import haushalt.daten.Datenbasis;
import haushalt.daten.ExtendedDatabase;

import java.io.File;
import java.io.FileNotFoundException;

public interface DatabaseService {

	ExtendedDatabase loadDatabase(File dbFile) throws FileNotFoundException, DatabaseServiceException;

	void saveDbFile(Datenbasis db) throws FileNotFoundException, DatabaseServiceException;

}
