package haushalt.service.data;

import haushalt.daten.Datenbasis;
import haushalt.daten.ExtendedDatabase;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DatabaseFileLoaderImpl implements DatabaseFileLoader {
	
	
	public ExtendedDatabase loadDbFile(final File datei) throws FileNotFoundException, DatabaseServiceException {
		FileInputStream fis = new FileInputStream(datei);
		Datenbasis db = new Datenbasis();
		String versionInfo = Datenbasis.VERSION_DATENBASIS;
		final DataInputStream in = new DataInputStream(fis);
		
		try {
			versionInfo = in.readUTF();
			db.laden(in, versionInfo);
		} catch (IOException e) {
			throw new DatabaseServiceException("Couldn't load file");
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				throw new DatabaseServiceException("Couldn't close file handler");
			}				
		}
		return new ExtendedDatabase(db, versionInfo);
	}
}
