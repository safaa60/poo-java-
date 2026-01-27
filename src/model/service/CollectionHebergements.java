package model.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.hebergements.Hebergement;

public class CollectionHebergements {

    private final ArrayList<Hebergement> hebergements = new ArrayList<>();

    public void ajouter(Hebergement h) {
        if (h != null) hebergements.add(h);
    }

    public boolean supprimerParId(long id) {
        return hebergements.removeIf(h -> h.getId() == id);
    }

    public Hebergement trouverParId(long id) {
        for (Hebergement h : hebergements) {
            if (h.getId() == id) return h;
        }
        return null;
    }

    public ArrayList<Hebergement> getTous() {
        return hebergements;
    }

    public List<Hebergement> trier() {
        hebergements.sort(null); // Comparable
        return hebergements;
    }

    // recherches demandées (prix, capacité, type, note, disponibilités)
    public List<Hebergement> rechercherParPrixMax(double prixMax) {
        List<Hebergement> res = new ArrayList<>();
        for (Hebergement h : hebergements) if (h.getPrixParNuit() <= prixMax) res.add(h);
        return res;
    }

    public List<Hebergement> rechercherParCapaciteMin(int capMin) {
        List<Hebergement> res = new ArrayList<>();
        for (Hebergement h : hebergements) if (h.getCapaciteMax() >= capMin) res.add(h);
        return res;
    }

    public List<Hebergement> rechercherParType(String type) {
        List<Hebergement> res = new ArrayList<>();
        for (Hebergement h : hebergements) if (h.getType().equalsIgnoreCase(type)) res.add(h);
        return res;
    }

    public List<Hebergement> rechercherParNoteMin(double noteMin) {
        List<Hebergement> res = new ArrayList<>();
        for (Hebergement h : hebergements) if (h.getNoteMoyenne() >= noteMin) res.add(h);
        return res;
    }

    public List<Hebergement> rechercherDisponibles(Date debut, Date fin) {
        List<Hebergement> res = new ArrayList<>();
        for (Hebergement h : hebergements) if (h.estDisponible(debut, fin)) res.add(h);
        return res;
    }

    // recherche combinée (pratique)
    public List<Hebergement> rechercher(Date debut, Date fin,
                                        Integer capaciteMin, Double prixMax,
                                        String type, Double noteMin) {
        List<Hebergement> res = new ArrayList<>();
        for (Hebergement h : hebergements) {
            if (debut != null && fin != null && !h.estDisponible(debut, fin)) continue;
            if (capaciteMin != null && h.getCapaciteMax() < capaciteMin) continue;
            if (prixMax != null && h.getPrixParNuit() > prixMax) continue;
            if (type != null && !h.getType().equalsIgnoreCase(type)) continue;
            if (noteMin != null && h.getNoteMoyenne() < noteMin) continue;
            res.add(h);
        }
        return res;
    }
}
