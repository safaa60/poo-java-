# Mini-Booking – Projet Java POO

## 📌 Description
Mini-Booking est une application Java en mode console inspirée de Booking.com.
Elle permet de gérer des hébergements, des utilisateurs (clients et administrateurs)
et des réservations en appliquant les principes fondamentaux de la programmation orientée objet.


## 🎯 Objectifs pédagogiques
- Appliquer les concepts POO :
  - héritage
  - abstraction
  - interfaces
  - polymorphisme
- Utiliser des collections dynamiques (`ArrayList`)
- Implémenter le tri avec `Comparable`
- Concevoir une architecture claire et évolutive

---

## 🏗️ Architecture du projet

### Utilisateurs
- `Personne` (abstraite)
- `Client`
  - `NouveauClient`
  - `AncienClient`
- `Administrateur`

### Hébergements
- `Hebergement` (implémente `Reservable`, `Comparable`)
- `ChambreHotel`
- `Appartement`
- `Villa`

### Réservations
- `Reservation`
- `Periode`
- `StatutReservation`

### Services
- `CollectionHebergements`

---

## 🔁 Fonctionnalités
- Recherche d’hébergements (prix, type, capacité, note)
- Vérification des disponibilités
- Réservation et annulation
- Calcul du prix avec réduction
- Gestion des hébergements par un administrateur
- Tri des hébergements

---

## ▶️ Exécution
Lancer la classe :

MainBooking

connexion admin :

Email: admin@mail.com
Mot de passe: admin


Elle simule plusieurs scénarios :
1. Inscription et réservation d’un nouveau client
2. Connexion d’un ancien client avec réduction
3. Gestion des hébergements par un administrateur


## 🧪 Technologies
- Java
- Programmation Orientée Objet
- Application console



## 👩‍💻 Auteur
zemmar safaa karadag nissa 
