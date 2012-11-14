package haushalt.service.daten;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import haushalt.service.data.DatabaseService;
import haushalt.service.data.DatabaseServiceException;
import haushalt.service.data.DatabaseServiceImpl;

public class DatabaseServiceTest {

	DatabaseService databaseService;
	
	@Before
	public void setUp() {
		databaseService = new DatabaseServiceImpl();
	}
	
	@Test @Ignore
	public void loadDatabaseUsesDatabaseFileLoader() throws FileNotFoundException, DatabaseServiceException {
		File dbFile = new File("foobar.txt");
		databaseService.loadDatabase(dbFile);
	}
	
}
