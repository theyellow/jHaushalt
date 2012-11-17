package haushalt.service.data;

import haushalt.daten.Datenbasis;
import haushalt.daten.ExtendedDatabase;
import java.io.File;
import java.io.FileNotFoundException;

public class DatabaseServiceImpl implements DatabaseService {

	private DatabaseFileLoader databaseFileLoader;
	
	public void setDatabaseFileLoader(final DatabaseFileLoader databaseFileLoad) {
		this.databaseFileLoader = databaseFileLoad;
	}
	
	public ExtendedDatabase loadDatabase(final File dbFile) throws FileNotFoundException, DatabaseServiceException {
		final ExtendedDatabase db = databaseFileLoader.loadDbFile(dbFile);
		return db;
	}
		
	public void saveDbFile(final Datenbasis db) throws FileNotFoundException, DatabaseServiceException {
		databaseFileLoader.saveDbFile(db);
	}
	
}
