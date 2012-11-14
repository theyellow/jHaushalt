package haushalt.service.data;

import haushalt.daten.Datenbasis;
import haushalt.daten.ExtendedDatabase;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DatabaseServiceImpl implements DatabaseService {

	private DatabaseFileLoader databaseFileLoader;
	
	public void setDatabaseFileLoader(DatabaseFileLoader databaseFileLoad) {
		this.databaseFileLoader = databaseFileLoad;
	}
	
	public ExtendedDatabase loadDatabase(File dbFile) throws FileNotFoundException, DatabaseServiceException {
		ExtendedDatabase db = databaseFileLoader.loadDbFile(dbFile);
		return db;
	}
		
	public void saveDbFile(File datei, Datenbasis db) throws FileNotFoundException, DatabaseServiceException {
		final FileOutputStream fos = new FileOutputStream(datei);
		final DataOutputStream out = new DataOutputStream(fos);
		try {
			db.speichern(out);
			out.flush();
		} catch (IOException e) {
			throw new DatabaseServiceException("Could not save file "+datei.getName());
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				throw new DatabaseServiceException("Could not save and close file "+datei.getName());
			}			
		}
	}
	
}
