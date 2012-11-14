package haushalt.service.daten;

import static org.mockito.Mockito.*;
import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

import haushalt.daten.Datenbasis;
import haushalt.daten.ExtendedDatabase;
import haushalt.service.data.DatabaseFileLoader;
import haushalt.service.data.DatabaseService;
import haushalt.service.data.DatabaseServiceException;
import haushalt.service.data.DatabaseServiceImpl;

public class DatabaseServiceTest {

	private DatabaseService databaseService;
	private DatabaseFileLoader dbFileLoader;
	
	@Before
	public void setUp() {
		dbFileLoader = mock(DatabaseFileLoader.class);
		DatabaseServiceImpl databaseService = new DatabaseServiceImpl();
		databaseService.setDatabaseFileLoader(dbFileLoader);
		this.databaseService = databaseService;
	}
	
	@Test
	public void loadDatabaseUsesDatabaseFileLoader() throws FileNotFoundException, DatabaseServiceException {
		ExtendedDatabase extendedDatabase = createExtendedDatabase();
		when(dbFileLoader.loadDbFile(any(File.class))).thenReturn(extendedDatabase);
		File dbFile = new File("foobar.txt");
		ExtendedDatabase actualExtendedDatabase = databaseService.loadDatabase(dbFile);
		
		assertThat(actualExtendedDatabase).isEqualTo(extendedDatabase);
		verify(dbFileLoader).loadDbFile(dbFile);
	}

	@Test
	public void saveDatabaseUsesDatabaseFileLoader() throws FileNotFoundException, DatabaseServiceException {
		Datenbasis database = new Datenbasis();

		databaseService.saveDbFile(database);
		
		verify(dbFileLoader).saveDbFile(database);
	}

	private ExtendedDatabase createExtendedDatabase() {
		ExtendedDatabase extendedDatabase = new ExtendedDatabase(new Datenbasis(), "Any version Id");
		return extendedDatabase;
	}
	
}
