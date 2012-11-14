package haushalt.service.data;

import haushalt.daten.ExtendedDatabase;

import java.io.File;
import java.io.FileNotFoundException;

public interface DatabaseFileLoader {
	
	ExtendedDatabase loadDbFile(final File datei) throws FileNotFoundException, DatabaseServiceException;

}