package jhaushalt.service.factories.buchung;


import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.Umbuchung;
import jhaushalt.service.factories.DatumFactory;
import jhaushalt.service.factories.GeldbetragFactory;

public class UmbuchungStrategy implements BuchungStrategy {

	public Buchung loadData(DataInputStream in) throws IOException, ParseException {
		final Umbuchung umbuchung = new Umbuchung();
		laden(in, umbuchung);
		return umbuchung;
	}
	
	private static void laden(final DataInputStream in, final Umbuchung umbuchung) throws IOException, ParseException {
		umbuchung.setDatum(DatumFactory.getInstance(in));
		umbuchung.setText(in.readUTF());
		in.readUTF(); // first time final String quellRegister = 
		umbuchung.setWert(GeldbetragFactory.getInstance(in));
		
		// FIXME this re-accounting mst be done later, but I have to read this information now... uuuah		
//		final Register quellRegister = findeOderErzeugeRegister(in.readUTF());
//		final Register zielRegister = findeOderErzeugeRegister(in.readUTF());
		in.readUTF(); // quellRegister second time !!
		in.readUTF(); // zielRegister
//		final Integer zeitraum = getLegacyIntervallIndex(in.readUTF());
		in.readUTF(); // zeitraum
	}
	
//	private static void doUmbuchung(DataInputStream in) throws IOException {
//		final Umbuchung buchung = new Umbuchung();
//		buchung.laden(in, this, null);
//		// Register laden:
//		final Register quellRegister = findeOderErzeugeRegister(in.readUTF());
//		final Register zielRegister = findeOderErzeugeRegister(in.readUTF());

		//		final UmbuchungKategorie registerPaar = new UmbuchungKategorie(quellRegister, zielRegister);
//		// Intervall laden:
//		final Integer zeitraum = getLegacyIntervallIndex(in.readUTF());
//
//		// Umbuchung einsortieren
//		final int anz = this.autoUmbuchungen.size();
//		int pos = -1;
//		for (int j = 0; j < anz; j++) {
//			if (buchung.compareTo(this.autoUmbuchungen.get(j)) >= 0) {
//				pos = j;
//			}
//		}
//		if (pos == anz - 1) { // ans Ende
//			this.autoUmbuchungen.add(buchung);
//			this.autoUmbuchungRegister.add(registerPaar);
//			this.autoUmbuchungIntervalle.add(zeitraum);
//		} else { // neue Buchung einfuegen
//			this.autoUmbuchungen.add(pos + 1, buchung);
//			this.autoUmbuchungRegister.add(pos + 1, registerPaar);
//			this.autoUmbuchungIntervalle.add(pos + 1, zeitraum);
//		}
//	}

}
