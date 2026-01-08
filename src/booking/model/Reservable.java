package booking.model;

import booking.users.Client;
import java.time.LocalDate;

public interface Reservable {
    boolean estDisponible(LocalDate debut, LocalDate fin);
    double calculerPrix(LocalDate debut, LocalDate fin, int nbPersonnes);
    void reserver(Client c, LocalDate debut, LocalDate fin, int nbPersonnes);
    void annulerReservation(Client c, LocalDate dateAnnulation);
    void afficherDetails();
    boolean estReservee(LocalDate date);

    int getId();
    String getType();
    int getCapacite();
    double getPrixParNuit();
}
