package jhaushalt.service.factories;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.Geldbetrag;

import org.junit.Test;

public class GeldbetragFactoryUnitTest {

	@Test
	public void longValueIsReturnedInGeldbetragObject() throws IOException {
		DataSourceArrayHolder holder = new DataSourceArrayHolder(createArray(2000L));
		Geldbetrag betrag = GeldbetragFactory.getInstance(holder);

		assertThat(betrag.getBetrag()).isEqualTo(200000L);
	}

	@Test(expected=IOException.class)
	public void noInputValueReturnsIOException() throws IOException {
		DataSourceArrayHolder holder = new DataSourceArrayHolder(new ArrayList<String>());
		GeldbetragFactory.getInstance(holder);
	}
	
	
	private List<String> createArray(Long amount) {
		List<String> result = new ArrayList<String>();
		result.add(amount.toString());
		return result;
	}
}
