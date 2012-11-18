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

public class DatabaseFileLoaderImpl implements DatabaseFileLoader {
	
	
	public ExtendedDatabase loadDbFile(final File datei) throws FileNotFoundException, DatabaseServiceException {
		final FileInputStream fis = new FileInputStream(datei);
		final Datenbasis db = new Datenbasis();
		String versionInfo = Datenbasis.VERSION_DATENBASIS;
		final DataInputStream in = new DataInputStream(fis);
		
		try {
			versionInfo = in.readUTF();
			db.laden(in, versionInfo);
		} catch (final IOException e) {
			throw new DatabaseServiceException("Couldn't load file");
		} finally {
			try {
				fis.close();
			} catch (final IOException e) {
				throw new DatabaseServiceException("Couldn't close file handler");
			}				
		}
		db.setFileName(datei.getAbsolutePath());
		return new ExtendedDatabase(db, versionInfo);
	}

	public void saveDbFile(final Datenbasis database) throws FileNotFoundException, DatabaseServiceException {
		final File fileToSave = new File(database.getFilename());
		final FileOutputStream fos = new FileOutputStream(fileToSave);
		final DataOutputStream out = new DataOutputStream(fos);
		try {
			database.speichern(out);
			out.flush();
		} catch (final IOException e) {
			throw new DatabaseServiceException("Could not save file "+fileToSave.getName());
		} finally {
			try {
				fos.close();
			} catch (final IOException e) {
				throw new DatabaseServiceException("Could not save and close file "+fileToSave.getName());
			}			
		}		
	}
}
