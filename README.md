# 📘 Projet Nephro – Documentation Technique

## 1. Présentation Générale

Nephro est un système distribué d’aide à la décision médicale basé sur une architecture microservices.

Ce système a pour objectif de transformer des données médicales brutes en informations exploitables afin d’assister les professionnels de santé dans leurs décisions cliniques.

Le projet intègre plusieurs fonctionnalités essentielles telles que :

* L’analyse des résultats biologiques
* La gestion des prescriptions médicales
* La validation inter-services
* Une assistance intelligente basée sur l’IA
* Un calendrier clinique pour le suivi des décisions

---

## 2. Architecture du Système

L’architecture du système repose sur une organisation en microservices, garantissant modularité, scalabilité et maintenabilité.

### Composants principaux :

* **Frontend** : Application Angular (interface utilisateur)
* **API Gateway** : Point d’entrée unique des requêtes (Spring Cloud Gateway)
* **Eureka Server** : Service de découverte
* **Microservices** :

  * lab-results-service
  * prescriptions-service

### Principe fondamental :

Le frontend ne communique jamais directement avec les microservices. Toutes les requêtes passent obligatoirement par l’API Gateway.

---

## 3. Description des Microservices

### 3.1 Lab Results Service

Responsabilités :

* Gestion des rapports biologiques
* Gestion des résultats biologiques
* Analyse des anomalies et de leur gravité
* Génération d’événements pour le calendrier
* Fourniture de statistiques

### 3.2 Prescriptions Service

Responsabilités :

* Gestion des prescriptions médicales
* Gestion des médicaments
* Processus de signature
* Mécanisme de verrouillage
* Validation croisée avec les résultats biologiques
* Génération d’explications via IA

---

## 4. Bases de Données

Chaque microservice possède sa propre base de données :

* **lab_results_db**

  * biological_report
  * biological_result

* **prescriptions_db**

  * prescription
  * prescribed_medication

---

## 5. Fonctionnalité Clé : Système de Calendrier

Le calendrier représente les décisions cliniques dans le temps et constitue un élément central du système.

Chaque rapport génère deux événements :

### 1. Événement de rapport

* Date : date du rapport
* Type : REPORT

### 2. Événement de suivi

Déterminé selon le niveau de gravité :

* NORMAL → +30 jours
* FOLLOW_UP → +7 jours
* URGENT → +2 jours

Ce mécanisme permet :

* Le suivi des patients
* L’identification des cas urgents
* La planification des actions médicales

---

## 6. Implémentation Backend

* DTO utilisé : `CalendarEventDTO`
* Service principal : `BiologicalReportService`
* Méthode : `getCalendarEvents()`

### Endpoint :

```http
GET /lab-reports/calendar
```

---

## 7. Implémentation Frontend

Localisation :

```
src/app/pages/calendar/
```

Fonctionnalités :

* Affichage sous forme de grille mensuelle
* Navigation entre les mois
* Code couleur des événements :

  * Bleu : REPORT
  * Vert : NORMAL
  * Jaune : FOLLOW_UP
  * Rouge : URGENT (avec animation)

Interaction :

* Clic sur un événement → affichage du détail du rapport

---

## 8. Validation Inter-Services

Flux de validation :

Prescription → API Gateway → Lab Results Service → récupération du dernier rapport

Résultat retourné :

* OK
* WARNING
* ERROR

---

## 9. Système d’Intelligence Artificielle

Le système utilise Ollama en local avec le modèle **gemma3**.

### Utilisation :

* Explication des prescriptions
* Assistance clinique

### Endpoint :

```http
GET /prescriptions/{id}/ai-explanation
```

---

## 10. Principes UX

Le système est conçu selon les principes suivants :

* Visibilité des informations
* Priorisation clinique
* Aide à la décision

Le calendrier joue un rôle central en permettant :

* Le suivi des patients
* La mise en évidence des cas critiques
* La planification des actions

---

## 11. Limitations Actuelles

* Absence de regroupement d’événements
* Pas de prévisualisation au survol
* Chargement de tous les événements sans pagination
* Absence de filtrage par patient

---

## 12. Améliorations Futures

* Filtrage intelligent du calendrier
* Prévisualisation des événements
* Regroupement des événements
* Prédiction des risques via IA
* Tableau de bord analytique

---

## 13. Endpoints de Test

### Calendrier :

```http
GET /lab-reports/calendar
```

### IA :

```http
GET /prescriptions/{id}/ai-explanation
```

### Validation :

```http
GET /prescriptions/{id}/validation
```

---

## 14. Conclusion

Le système Nephro ne se limite pas à une simple application CRUD.

Il établit une chaîne complète :
**Données → Analyse → Décision → Action**

Ce qui en fait un véritable prototype de système d’aide à la décision médicale.
