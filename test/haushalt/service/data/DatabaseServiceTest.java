package haushalt.service.data;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.fest.assertions.Assertions.assertThat;

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
		final DatabaseServiceImpl databaseServiceImpl = new DatabaseServiceImpl();
		databaseServiceImpl.setDatabaseFileLoader(dbFileLoader);
		this.databaseService = databaseServiceImpl;
	}
	
	@Test
	public void loadDatabaseUsesDatabaseFileLoader() throws FileNotFoundException, DatabaseServiceException {
		final ExtendedDatabase extendedDatabase = createExtendedDatabase();
		when(dbFileLoader.loadDbFile(any(File.class))).thenReturn(extendedDatabase);
		final File dbFile = new File("foobar.txt");
		final ExtendedDatabase actualExtendedDatabase = databaseService.loadDatabase(dbFile);
		
		assertThat(actualExtendedDatabase).isEqualTo(extendedDatabase);
		verify(dbFileLoader).loadDbFile(dbFile);
	}

	@Test
	public void saveDatabaseUsesDatabaseFileLoader() throws FileNotFoundException, DatabaseServiceException {
		final Datenbasis database = new Datenbasis();

		databaseService.saveDbFile(database);
		
		verify(dbFileLoader).saveDbFile(database);
	}

	private ExtendedDatabase createExtendedDatabase() {
		final ExtendedDatabase extendedDatabase = new ExtendedDatabase(new Datenbasis(), "Any version Id");
		return extendedDatabase;
	}
	
}
