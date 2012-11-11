package haushalt.service.data;

import haushalt.daten.Datenbasis;
import haushalt.daten.ExtendedDatabase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DatabaseService {

	public ExtendedDatabase loadDatabase(File dbFile) throws FileNotFoundException, DatabaseServiceException {
		ExtendedDatabase db = loadDbFile(dbFile);
		return db;
	}
		
	private ExtendedDatabase loadDbFile(final File datei) throws FileNotFoundException, DatabaseServiceException {
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
