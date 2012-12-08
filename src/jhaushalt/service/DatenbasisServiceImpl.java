package jhaushalt.service;

import haushalt.daten.AbstractBuchung;
import haushalt.daten.Register;
import haushalt.daten.UmbuchungKategorie;

import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.Datenbasis;
import jhaushalt.service.factories.DatenbasisFactory;
import jhaushalt.service.factories.UnknownBuchungTypeException;
import jhaushalt.service.factories.io.DataInputFacade;
import jhaushalt.service.factories.io.DataOutputFacade;

public class DatenbasisServiceImpl implements DatenbasisService {

	private Datenbasis datenbasis = new Datenbasis();
	private DatenbasisFactory datenbasisFactory;
	
	
	public void setDatenbasisFactory(DatenbasisFactory datenbasisFactory) {
		this.datenbasisFactory = datenbasisFactory;
	}
	
	public synchronized Datenbasis getDatenbasis() {
		return datenbasis;	
	}

	public synchronized void loadDatabase(DataInputFacade holder) throws CouldNotLoadDatabaseException {
		try {
			datenbasis = datenbasisFactory.getInstance(holder);
		} catch (IOException e) {
			throw new CouldNotLoadDatabaseException("Unexpected file operation problem:" + e.getMessage());
		} catch (UnknownBuchungTypeException e) {
			throw new CouldNotLoadDatabaseException("Unexpected Booking Type:" + e.getMessage());
		} catch (ParseException e) {
			throw new CouldNotLoadDatabaseException("Unexpected parsing problem:" + e.getMessage());
		}
	}

	public synchronized void saveDatabase(DataOutputFacade holder) {
		// TODO Auto-generated method stub
//		// 1. Versionsinfo:
//		out.writeUTF("jHaushalt" + VERSION_DATENBASIS);
//
//		// 2. Buchungen (Kategorien werden NICHT gespeichert)
//		out.writeInt(this.registerListe.size());
//		for (int i = 0; i < this.registerListe.size(); i++) {
//			final Register register = this.registerListe.get(i);
//			register.speichern(out);
//		}
//
//		// 3. automatische Buchungen
//		out.writeInt(this.autoStandardBuchungen.size() + this.autoUmbuchungen.size());
//
//		// 3a. automatische Standard-Buchungen
//		for (int i = 0; i < this.autoStandardBuchungen.size(); i++) {
//			final AbstractBuchung buchung = this.autoStandardBuchungen.get(i);
//			buchung.speichern(out);
//			out.writeUTF("" + this.autoStandardBuchungRegister.get(i));
//			out.writeUTF(LEGACY_INTERVALL_NAMEN[this.autoStandardBuchungIntervalle.get(i)]);
//		}
//
//		// 3b. automatische Umbuchungen
//		for (int i = 0; i < this.autoUmbuchungen.size(); i++) {
//			final AbstractBuchung buchung = this.autoUmbuchungen.get(i);
//			buchung.speichern(out);
//			final UmbuchungKategorie registerPaar = this.autoUmbuchungRegister.get(i);
//			out.writeUTF("" + registerPaar.getQuelle());
//			out.writeUTF("" + registerPaar.getZiel());
//			out.writeUTF(LEGACY_INTERVALL_NAMEN[this.autoUmbuchungIntervalle.get(i)]);
//		}
//
//		out.flush();
//		this.geaendert = false;

	}

}
