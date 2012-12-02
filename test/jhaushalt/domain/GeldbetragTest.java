package jhaushalt.domain;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;


public class GeldbetragTest {

	@Test
	public void defaultGeldBetrag() {
		Geldbetrag betrag = new Geldbetrag();
		assertThat(betrag.getBetrag()).isEqualTo(0L);
		assertThat(betrag.toDouble()).isEqualTo(0D);
	}
	
	@Test
	public void setGeldBetragWithDoubleReturnsCorrectValues() {
		Geldbetrag betrag = new Geldbetrag(1234.56D);
		
		assertThat(betrag.getBetrag()).isEqualTo(123456L);
		assertThat(betrag.toDouble()).isEqualTo(1234.56D);
	}
	
	@Test
	public void positiveAmountWithDecimalsAsStringReturnsCorrectValue() {
		Geldbetrag betrag = new Geldbetrag("1234,56");
		
		assertThat(betrag.getBetrag()).isEqualTo(123456L);
	}
	
	@Test
	public void positiveAmountWithoutDecimalsAsStringReturnsCorrectValue() {
		Geldbetrag betrag = new Geldbetrag("1234");
		
		assertThat(betrag.getBetrag()).isEqualTo(123400L);
	}
	
	@Test
	public void negativeAmountWithDecimalsAsStringReturnsCorrectValue() {
		Geldbetrag betrag = new Geldbetrag("-1234,56");
		
		assertThat(betrag.getBetrag()).isEqualTo(-123456L);
	}
	
	@Test
	public void negativeAmountWithoutDecimalsAsStringReturnsCorrectValue() {
		Geldbetrag betrag = new Geldbetrag("-1234");
		
		assertThat(betrag.getBetrag()).isEqualTo(-123400L);
	}
	
	@Test
	public void emptyStringReturnsZeroAmountValue() {
		Geldbetrag betrag = new Geldbetrag("");
		
		assertThat(betrag.getBetrag()).isEqualTo(0L);
	}

	@Test
	public void anyCharactersinStringReturnsZeroAmountValue() {
		Geldbetrag betrag = new Geldbetrag("foobar");
		
		assertThat(betrag.getBetrag()).isEqualTo(0L);
	}

	@Test
	public void dotsInNumberAreIgnored() {
		Geldbetrag betrag = new Geldbetrag("123.45678.90");
		
		assertThat(betrag.getBetrag()).isEqualTo(123456789000L);
	}

	
}
