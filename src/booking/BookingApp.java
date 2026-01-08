package booking;

import booking.model.*;
import booking.users.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class BookingApp extends JFrame {

    private final CollectionHebergements col = new CollectionHebergements();

    // ✅ On crée un NouveauClient avec le constructeur QUI EXISTE chez toi.
    // Si ton NouveauClient a 3 paramètres (nom, prenom, email), c'est OK.
    private final NouveauClient client = new NouveauClient("Dupont", "Lina", "lina@mail.com");

    private final DefaultListModel<Hebergement> listModel = new DefaultListModel<>();
    private final JList<Hebergement> list = new JList<>(listModel);
    private final JTextArea details = new JTextArea();

    private final JTextField tfDebut = new JTextField("2026-01-12");
    private final JTextField tfFin = new JTextField("2026-01-15");
    private final JTextField tfNb = new JTextField("2");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"ID", "Statut", "Début", "Fin", "Prix"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

    private final JTable table = new JTable(tableModel);

    public BookingApp() {
        super("Mini-Booking (Swing - version compatible)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        seedData();
        buildUI();
        refreshHebergements();
        refreshReservations();

        if (!listModel.isEmpty()) list.setSelectedIndex(0);
    }

    // ------------------ Données de test ------------------
    private void seedData() {
        ChambreHotel h1 = new ChambreHotel(1, "Hotel Paris Centre", "10 rue de Paris", 2, 120, "Chambre cosy", 3);
        h1.ajouterEquipement("WiFi");
        h1.ajouterEquipement("TV");
        h1.ajouterPeriodeDisponible(LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 10));
        h1.ajouterNote(4);
        h1.ajouterNote(5);

        Appartement h2 = new Appartement(2, "Appart Lyon", "2 place Bellecour", 4, 90, "Appartement pratique", 2);
        h2.ajouterEquipement("Cuisine");
        h2.ajouterPeriodeDisponible(LocalDate.of(2026, 1, 5), LocalDate.of(2026, 2, 20));
        h2.ajouterNote(3);

        Villa h3 = new Villa(3, "Villa Nice", "1 avenue de la Mer", 6, 200, "Vue mer", true);
        h3.ajouterEquipement("Piscine");
        h3.ajouterPeriodeDisponible(LocalDate.of(2026, 1, 15), LocalDate.of(2026, 3, 1));
        h3.ajouterNote(5);

        col.ajouter(h1);
        col.ajouter(h2);
        col.ajouter(h3);
    }

    // ------------------ UI ------------------
    private void buildUI() {
        setLayout(new BorderLayout(10, 10));

        // LEFT
        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Hébergements"));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        left.add(new JScrollPane(list), BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(380, 0));
        add(left, BorderLayout.WEST);

        // CENTER
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBorder(BorderFactory.createTitledBorder("Détails"));

        details.setEditable(false);
        details.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        center.add(new JScrollPane(details), BorderLayout.CENTER);

        JPanel reserve = new JPanel(new GridLayout(2, 4, 6, 6));
        reserve.setBorder(BorderFactory.createTitledBorder("Réserver (YYYY-MM-DD)"));

        JButton btnDispo = new JButton("Vérifier dispo");
        JButton btnReserver = new JButton("Réserver");

        reserve.add(new JLabel("Début"));
        reserve.add(new JLabel("Fin"));
        reserve.add(new JLabel("Nb personnes"));
        reserve.add(new JLabel(""));
        reserve.add(tfDebut);
        reserve.add(tfFin);
        reserve.add(tfNb);
        reserve.add(btnReserver);

        JPanel reserveWrap = new JPanel(new BorderLayout());
        reserveWrap.add(reserve, BorderLayout.CENTER);
        reserveWrap.add(btnDispo, BorderLayout.EAST);

        center.add(reserveWrap, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        // BOTTOM
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createTitledBorder("Mes réservations"));

        bottom.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton btnAnnuler = new JButton("Annuler la réservation sélectionnée");
        bottom.add(btnAnnuler, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);

        // EVENTS
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showDetails();
        });

        btnDispo.addActionListener(e -> verifierDispo());
        btnReserver.addActionListener(e -> reserver());
        btnAnnuler.addActionListener(e -> annulerReservation());
    }

    // ------------------ Actions ------------------
    private void refreshHebergements() {
        listModel.clear();
        for (Hebergement h : col.lister()) listModel.addElement(h);
    }

    private void showDetails() {
        Hebergement h = list.getSelectedValue();
        if (h == null) {
            details.setText("");
            return;
        }

        // ✅ Comme detailsTexte() n'existe pas chez toi, on affiche simple
        String txt =
                "ID: " + h.getId() + "\n" +
                "Nom: " + h.getNom() + "\n" +
                "Type: " + h.getType() + "\n" +
                "Adresse: " + h.getAdresse() + "\n" +
                "Capacité: " + h.getCapacite() + "\n" +
                "Prix/nuit: " + h.getPrixParNuit() + "€\n" +
                "Note moyenne: " + String.format("%.2f", h.getMoyenneNotes()) + "/5\n";

        details.setText(txt);
    }

    private void verifierDispo() {
        Hebergement h = list.getSelectedValue();
        if (h == null) return;

        try {
            LocalDate d = LocalDate.parse(tfDebut.getText().trim());
            LocalDate f = LocalDate.parse(tfFin.getText().trim());
            boolean ok = h.estDisponible(d, f);
            JOptionPane.showMessageDialog(this, ok ? "✅ Disponible" : "❌ Indisponible");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private void reserver() {
        Hebergement h = list.getSelectedValue();
        if (h == null) return;

        try {
            LocalDate d = LocalDate.parse(tfDebut.getText().trim());
            LocalDate f = LocalDate.parse(tfFin.getText().trim());
            int nb = Integer.parseInt(tfNb.getText().trim());

            // ✅ Chez toi, reserver() retourne void -> donc on ne récupère rien
            client.reserver(h, d, f, nb);

            JOptionPane.showMessageDialog(this, "✅ Réservation effectuée");
            refreshReservations();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private void refreshReservations() {
        tableModel.setRowCount(0);
        for (Reservation r : client.getReservations()) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getStatut(),
                    r.getDebut(),
                    r.getFin(),
                    String.format("%.2f", r.getPrixTotal())
            });
        }
    }

    private void annulerReservation() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne une réservation");
            return;
        }

        // on lit l'id (optionnel, mais on garde ta logique d'annulation existante)
        int id = (int) tableModel.getValueAt(row, 0);

        // on appelle ta méthode existante: annulerReservation(Client, LocalDate)
        // (elle annule la première réservation future du client)
        Hebergement h = list.getSelectedValue();
        if (h == null) {
            JOptionPane.showMessageDialog(this, "Sélectionne aussi un hébergement dans la liste à gauche.");
            return;
        }

        try {
            h.annulerReservation(client, LocalDate.now());
            JOptionPane.showMessageDialog(this, "✅ Annulation demandée (id=" + id + ")");
            refreshReservations();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    // ------------------ MAIN ------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingApp().setVisible(true));
    }
}
