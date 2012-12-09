package jhaushalt.service.factories;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.Register;
import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.SplitBuchung;
import jhaushalt.service.factories.io.DataInputFacade;
import jhaushalt.service.factories.io.DataOutputFacade;

import org.junit.Before;
import org.junit.Test;

public class RegisterFactoryUnitTest {

	private static final String ANY_REGISTER_NAME = "Foo Register";
	private RegisterFactory registerFactory;
	private BuchungFactory bookingFactory;
	
	@Before
	public void setup() {
		bookingFactory = mock(BuchungFactory.class);
		registerFactory = new RegisterFactory();
		registerFactory.setBookingFactory(bookingFactory);
	}
	
	@Test
	public void getInstanceGetsNameAndBookingList() throws IOException, UnknownBuchungTypeException, ParseException {
		DataInputFacade inputFacade = mock(DataInputFacade.class);
		when(inputFacade.getInt()).thenReturn(1);
		List<Buchung> bookingList = createBookingList(); 
		when(bookingFactory.getInstance(inputFacade)).thenReturn(bookingList.get(0));
		Register actualRegister = registerFactory.getInstance(inputFacade, ANY_REGISTER_NAME);
		
		assertThat(actualRegister.getName()).isEqualTo(ANY_REGISTER_NAME);
		assertThat(actualRegister.getAnzahlBuchungen()).isEqualTo(1);
		assertThat(actualRegister.getBookings()).isEqualTo(bookingList);
		
		verify(bookingFactory).getInstance(inputFacade);
	}

	@Test
	public void saveRegister() throws IOException {
		Register register = new Register("fooName");
		List<Buchung> bookingList = createBookingList(); 
		register.insertBookingList(bookingList);
		DataOutputFacade dataOutputFacade = mock(DataOutputFacade.class);
		registerFactory.saveData(dataOutputFacade, register);
		
		verify(dataOutputFacade).writeInt(1);
		verify(bookingFactory).saveData(eq(dataOutputFacade), any(Buchung.class));
	}
	
	private List<Buchung> createBookingList() {
		List<Buchung> bookingList = new ArrayList<Buchung>();
		bookingList.add(new SplitBuchung());
 		return bookingList;
	}
	
}
