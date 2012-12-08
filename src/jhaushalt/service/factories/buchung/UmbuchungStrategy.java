package jhaushalt.service.factories.buchung;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.Umbuchung;
import jhaushalt.service.factories.DatumFactory;
import jhaushalt.service.factories.GeldbetragFactory;
import jhaushalt.service.factories.io.DataInputFacade;

public class UmbuchungStrategy implements BuchungStrategy {

	public Buchung loadData(DataInputFacade in) throws IOException, ParseException {
		final Umbuchung umbuchung = new Umbuchung();
		laden(in, umbuchung);
		return umbuchung;
	}
	
	private static void laden(final DataInputFacade in, final Umbuchung umbuchung) throws IOException, ParseException {
		umbuchung.setDatum(DatumFactory.getInstance(in));
		umbuchung.setText(in.getDataString());
		in.getDataString(); // first time final String quellRegister = 
		umbuchung.setWert(GeldbetragFactory.getInstance(in)); // FIXME

		// DAS klappte alles überhaupt nicht... kommentiere das mal aus...
//		// FIXME this re-accounting mst be done later, but I have to read this information now... uuuah		
//		in.readUTF(); // final Register quellRegister = findeOderErzeugeRegister(in.readUTF());
//		in.readUTF(); // final Register zielRegister = findeOderErzeugeRegister(in.readUTF());
//		in.readUTF(); // final Integer zeitraum = getLegacyIntervallIndex(in.readUTF());
//		in.readInt(); // anzahl Auto-Umbuchungen		
	}

}
