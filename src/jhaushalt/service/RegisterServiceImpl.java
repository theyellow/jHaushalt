package jhaushalt.service;

import jhaushalt.domain.Register;
import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.Umbuchung;
import jhaushalt.domain.kategorie.UmbuchungKategorie;


public class RegisterServiceImpl {
	/**
	 * Fügt die Buchungen aus einem anderen Register diesem
	 * Register hinzu und löscht sie dann.
	 * 
	 * @param registerZumLoeschen
	 *            Register aus dem die Buchungen übernommen werden
	 */
	public void registerVereinigen(String registerName, final Register register1, final Register registerZumLoeschen) {
//		while (registerZumLoeschen.getAnzahlBuchungen() > 0) {
//			Buchung currentBuchung = registerZumLoeschen.getBuchung(0);
//			if (currentBuchung.getClass() == Umbuchung.class) {
//				final Umbuchung umbuchung = (Umbuchung) currentBuchung;
//				final UmbuchungKategorie alteKategorie = (UmbuchungKategorie) umbuchung.getKategorie();
//
//				if (alteKategorie.isSelbstbuchung()) {
//					// Umbuchung: alte Selbstbuchung
//					umbuchung.setKategorie(new UmbuchungKategorie(register1, register1));
//					// -> automatisch löschen und neu einfügen
//				} else {
//					// normale Umbuchung
//					Register neueQuelle;
//					Register neuesZiel;
//					if (alteKategorie.getQuelle() == registerZumLoeschen) {
//						neueQuelle = register1;
//					} else {
//						neueQuelle = alteKategorie.getQuelle();
//					}
//					if (alteKategorie.getZiel() == registerZumLoeschen) {
//						neuesZiel = register1;
//					} else {
//						neuesZiel = alteKategorie.getZiel();
//					}
//					if (neueQuelle != neuesZiel) {
//						umbuchung.setKategorie(new UmbuchungKategorie(neueQuelle, neuesZiel));
//					} else {
//						// sonst loeschen:
//						neueQuelle.loescheUmbuchung(umbuchung);
//						registerZumLoeschen.getBookings().remove(0);
//					}
//				}
//			} else {
//				// StandardBuchung + SplitBuchung
//				register1.einsortierenBuchung(registerZumLoeschen.getBuchung(0));
//				registerZumLoeschen.getBookings().remove(0);
//			}
//		}
	}

	public String[][] csvExport(Register register) {
//		final int anzahl = register.getAnzahlBuchungen();
		int anzahl = 1;
		final String[][] text = new String[anzahl][3];
//		for (int i = 0; i < anzahl; i++) {
//			final Buchung buchung = register.getBuchung(i);
//			text[i][0] = "" + buchung.getDatum();
//			text[i][1] = buchung.getText();
//			text[i][2] = "" + buchung.getKategorie();
//			text[i][3] = "" + buchung.getWert();
//		}
		return text;
	}

}
