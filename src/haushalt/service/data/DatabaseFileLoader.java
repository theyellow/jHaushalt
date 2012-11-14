package haushalt.service.data;

import haushalt.daten.Datenbasis;
import haushalt.daten.ExtendedDatabase;

import java.io.File;
import java.io.FileNotFoundException;

public interface DatabaseFileLoader {
	
	ExtendedDatabase loadDbFile(final File datei) throws FileNotFoundException, DatabaseServiceException;
	void saveDbFile(Datenbasis database) throws FileNotFoundException, DatabaseServiceException;
}