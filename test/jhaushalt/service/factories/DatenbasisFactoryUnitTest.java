package jhaushalt.service.factories;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import jhaushalt.domain.Datenbasis;
import jhaushalt.domain.Register;
import jhaushalt.service.factories.io.DataInputFacade;

import org.junit.Before;
import org.junit.Test;

public class DatenbasisFactoryUnitTest {

	private static final String ANY_STRING_VALUE = "any String";
	private static final String ANY_REGISTER_NAME = "Any Register Name";
	private DatenbasisFactory datenbasisFactory;
	private RegisterFactory registerFactory;
	private DataInputFacade dataInputFacade;
	
	@Before
	public void setUp() {
		datenbasisFactory = new DatenbasisFactory();
		registerFactory = mock(RegisterFactory.class);
		datenbasisFactory.setRegisterFactory(registerFactory);
		dataInputFacade = mock(DataInputFacade.class);
	}
	
	@Test
	public void getInstanceCallsFacadeCorrectly() throws IOException, UnknownBuchungTypeException, ParseException {
		when(dataInputFacade.getDataString()).thenReturn(ANY_STRING_VALUE);
		when(dataInputFacade.getInt()).thenReturn(33);
		
		datenbasisFactory.getInstance(dataInputFacade);
		
		verify(dataInputFacade, times(34)).getDataString(); // 1x version; 33x register name
		verify(dataInputFacade).getInt();
	}
	
	@Test
	public void getInstanceProducesThreeRegisterEntriesWithSameName() throws IOException, UnknownBuchungTypeException, ParseException {
		when(dataInputFacade.getDataString()).thenReturn(ANY_STRING_VALUE);
		when(dataInputFacade.getInt()).thenReturn(3);
		when(registerFactory.getInstance(any(DataInputFacade.class), anyString())).thenReturn(new Register(ANY_STRING_VALUE));
		
		datenbasisFactory.getInstance(dataInputFacade);
		
		verify(registerFactory, times(3)).getInstance(dataInputFacade, ANY_STRING_VALUE);
	}
	
	
	@Test
	public void getInstanceCallsRegisterFactorySeveralTimes() throws IOException, UnknownBuchungTypeException, ParseException {
		when(dataInputFacade.getDataString()).thenReturn(ANY_REGISTER_NAME);
		when(dataInputFacade.getInt()).thenReturn(3);
		when(registerFactory.getInstance(any(DataInputFacade.class), anyString())).thenReturn(new Register(ANY_REGISTER_NAME));
		Datenbasis actualDatenbasis = datenbasisFactory.getInstance(dataInputFacade);
		
		List<Register> registers = actualDatenbasis.getRegisterList();
		assertThat(registers).hasSize(3);
		assertThat(registers.get(0).getName()).isEqualTo(ANY_REGISTER_NAME);
		assertThat(registers.get(1).getName()).isEqualTo(ANY_REGISTER_NAME);
		assertThat(registers.get(2).getName()).isEqualTo(ANY_REGISTER_NAME);
		verify(registerFactory, times(3)).getInstance(dataInputFacade, ANY_REGISTER_NAME);
	}
	
}
