package model.hebergements;

import java.util.Date;

import model.personnes.Client;

public interface Reservable {
    boolean estDisponible(Date debut, Date fin);
    double calculerPrix(Date debut, Date fin, int nbPersonnes);

    void reserver(Client c, Date debut, Date fin);
    void annulerReservation(Client c, Date date);

    void afficherDetails();
    boolean estReservee(Date date);

    // getters propriétés principales
    long getId();
    String getType();
    int getCapaciteMax();
    double getPrixParNuit();
    double getNoteMoyenne();
}
