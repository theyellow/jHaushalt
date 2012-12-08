package jhaushalt.service.factories;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.Datenbasis;
import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.StandardBuchung;

public class DatenbasisWithAutomationFactory {

	private DatenbasisFactory datenbasisFactory;
	
	public void setDatenbasisFactory(DatenbasisFactory datenbasisFactory) {
		this.datenbasisFactory = datenbasisFactory;
	}
	
	public Datenbasis getInstance(DataSourceHolder input) throws IOException, UnknownBuchungTypeException, ParseException {
		Datenbasis datenbasis = datenbasisFactory.getInstance(input);
		if (! "jHaushalt1.0".equals(datenbasis.getVersionInfo())) {
			// automatische Buchungen laden und ausführen (in v1.0 noch unbekannt!)
			datenbasis.setAutomatedEntries(loadAutomatedEntries(input));
		}
		return datenbasis;
	}

	private static List<Buchung> loadAutomatedEntries (DataSourceHolder in) throws IOException, UnknownBuchungTypeException, ParseException {
		final int numberOfAutomatedEntries = in.getInt();
		List<Buchung> automatedEntries = new ArrayList<Buchung>();
		
		for (int i = 0; i < numberOfAutomatedEntries; i++) {
			// Buchung laden:
			//final String typ = in.readUTF();
			automatedEntries.add(BuchungFactory.getInstance(in));
//			if (typ.equals("Umbuchung")) {
//				//doUmbuchung(in);
//			} else if (!typ.equals("StandardBuchung") && !typ.equals("StandardBuchung2")) {
//				throw new IOException("AutoBuchung: Falscher Buchungstyp: " + typ);
//			} else {
//				StandardBuchung buchung = doStandardBuchung(in, typ);
		}
		return automatedEntries;
	}


	private static StandardBuchung doStandardBuchung(DataInputStream in,
			final String typ) throws IOException {
		StandardBuchung buchung = null;
//		if (typ.equals("StandardBuchung")) { // Laden des
//												// Legacy-Formats
//			final Datum datum = new Datum();
//			datum.laden(in);
//			final String text = in.readUTF();
//			final int anz = in.readInt();
//			if (anz == 1) {
//				final EinzelKategorie kategorie = findeOderErzeugeKategorie(in.readUTF());
//				final Geldbetrag betrag = new Geldbetrag();
//				betrag.laden(in);
//				buchung = new StandardBuchung(datum, text, kategorie, betrag);
//			} else {
//				throw new IOException("AutoBuchung: Falscher Buchungstyp: SplitBuchung");
//			}
//		} else { // Laden des aktuellen Formats für Standard-Buchungen
//			buchung = new StandardBuchung();
//			(buchung).laden(in, this);
//		}
//
//		// Register laden:
//		final Register register = findeOderErzeugeRegister(in.readUTF());
//		// Intervall laden:
//		final Integer zeitraum = getLegacyIntervallIndex(in.readUTF());
//
//		// Buchung einsortieren
//		final int anz = this.autoStandardBuchungen.size();
//		int pos = -1;
//		for (int j = 0; j < anz; j++) {
//			if (buchung.compareTo(this.autoStandardBuchungen.get(j)) >= 0) {
//				pos = j;
//			}
//		}
//		if (pos == anz - 1) { // ans Ende
//			this.autoStandardBuchungen.add(buchung);
//			this.autoStandardBuchungRegister.add(register);
//			this.autoStandardBuchungIntervalle.add(zeitraum);
//		} else { // neue Buchung einfuegen
//			this.autoStandardBuchungen.add(pos + 1, buchung);
//			this.autoStandardBuchungRegister.add(pos + 1, register);
//			this.autoStandardBuchungIntervalle.add(pos + 1, zeitraum);
//		}
		return buchung;
	}

}
