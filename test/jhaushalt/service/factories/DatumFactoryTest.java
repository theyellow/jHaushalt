package jhaushalt.service.factories;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.zeitraum.Datum;

import org.junit.Test;


public class DatumFactoryTest {

	@Test
	public void testThatDateIsReadOut() throws IOException, ParseException {
		List<String> input = new ArrayList<String>();
		input.add("27.10.2010");
		DataSourceArrayHolder holder = new DataSourceArrayHolder(input);
		
		Datum actualDate = DatumFactory.getInstance(holder);
		
		assertThat(actualDate.getJahr()).isEqualTo(2010);
		assertThat(actualDate.getMonat()).isEqualTo(10);
		assertThat(actualDate.getTag()).isEqualTo(27);
	}
	
}
