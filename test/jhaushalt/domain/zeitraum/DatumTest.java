package jhaushalt.domain.zeitraum;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;

public class DatumTest {

	@Test
	public void compareToReturnsZeroWhenDatesAreEqual() {
		Datum datum1 = new Datum(22, 1, 2012);
		Datum datum2 = new Datum(22, 1, 2012);

		assertThat(datum1.compareTo(datum2)).isEqualTo(0);
	} 

	@Test
	public void compareToReturnsPositiveIntWhenDatumOneIsLaterThanDatumTwo() {
		Datum datum1 = new Datum(22, 1, 2012);
		Datum datum2 = new Datum(21, 1, 2012);

		assertThat(datum1.compareTo(datum2)).isGreaterThanOrEqualTo(1);
	} 

	@Test
	public void compareToReturnsNegativeIntWhenDatumOneIsEarlierThanDatumTwo() {
		Datum datum1 = new Datum(21, 1, 2012);
		Datum datum2 = new Datum(22, 1, 2012);

		assertThat(datum1.compareTo(datum2)).isLessThanOrEqualTo(-1);
	} 

	@Test
	public void twoDatumElementsAreEqualWhenValuesAreEqual() {
		Datum datum1 = new Datum(22, 1, 2012);
		Datum datum2 = new Datum(22, 1, 2012);
		
		assertThat(datum1).isNotSameAs(datum2);
		assertThat(datum1).isEqualTo(datum2);
	}

	@Test
	public void twoDatumElementsAreNotEqualWithDifferentValues() {
		Datum datum1 = new Datum(20, 1, 2012);
		Datum datum2 = new Datum(22, 1, 2012);
		
		assertThat(datum1).isNotEqualTo(datum2);
	}
	
	
}
