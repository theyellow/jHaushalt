package haushalt.service.data;

import haushalt.daten.Datenbasis;
import haushalt.daten.ExtendedDatabase;
import java.io.File;
import java.io.FileNotFoundException;

public class DatabaseServiceImpl implements DatabaseService {

	private DatabaseFileLoader databaseFileLoader;
	
	public void setDatabaseFileLoader(DatabaseFileLoader databaseFileLoad) {
		this.databaseFileLoader = databaseFileLoad;
	}
	
	public ExtendedDatabase loadDatabase(File dbFile) throws FileNotFoundException, DatabaseServiceException {
		ExtendedDatabase db = databaseFileLoader.loadDbFile(dbFile);
		return db;
	}
		
	public void saveDbFile(Datenbasis db) throws FileNotFoundException, DatabaseServiceException {
		databaseFileLoader.saveDbFile(db);
	}
	
}
