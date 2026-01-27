package model.reservation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Periode {
    private final Date debut; // inclus
    private final Date fin;   // exclus

    public Periode(Date debut, Date fin) {
        if (debut == null || fin == null) throw new IllegalArgumentException("Dates nulles");
        if (!fin.after(debut)) throw new IllegalArgumentException("La fin doit être après le début");
        this.debut = stripTime(debut);
        this.fin = stripTime(fin);
    }

    public Date getDebut() { return debut; }
    public Date getFin() { return fin; }

    public long nbNuits() {
        LocalDate d = toLocalDate(debut);
        LocalDate f = toLocalDate(fin);
        return ChronoUnit.DAYS.between(d, f);
    }

    public boolean couvre(Periode autre) {
        return !this.debut.after(autre.debut) && !this.fin.before(autre.fin);
    }

    public boolean chevauche(Periode autre) {
        // [a,b) chevauche [c,d) si a < d et c < b
        return this.debut.before(autre.fin) && autre.debut.before(this.fin);
    }

    public boolean toucheOuChevauche(Periode autre) {
        return chevauche(autre) || this.fin.equals(autre.debut) || autre.fin.equals(this.debut);
    }

    public Periode fusion(Periode autre) {
        Date nd = this.debut.before(autre.debut) ? this.debut : autre.debut;
        Date nf = this.fin.after(autre.fin) ? this.fin : autre.fin;
        return new Periode(nd, nf);
    }

    public boolean contient(Date date) {
        if (date == null) return false;
        Date d = stripTime(date);
        return (!d.before(debut)) && d.before(fin);
    }

    @Override
    public String toString() {
        return "[" + debut + " -> " + fin + ")";
    }

    // --- Helpers ---
    private static LocalDate toLocalDate(Date d) {
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static Date stripTime(Date d) {
        LocalDate ld = toLocalDate(d);
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
