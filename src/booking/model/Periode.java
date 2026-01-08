package booking.model;

import java.time.LocalDate;

public class Periode {
    private final LocalDate debut;
    private final LocalDate fin; 

    public Periode(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null || !debut.isBefore(fin)) {
            throw new IllegalArgumentException("Période invalide (debut < fin obligatoire).");
        }
        this.debut = debut;
        this.fin = fin;
    }

    public LocalDate getDebut() { return debut; }
    public LocalDate getFin() { return fin; }

    public boolean contient(LocalDate date) {
        return (date.isEqual(debut) || date.isAfter(debut)) && date.isBefore(fin);
    }

    public boolean chevauche(LocalDate d, LocalDate f) {
        // chevauche si (debut < f) et (fin > d)
        return debut.isBefore(f) && fin.isAfter(d);
    }

    public boolean couvre(LocalDate d, LocalDate f) {
        return (debut.isEqual(d) || debut.isBefore(d)) && (fin.isEqual(f) || fin.isAfter(f));
    }

    @Override
    public String toString() {
        return "[" + debut + " -> " + fin + "]";
    }
}
