package jhaushalt.domain;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.StandardBuchung;
import jhaushalt.domain.buchung.Umbuchung;

import org.junit.Before;
import org.junit.Test;

public class RegisterUnitTest {

	private static final String ANY_REGISTER_NAME = "fooBar";

	private Register register;

	@Before
	public void setUp() {
		register = new Register(ANY_REGISTER_NAME);
	}

	@Test
	public void NewRegisterHasNameAndEmptyBookingList() {
		assertThat(register.getName()).isEqualTo(ANY_REGISTER_NAME);
		assertThat(register.getBookings()).isEmpty();
	}

	@Test
	public void registerCanAddBookingList() {
		List<Buchung> bookingList = createBookingList();
		
		register.insertBookingList(bookingList);

		assertThat(register.getBookings()).hasSize(2);
		assertThat(register.getBookings()).isEqualTo(bookingList);
	}

	@Test
	public void registerCanAddAnotherBookingEntry() {
		Buchung myBooking = new StandardBuchung();
		List<Buchung> expectedList = new ArrayList<Buchung>();
		expectedList.add(myBooking);
		
		register.addBooking(myBooking);
		
		assertThat(register.getBookings()).isEqualTo(expectedList);
	}

	private List<Buchung> createBookingList() {
		List<Buchung> bookingList = new ArrayList<Buchung>();
		bookingList.add(new StandardBuchung());
		bookingList.add(new Umbuchung());
		return bookingList;
	}

}
