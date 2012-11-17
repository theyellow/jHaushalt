package jhaushalt.service.factories;

import jhaushalt.service.factories.buchung.BuchungStrategy;
import jhaushalt.service.factories.buchung.SplitBuchungStrategy;
import jhaushalt.service.factories.buchung.StandardBuchungStrategy;
import jhaushalt.service.factories.buchung.StandardOrSplitBuchungStrategy;
import jhaushalt.service.factories.buchung.UmbuchungStrategy;

public enum BuchungType {
	UMBUCHUNG("Umbuchung", new UmbuchungStrategy()),
	STANDARD_OR_SPLIT_BUCHUNG("Standardbuchung", new StandardOrSplitBuchungStrategy()),
	STANDARD_BUCHUNG("StandardBuchung2", new StandardBuchungStrategy()),
	SPLIT_BUCHUNG("SplitBuchung", new SplitBuchungStrategy());
	
	private String fileRepresentation;
	private BuchungStrategy buchungStrategy;

	private BuchungType(String filePattern, BuchungStrategy buchungStrategy) {
		this.fileRepresentation = filePattern;
	}
	
	public String getFileRepresentation() {
		return fileRepresentation;
	}
	
	public BuchungStrategy getBuchungStrategy() {
		return buchungStrategy;
	}
	
	static BuchungType getBuchungTpeByFileRepresentation(String fileRepresentation) throws UnknownBuchungTypeException {
		for(BuchungType buchungType: BuchungType.values()) {
			if (buchungType.getFileRepresentation().equals(fileRepresentation)) {
				return buchungType;
			}
		}
		throw new UnknownBuchungTypeException("The following name is unknown: " + fileRepresentation);
	}
}
