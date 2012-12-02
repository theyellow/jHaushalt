package jhaushalt.domain.buchung;

import static org.fest.assertions.Assertions.*;

import jhaushalt.domain.kategorie.Kategorie;
import jhaushalt.domain.kategorie.MehrfachKategorie;

import org.junit.Test;


public class SplitBuchungUnitTest {

	@Test
	public void splitBuchungWorksWithMehrfachKategorie() {
		Buchung splitBuchung = new SplitBuchung();
		Kategorie actualCategory = splitBuchung.getKategorie();
		
		assertThat(actualCategory).isInstanceOf(MehrfachKategorie.class);
	}

	@Test
	public void emptySplitBuchungReturnsEmptyMehrfachKategorie() {
		Buchung splitBuchung = new SplitBuchung();
		MehrfachKategorie actualCategory = (MehrfachKategorie) splitBuchung.getKategorie();
		
		assertThat(actualCategory.isEmpty()).isTrue();
	}

}
