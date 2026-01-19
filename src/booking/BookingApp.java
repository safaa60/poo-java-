package booking;

import booking.model.*;
import booking.users.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class BookingApp extends JFrame {

    private CollectionHebergements col = new CollectionHebergements();
    private Client client = new NouveauClient("Lina", "Dupont", "lina@mail.com", "1234");

    private JList<Hebergement> listHebergements;
    private DefaultListModel<Hebergement> listModel;

    private JTextField tfDebut;
    private JTextField tfFin;
    private JSpinner spNb;

    private JTable table;
    private DefaultTableModel tableModel;

    public BookingApp() {
        setTitle("Mini-Booking (Swing - version compatible)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        seedData();
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));

        // ====== HAUT ======
        JPanel top = new JPanel(new BorderLayout(10, 10));

        // Liste hébergements
        listModel = new DefaultListModel<>();
        for (Hebergement h : col.lister()) listModel.addElement(h);

        listHebergements = new JList<>(listModel);
        top.add(new JScrollPane(listHebergements), BorderLayout.CENTER);

        // Réserver
        JPanel reservePanel = new JPanel(new GridLayout(2, 5, 5, 5));
        reservePanel.setBorder(BorderFactory.createTitledBorder("Réserver (YYYY-MM-DD)"));

        tfDebut = new JTextField("2026-01-12");
        tfFin = new JTextField("2026-01-15");

        spNb = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));

        JButton btnCheck = new JButton("Vérifier dispo");
        JButton btnReserve = new JButton("Réserver");

        reservePanel.add(new JLabel("Début"));
        reservePanel.add(new JLabel("Fin"));
        reservePanel.add(new JLabel("Nb personnes"));
        reservePanel.add(new JLabel(""));
        reservePanel.add(new JLabel(""));

        reservePanel.add(tfDebut);
        reservePanel.add(tfFin);
        reservePanel.add(spNb);
        reservePanel.add(btnReserve);
        reservePanel.add(btnCheck);

        top.add(reservePanel, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        // ====== TABLE ======
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Statut", "Début", "Fin", "Prix"}, 0
        );
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnCancel = new JButton("Annuler la réservation sélectionnée");
        add(btnCancel, BorderLayout.SOUTH);

        // ====== EVENTS ======
        btnReserve.addActionListener(e -> reserver());
        btnCheck.addActionListener(e -> verifier());
        btnCancel.addActionListener(e -> annuler());
    }

    // =========================
    // PLUS DE CHOIX + CAPACITÉS
    // =========================
    private void seedData() {

        // Hotels
        ChambreHotel h1 = new ChambreHotel(1, "Hotel Paris Centre", "Paris", 2, 120, "Cosy", 3);
        ChambreHotel h2 = new ChambreHotel(2, "Hotel Marseille Port", "Marseille", 4, 90, "Familial", 2);
        ChambreHotel h3 = new ChambreHotel(3, "Hotel Lyon Part-Dieu", "Lyon", 6, 140, "Grandes chambres", 4);

        // Appartements
        Appartement a1 = new Appartement(4, "Appart Lyon", "Bellecour", 4, 80, "Centre ville", 2);
        Appartement a2 = new Appartement(5, "Appart Paris 15e", "Paris", 6, 110, "Spacieux", 3);

        // Villas
        Villa v1 = new Villa(6, "Villa Nice", "Nice", 8, 200, "Vue mer", true);
        Villa v2 = new Villa(7, "Villa Bordeaux", "Bordeaux", 10, 180, "Grande villa", false);

        // périodes
        LocalDate d1 = LocalDate.of(2026, 1, 1);
        LocalDate d2 = LocalDate.of(2026, 12, 31);

        for (Hebergement h : new Hebergement[]{h1,h2,h3,a1,a2,v1,v2}) {
            h.ajouterPeriodeDisponible(d1, d2);
            col.ajouter(h);
        }
    }

    private void reserver() {
        try {
            Hebergement h = listHebergements.getSelectedValue();
            if (h == null) {
                JOptionPane.showMessageDialog(this, "Sélectionne un hébergement");
                return;
            }

            LocalDate debut = LocalDate.parse(tfDebut.getText());
            LocalDate fin = LocalDate.parse(tfFin.getText());
            int nb = (int) spNb.getValue();

            client.reserver(h, debut, fin, nb);

            refreshTable();
            JOptionPane.showMessageDialog(this, "Réservation OK");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage());
        }
    }

    private void verifier() {
        try {
            Hebergement h = listHebergements.getSelectedValue();
            LocalDate debut = LocalDate.parse(tfDebut.getText());
            LocalDate fin = LocalDate.parse(tfFin.getText());

            boolean ok = h.estDisponible(debut, fin);
            JOptionPane.showMessageDialog(this, ok ? "Disponible" : "Indisponible");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur format date");
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Reservation r : client.getReservations()) {
            tableModel.addRow(new Object[]{
                    r.getId(), r.getStatut(), r.getDebut(), r.getFin(), r.getPrixTotal()
            });
        }
    }

    private void annuler() {
        try {
            int row = table.getSelectedRow();
            if (row == -1) return;

            Reservation r = client.getReservations().get(row);
            r.annuler();

            refreshTable();
        } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingApp().setVisible(true));
    }
}
