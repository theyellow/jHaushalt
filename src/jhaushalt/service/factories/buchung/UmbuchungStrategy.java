package jhaushalt.service.factories.buchung;

import haushalt.daten.Datenbasis;
import haushalt.daten.Register;
import haushalt.daten.UmbuchungKategorie;

import java.io.DataInputStream;
import java.io.IOException;

import jhaushalt.domain.buchung.Buchung;

public class UmbuchungStrategy implements BuchungStrategy {

	public Buchung loadData(DataInputStream in) {
//		final Umbuchung umbuchung = new Umbuchung();
//		umbuchung.laden(in, db, this);
		return null;
	}
	
//	public void laden(final DataInputStream in, final Datenbasis db, final Register zielRegister) throws IOException {
//		getDatum().laden(in);
//		setText(in.readUTF());
//		final String quellRegister = in.readUTF();
//		if (zielRegister != null) {
//			setKategorie(new UmbuchungKategorie(
//					db.findeOderErzeugeRegister(quellRegister),
//					zielRegister));
//		}
//		this.wert.laden(in);
//		if (DEBUG) {
//			LOGGER.info("Umbuchung: " + getText() + " / " + this.kategorie.getQuelle() + " geladen.");
//		}
//	}
}
