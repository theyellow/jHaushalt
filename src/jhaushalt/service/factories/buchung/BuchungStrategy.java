package jhaushalt.service.factories.buchung;

import java.io.DataInputStream;

import jhaushalt.domain.buchung.Buchung;

public interface BuchungStrategy {
	
	public Buchung loadData(DataInputStream in);

}
