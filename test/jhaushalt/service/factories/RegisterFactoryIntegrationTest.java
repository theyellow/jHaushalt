package jhaushalt.service.factories;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.Register;
import jhaushalt.domain.buchung.Buchung;
import jhaushalt.service.factories.io.DataInputFacade;

import org.junit.Before;
import org.junit.Test;


public class RegisterFactoryIntegrationTest {

	private static final String ANY_BOOKING_TEXT = "Foo Text";
	private static final String ANY_REGISTER_NAME = "anyRegisterName";

	private RegisterFactory registerFactory;
	
	@Before
	public void setUp() {
		registerFactory = new RegisterFactory();
		registerFactory.setBookingFactory(new BuchungFactory());
	}
	
	@Test
	public void getInstanceLoadsOneRegister() throws IOException, UnknownBuchungTypeException, ParseException {
		DataInputFacade in = new DataSourceArrayHolder(createInputDataArray());
		Register actualRegister = registerFactory.getInstance(in, ANY_REGISTER_NAME);
		
		assertThat(actualRegister.getName()).isEqualTo(ANY_REGISTER_NAME);
		List<Buchung> bookings = actualRegister.getBookings();
		assertThat(bookings).hasSize(1);
		assertThat(bookings.get(0).getText()).isEqualTo(ANY_BOOKING_TEXT);
	}

	private List<String> createInputDataArray() {
		List<String> inputParameters = new ArrayList<String>();
		inputParameters.add("1");
		inputParameters.add("StandardBuchung2");
		inputParameters.add("02.01.2012");
		inputParameters.add(ANY_BOOKING_TEXT);
		inputParameters.add("Any Input Category");
		inputParameters.add("2000");		
		return inputParameters;
	}
	
}
