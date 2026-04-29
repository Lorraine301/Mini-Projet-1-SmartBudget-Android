# SmartBudget - Android

Application Android de gestion de budget personnel, conçue pour aider les étudiants à suivre leurs dépenses quotidiennes et visualiser où part leur argent.

## Fonctionnalités

- Ajout, modification et suppression de dépenses
- Catégorisation des dépenses (Alimentation, Transport, Logement, Santé, Loisirs, Études, Autre)
- Navigation par mois (mois précédent / suivant)
- Total des dépenses du mois et répartition par catégorie
- Statistiques et top catégories
- Fonctionne entièrement hors connexion (offline-first)
- Dépenses récurrentes (loyer, abonnements)

## Stack technique

- **Langage** : Kotlin
- **UI** : Jetpack Compose + Material Design 3
- **Base de données** : Room (SQLite)
- **Architecture** : MVVM + Repository Pattern
- **Navigation** : Jetpack Navigation Compose
- **Icônes** : Material Icons Extended

## Architecture du projet

![Architecture](architecture.png)

## Écrans

| Écran | Description |
|---|---|
| Dépenses | Liste des dépenses du mois avec filtres par catégorie |
| Statistiques | Répartition des dépenses par catégorie et top postes |
| Paramètres | Gestion des catégories et préférences |

## Lancer le projet

1. Cloner le repo
2. Ouvrir dans Android Studio
3. Synchroniser Gradle
4. Lancer sur un émulateur ou appareil Android (API 26+)

## Auteur

Lorraine301
