package jhaushalt.service.factories.buchung;

import static org.fest.assertions.Assertions.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.Geldbetrag;
import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.SplitBuchung;
import jhaushalt.service.factories.DataSourceArrayHolder;
import jhaushalt.service.factories.DataSourceHolder;

import org.junit.Test;


public class SplitBuchungStrategyIntegrationTest {
	private static final String ANY_SPLIT_BOOKING_TEXT = "Split-Buchungs-Text";
	private SplitBuchungStrategy strategy = new SplitBuchungStrategy();
	
	@Test
	public void validEntryShouldBeLoadedProperly() throws IOException, ParseException {
		List<String> inputArray = createInputArray();
		inputArray.add("2");
		addSingleBookingEntry(inputArray, "Category #1", 20000);
		addSingleBookingEntry(inputArray, "Category #2", 30000);
		DataSourceHolder in = new DataSourceArrayHolder(inputArray);
		Buchung actualBooking = strategy.loadData(in);
		assertThat(actualBooking).isInstanceOf(SplitBuchung.class);
		assertThat(actualBooking.getText()).isEqualTo(ANY_SPLIT_BOOKING_TEXT);
		assertThat(actualBooking.getWert()).isEqualTo(new Geldbetrag(50000D));
	}

	private void addSingleBookingEntry(List<String> inputArray, String category, long sum) { 
		inputArray.add(category);
		inputArray.add(Long.toString(sum));
	}
	
	private List<String> createInputArray() {
		List<String> input = new ArrayList<String>();
		input.add("24.12.2004");
		input.add(ANY_SPLIT_BOOKING_TEXT);
		return input;
	}
}
