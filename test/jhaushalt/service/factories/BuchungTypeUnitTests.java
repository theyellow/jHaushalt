package jhaushalt.service.factories;

import static org.fest.assertions.Assertions.assertThat;
import jhaushalt.service.factories.buchung.SplitBuchungStrategy;
import jhaushalt.service.factories.buchung.StandardBuchungStrategy;
import jhaushalt.service.factories.buchung.StandardOrSplitBuchungStrategy;
import jhaushalt.service.factories.buchung.UmbuchungStrategy;

import org.junit.Test;

public class BuchungTypeUnitTests {
	String[] KNOWN_BOOKING_TYPES = { "Umbuchung", "StandardBuchung", "StandardBuchung2", "SplitBuchung" };

	@Test
	public void checkThatUmbuchungIsRecognized() throws UnknownBuchungTypeException {
		BuchungType type = BuchungType.getBuchungTpeByFileRepresentation("Umbuchung");

		assertThat(type).isEqualTo(BuchungType.UMBUCHUNG);
		assertThat(type.getBuchungStrategy()).isInstanceOf(UmbuchungStrategy.class);
	}

	@Test
	public void checkThatStandardBuchungIsRecognized() throws UnknownBuchungTypeException {
		BuchungType type = BuchungType.getBuchungTpeByFileRepresentation("Standardbuchung");

		assertThat(type).isEqualTo(BuchungType.STANDARD_OR_SPLIT_BUCHUNG);
		assertThat(type.getBuchungStrategy()).isInstanceOf(StandardOrSplitBuchungStrategy.class);
	}

	@Test
	public void checkThatSplitBuchungIsRecognized() throws UnknownBuchungTypeException {
		BuchungType type = BuchungType.getBuchungTpeByFileRepresentation("SplitBuchung");

		assertThat(type).isEqualTo(BuchungType.SPLIT_BUCHUNG);
		assertThat(type.getBuchungStrategy()).isInstanceOf(SplitBuchungStrategy.class);
	}

	@Test
	public void checkThatStandardBuchung2IsRecognized() throws UnknownBuchungTypeException {
		BuchungType type = BuchungType.getBuchungTpeByFileRepresentation("StandardBuchung2");

		assertThat(type).isEqualTo(BuchungType.STANDARD_BUCHUNG);
		assertThat(type.getBuchungStrategy()).isInstanceOf(StandardBuchungStrategy.class);
	}


	@Test(expected=UnknownBuchungTypeException.class)
	public void checkThatUnknowRepresentationThrowsException() throws UnknownBuchungTypeException {
		BuchungType.getBuchungTpeByFileRepresentation("FooBuchung");
	}
	
}
