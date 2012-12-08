package jhaushalt.service;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import jhaushalt.domain.Datenbasis;
import jhaushalt.service.factories.DataSourceArrayHolder;
import jhaushalt.service.factories.DatenbasisFactory;
import jhaushalt.service.factories.UnknownBuchungTypeException;
import jhaushalt.service.factories.io.DataInputFacade;

import org.junit.Before;
import org.junit.Test;


public class DatenbasisServiceTest {

	private DatenbasisServiceImpl datenbasisService;
	private DatenbasisFactory datenbasisFactory;
	
	@Before
	public void setUp() {
		datenbasisService = new DatenbasisServiceImpl();
		datenbasisFactory = mock(DatenbasisFactory.class);
		datenbasisService.setDatenbasisFactory(datenbasisFactory);
	}
	
	@Test
	public void initialDatenbasisServiceInstanceDeliversEmptyDatenbasis() {
		Datenbasis datenbasis = datenbasisService.getDatenbasis();	
		assertThat(datenbasis).isNotNull();
	}
	
	@Test
	public void testThatMinimalInputFileReturnsMinimalDatenbasis() throws CouldNotLoadDatabaseException, IOException, UnknownBuchungTypeException, ParseException {
		Datenbasis expectedDatenbasis = new Datenbasis();
		when(datenbasisFactory.getInstance(any(DataInputFacade.class))).thenReturn(expectedDatenbasis);
		DataInputFacade holder = new DataSourceArrayHolder(new ArrayList<String>());
		
		datenbasisService.loadDatabase(holder);
		Datenbasis actualDatenbasis = datenbasisService.getDatenbasis();
		
		assertThat(actualDatenbasis).isEqualTo(expectedDatenbasis);
	}

	@Test(expected=CouldNotLoadDatabaseException.class)
	public void corruptFileShowCauseCouldNotLoadDatabaseException() throws CouldNotLoadDatabaseException, IOException, UnknownBuchungTypeException, ParseException {
		when(datenbasisFactory.getInstance(any(DataInputFacade.class))).thenThrow(new IOException());
		datenbasisService.loadDatabase(new DataSourceArrayHolder(new ArrayList<String>()));
	}
	
	@Test(expected=CouldNotLoadDatabaseException.class)
	public void invalidBookingTypeCausesCouldNotLoadDatabaseException() throws CouldNotLoadDatabaseException, IOException, UnknownBuchungTypeException, ParseException {
		when(datenbasisFactory.getInstance(any(DataInputFacade.class))).thenThrow(new UnknownBuchungTypeException("foo"));
		datenbasisService.loadDatabase(new DataSourceArrayHolder(new ArrayList<String>()));
	}
	
	@Test(expected=CouldNotLoadDatabaseException.class)
	public void invalidNumberCausesCouldNotLoadDatabaseException() throws CouldNotLoadDatabaseException, IOException, UnknownBuchungTypeException, ParseException {
		when(datenbasisFactory.getInstance(any(DataInputFacade.class))).thenThrow(new ParseException(null, 0));
		datenbasisService.loadDatabase(new DataSourceArrayHolder(new ArrayList<String>()));
	}

}
