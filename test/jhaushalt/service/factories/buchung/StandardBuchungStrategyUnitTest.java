package jhaushalt.service.factories.buchung;

import static org.fest.assertions.Assertions.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.buchung.Buchung;
import jhaushalt.service.factories.DataSourceArrayHolder;

import org.junit.Test;


public class StandardBuchungStrategyUnitTest {

	private StandardBuchungStrategy standardBuchungStrategy = new StandardBuchungStrategy();
	
	@Test
	public void loadDataReturnsStandardBuchung() throws IOException, ParseException {
		DataSourceArrayHolder holder = new DataSourceArrayHolder(createArray());
		Buchung actualBuchung = standardBuchungStrategy.loadData(holder);
		
		assertThat(actualBuchung).isNotNull();
		
	}
	
	private List<String> createArray() throws UnsupportedEncodingException {
		List<String> result = new ArrayList<String>();
		result.add("27.10.2010");
		result.add("Testtext");
		result.add("Sonstiges");
		result.add("30000");
		return result;
	}
	
}
