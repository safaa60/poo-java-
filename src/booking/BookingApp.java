package booking;

import booking.model.*;
import booking.users.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class BookingApp extends JFrame {

    private final CollectionHebergements col = new CollectionHebergements();
    private final NouveauClient client = new NouveauClient("Dupont", "Lina", "lina@mail.com");

    // UI
    private final DefaultListModel<Hebergement> listModel = new DefaultListModel<>();
    private final JList<Hebergement> list = new JList<>(listModel);

    private final JTextArea details = new JTextArea();
    private final JTextArea dispoArea = new JTextArea();

    // filtres
    private final JComboBox<String> cbType = new JComboBox<>(new String[]{"TOUS", "HOTEL", "APPARTEMENT", "VILLA"});
    private final JTextField tfPrixMax = new JTextField("0");
    private final JTextField tfCapMin = new JTextField("0");

    // réservation
    private final JTextField tfDebut = new JTextField("2026-01-12");
    private final JTextField tfFin = new JTextField("2026-01-15");
    private final JTextField tfNb = new JTextField("2");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"ID", "Statut", "Début", "Fin", "Prix"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable table = new JTable(tableModel);

    public BookingApp() {
        super("Mini-Booking (Swing - Design + Dispos + Filtres)");

        // ✅ Look moderne
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
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
        h1.ajouterPeriodeDisponible(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20));
        h1.ajouterNote(4);
        h1.ajouterNote(5);

        ChambreHotel h4 = new ChambreHotel(4, "Hotel Marseille Vieux-Port", "20 quai du Port", 3, 85, "Bon rapport qualité/prix", 2);
        h4.ajouterEquipement("WiFi");
        h4.ajouterPeriodeDisponible(LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 25));
        h4.ajouterNote(4);

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
        col.ajouter(h4);
    }

    // ------------------ UI ------------------
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10,10,10,10));
        setContentPane(root);

        // ========= LEFT : filtres + liste =========
        JPanel left = new JPanel(new BorderLayout(10,10));
        left.setBorder(BorderFactory.createTitledBorder("Recherche & Hébergements"));
        left.setPreferredSize(new Dimension(420, 0));

        JPanel filters = new JPanel(new GridLayout(3,2,8,8));
        filters.add(new JLabel("Type"));
        filters.add(cbType);
        filters.add(new JLabel("Prix max (0 = ignore)"));
        filters.add(tfPrixMax);
        filters.add(new JLabel("Capacité min (0 = ignore)"));
        filters.add(tfCapMin);

        JPanel filterButtons = new JPanel(new GridLayout(1,2,8,8));
        JButton btnSearch = new JButton("Rechercher");
        JButton btnSort = new JButton("Trier par prix");
        filterButtons.add(btnSearch);
        filterButtons.add(btnSort);

        JPanel northLeft = new JPanel(new BorderLayout(8,8));
        northLeft.add(filters, BorderLayout.CENTER);
        northLeft.add(filterButtons, BorderLayout.SOUTH);

        left.add(northLeft, BorderLayout.NORTH);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(12);
        left.add(new JScrollPane(list), BorderLayout.CENTER);

        root.add(left, BorderLayout.WEST);

        // ========= CENTER : détails + dispos =========
        JPanel center = new JPanel(new GridLayout(1,2,10,10));

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Détails"));
        details.setEditable(false);
        details.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        detailsPanel.add(new JScrollPane(details), BorderLayout.CENTER);

        JPanel dispoPanel = new JPanel(new BorderLayout());
        dispoPanel.setBorder(BorderFactory.createTitledBorder("Disponibilités"));
        dispoArea.setEditable(false);
        dispoArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        dispoPanel.add(new JScrollPane(dispoArea), BorderLayout.CENTER);

        center.add(detailsPanel);
        center.add(dispoPanel);

        root.add(center, BorderLayout.CENTER);

        // ========= SOUTH : réservation + réservations =========
        JPanel south = new JPanel(new BorderLayout(10,10));

        JPanel reserve = new JPanel(new GridLayout(2,5,8,8));
        reserve.setBorder(BorderFactory.createTitledBorder("Réserver (YYYY-MM-DD)"));

        JButton btnDispo = new JButton("Vérifier dispo");
        JButton btnReserver = new JButton("Réserver");

        reserve.add(new JLabel("Début"));
        reserve.add(new JLabel("Fin"));
        reserve.add(new JLabel("Nb personnes"));
        reserve.add(new JLabel(""));
        reserve.add(new JLabel(""));

        reserve.add(tfDebut);
        reserve.add(tfFin);
        reserve.add(tfNb);
        reserve.add(btnDispo);
        reserve.add(btnReserver);

        south.add(reserve, BorderLayout.NORTH);

        JPanel bookingPanel = new JPanel(new BorderLayout(6,6));
        bookingPanel.setBorder(BorderFactory.createTitledBorder("Mes réservations"));
        table.setRowHeight(24);
        bookingPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnAnnuler = new JButton("Annuler la réservation sélectionnée");
        bookingPanel.add(btnAnnuler, BorderLayout.SOUTH);

        south.add(bookingPanel, BorderLayout.CENTER);

        root.add(south, BorderLayout.SOUTH);

        // ========= EVENTS =========
        btnSearch.addActionListener(e -> refreshHebergements());
        btnSort.addActionListener(e -> { col.trierParPrix(); refreshHebergements(); });

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showDetailsAndDispos();
            }
        });

        btnDispo.addActionListener(e -> verifierDispo());
        btnReserver.addActionListener(e -> reserver());
        btnAnnuler.addActionListener(e -> annulerReservation());
    }

    // ------------------ Data refresh ------------------
    private void refreshHebergements() {
        listModel.clear();

        String type = (String) cbType.getSelectedItem();
        if ("TOUS".equals(type)) type = ""; // ignore

        double prixMax = parseDouble(tfPrixMax.getText().trim());
        int capMin = parseInt(tfCapMin.getText().trim());

        List<Hebergement> res = col.rechercher(type, prixMax, capMin);
        for (Hebergement h : res) listModel.addElement(h);

        if (!listModel.isEmpty()) list.setSelectedIndex(0);
        showDetailsAndDispos();
    }

    private void showDetailsAndDispos() {
        Hebergement h = list.getSelectedValue();
        if (h == null) {
            details.setText("");
            dispoArea.setText("");
            return;
        }

        // Détails généraux
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(h.getId()).append("\n");
        sb.append("Nom: ").append(h.getNom()).append("\n");
        sb.append("Type: ").append(h.getType()).append("\n");
        sb.append("Adresse: ").append(h.getAdresse()).append("\n");
        sb.append("Capacité: ").append(h.getCapacite()).append("\n");
        sb.append("Prix/nuit: ").append(h.getPrixParNuit()).append("€\n");
        sb.append("Note moyenne: ").append(String.format("%.2f", h.getMoyenneNotes())).append("/5\n");

        // Bonus : infos spécifiques selon sous-classe
        if (h instanceof ChambreHotel ch) {
            sb.append("Étoiles: ").append(ch.getEtoiles()).append("\n");
        } else if (h instanceof Appartement ap) {
            sb.append("Étage: ").append(ap.getEtage()).append("\n");
        } else if (h instanceof Villa v) {
            sb.append("Piscine: ").append(v.isPiscine() ? "Oui" : "Non").append("\n");
        }

        details.setText(sb.toString());

        // Disponibilités
        try {
            StringBuilder sd = new StringBuilder();
            List<Periode> dispos = h.getDisponibilites();
            if (dispos.isEmpty()) {
                sd.append("Aucune période disponible.\n");
            } else {
                sd.append("Périodes disponibles:\n");
                for (Periode p : dispos) {
                    sd.append(" - ").append(p).append("\n");
                }
            }
            dispoArea.setText(sd.toString());
        } catch (Exception ex) {
            dispoArea.setText("Impossible d'afficher les dispos.\nAjoute getDisponibilites() dans Hebergement.");
        }
    }

    // ------------------ Booking actions ------------------
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

            // chez toi: void -> on ne récupère pas un objet
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

        Hebergement h = list.getSelectedValue();
        if (h == null) {
            JOptionPane.showMessageDialog(this, "Sélectionne un hébergement à gauche.");
            return;
        }

        try {
            // ta méthode: annule la réservation future du client
            h.annulerReservation(client, LocalDate.now());
            JOptionPane.showMessageDialog(this, "✅ Annulation demandée");
            refreshReservations();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private static double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0; }
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    // ------------------ MAIN ------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingApp().setVisible(true));
    }
}
