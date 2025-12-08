# 📋 AquaWatch - Product Backlog Complet

## 🎯 Vision du Projet
**AquaWatch** est une plateforme de surveillance de la qualité de l'eau en temps réel utilisant une architecture microservices, intégrant des données de capteurs IoT et d'imagerie satellite pour prédire et alerter sur la qualité de l'eau.

---

## 👥 Équipe de Développement

| Membre | Rôle Principal | Email |
|--------|----------------|-------|
| **Issame Imad** | Tech Lead / Backend | - |
| **Agoumi Mohammed Amine** | Full Stack Developer | - |
| **Jabrane Mohamed Yahya** | Frontend / DevOps | - |

---

## 🏔️ ÉPIQUES (Epics)

### EPIC 1: Infrastructure & Architecture Microservices
**Description:** Mise en place de l'architecture technique de base avec les microservices
**Priorité:** 🔴 Critique
**Responsable:** Issame Imad

### EPIC 2: Gestion des Stations & Capteurs (Sensor Service)
**Description:** Développement du service de gestion des stations de surveillance et leurs capteurs
**Priorité:** 🔴 Critique
**Responsable:** Issame Imad

### EPIC 3: Intégration Satellite (Satellite Service)
**Description:** Intégration des données Sentinel-1/2 pour l'analyse de la qualité d'eau
**Priorité:** 🟠 Haute
**Responsable:** Agoumi Mohammed Amine

### EPIC 4: Modèle de Prédiction (STModel Service)
**Description:** Service de machine learning pour prédire la qualité de l'eau (WQI)
**Priorité:** 🟠 Haute
**Responsable:** Agoumi Mohammed Amine

### EPIC 5: Système d'Alertes (Alert Service)
**Description:** Gestion des alertes et notifications en temps réel
**Priorité:** 🟠 Haute
**Responsable:** Jabrane Mohamed Yahya

### EPIC 6: Cartographie (Map Service)
**Description:** Service de visualisation géospatiale des stations et données
**Priorité:** 🟡 Moyenne
**Responsable:** Jabrane Mohamed Yahya

### EPIC 7: Interface Utilisateur (Frontend Angular)
**Description:** Développement de l'application web Angular moderne
**Priorité:** 🔴 Critique
**Responsable:** Jabrane Mohamed Yahya

### EPIC 8: API Gateway & Sécurité
**Description:** Passerelle API et authentification/autorisation
**Priorité:** 🟠 Haute
**Responsable:** Issame Imad

---

## 📝 PRODUCT BACKLOG DÉTAILLÉ

---

### 🏔️ EPIC 1: Infrastructure & Architecture Microservices

#### US-1.1: Configuration de l'environnement de développement
**En tant que** développeur  
**Je veux** avoir un environnement de développement configuré  
**Afin de** pouvoir développer et tester les microservices  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-1.1.1 | Installer Java 17+ et configurer JAVA_HOME | 1h | Tous |
| T-1.1.2 | Installer Maven 3.8+ | 30min | Tous |
| T-1.1.3 | Installer Node.js 18+ et npm | 30min | Tous |
| T-1.1.4 | Installer et configurer Docker Desktop | 1h | Tous |
| T-1.1.5 | Configurer IDE (IntelliJ/VS Code) avec extensions | 1h | Tous |
| T-1.1.6 | Cloner le repository et vérifier la structure | 30min | Tous |

**Points:** 3 | **Sprint:** 1

---

#### US-1.2: Structure du projet multi-modules Maven
**En tant que** architecte  
**Je veux** une structure de projet Maven parent-enfant  
**Afin de** gérer les dépendances de manière centralisée  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-1.2.1 | Créer le POM parent avec les versions des dépendances | 2h | Issame |
| T-1.2.2 | Configurer Spring Boot 3.x comme parent | 1h | Issame |
| T-1.2.3 | Définir les modules enfants (sensor, satellite, etc.) | 1h | Issame |
| T-1.2.4 | Configurer les plugins Maven communs | 1h | Issame |
| T-1.2.5 | Documenter la structure du projet | 1h | Issame |

**Points:** 5 | **Sprint:** 1

---

#### US-1.3: Configuration Docker pour tous les services
**En tant que** DevOps  
**Je veux** des Dockerfiles et docker-compose  
**Afin de** déployer facilement l'ensemble des services  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-1.3.1 | Créer Dockerfile pour sensor-service | 1h | Yahya |
| T-1.3.2 | Créer Dockerfile pour satellite-service | 1h | Yahya |
| T-1.3.3 | Créer Dockerfile pour stmodel-service | 1h | Yahya |
| T-1.3.4 | Créer Dockerfile pour alert-service | 1h | Yahya |
| T-1.3.5 | Créer Dockerfile pour map-service | 1h | Yahya |
| T-1.3.6 | Créer Dockerfile pour gateway-service | 1h | Yahya |
| T-1.3.7 | Créer docker-compose.yml complet | 2h | Yahya |
| T-1.3.8 | Configurer les volumes et networks | 1h | Yahya |
| T-1.3.9 | Tester le démarrage de tous les services | 2h | Yahya |

**Points:** 8 | **Sprint:** 1

---

### 🏔️ EPIC 2: Gestion des Stations & Capteurs (Sensor Service)

#### US-2.1: CRUD des Stations de surveillance
**En tant que** administrateur  
**Je veux** pouvoir créer, modifier et supprimer des stations  
**Afin de** gérer le réseau de surveillance  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-2.1.1 | Créer l'entité JPA Station | 1h | Issame |
| T-2.1.2 | Créer le repository StationRepository | 30min | Issame |
| T-2.1.3 | Implémenter StationService avec méthodes CRUD | 2h | Issame |
| T-2.1.4 | Créer StationController (REST API) | 2h | Issame |
| T-2.1.5 | Créer les DTOs (StationDTO, CreateStationRequest) | 1h | Issame |
| T-2.1.6 | Ajouter validation des données (@Valid) | 1h | Issame |
| T-2.1.7 | Écrire les tests unitaires | 2h | Issame |
| T-2.1.8 | Écrire les tests d'intégration | 2h | Issame |
| T-2.1.9 | Documenter l'API avec Swagger/OpenAPI | 1h | Issame |

**Points:** 8 | **Sprint:** 2

---

#### US-2.2: Enregistrement des mesures des capteurs
**En tant que** système IoT  
**Je veux** envoyer les mesures des capteurs  
**Afin de** stocker les données de qualité d'eau  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-2.2.1 | Créer l'entité Measurement (pH, temp, turbidité, etc.) | 1h | Issame |
| T-2.2.2 | Créer MeasurementRepository avec requêtes custom | 1h | Issame |
| T-2.2.3 | Implémenter MeasurementService | 2h | Issame |
| T-2.2.4 | Créer endpoint POST /measurements | 1h | Issame |
| T-2.2.5 | Implémenter la validation des plages de valeurs | 1h | Issame |
| T-2.2.6 | Créer endpoint GET /stations/{id}/measurements | 1h | Issame |
| T-2.2.7 | Ajouter pagination et filtres par date | 2h | Issame |
| T-2.2.8 | Tests unitaires et d'intégration | 2h | Issame |

**Points:** 8 | **Sprint:** 2

---

#### US-2.3: Statistiques et agrégations des mesures
**En tant que** analyste  
**Je veux** voir les statistiques des mesures  
**Afin de** analyser les tendances de qualité  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-2.3.1 | Créer requêtes SQL pour moyennes/min/max | 2h | Issame |
| T-2.3.2 | Implémenter endpoint /stations/{id}/stats | 2h | Issame |
| T-2.3.3 | Ajouter agrégation par période (jour/semaine/mois) | 2h | Issame |
| T-2.3.4 | Créer DTO StatisticsResponse | 1h | Issame |
| T-2.3.5 | Optimiser les requêtes avec index | 1h | Issame |

**Points:** 5 | **Sprint:** 3

---

### 🏔️ EPIC 3: Intégration Satellite (Satellite Service)

#### US-3.1: Configuration API Copernicus/Sentinel Hub
**En tant que** système  
**Je veux** me connecter aux APIs satellite  
**Afin de** récupérer les images Sentinel  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-3.1.1 | Créer compte Copernicus Open Access Hub | 1h | Amine |
| T-3.1.2 | Obtenir les credentials API | 30min | Amine |
| T-3.1.3 | Configurer application.yml avec credentials | 1h | Amine |
| T-3.1.4 | Créer SentinelApiClient avec RestTemplate/WebClient | 3h | Amine |
| T-3.1.5 | Implémenter authentification OAuth2 | 2h | Amine |
| T-3.1.6 | Tester la connexion à l'API | 1h | Amine |

**Points:** 5 | **Sprint:** 2

---

#### US-3.2: Téléchargement et stockage des images satellite
**En tant que** système  
**Je veux** télécharger les images Sentinel-1/2  
**Afin de** les analyser pour la qualité de l'eau  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-3.2.1 | Créer entité SatelliteImage (métadonnées) | 1h | Amine |
| T-3.2.2 | Configurer stockage fichiers (local/S3) | 2h | Amine |
| T-3.2.3 | Implémenter service de téléchargement async | 3h | Amine |
| T-3.2.4 | Créer job planifié pour téléchargement (@Scheduled) | 2h | Amine |
| T-3.2.5 | Gérer les erreurs et retry | 2h | Amine |
| T-3.2.6 | Créer endpoint pour déclencher téléchargement manuel | 1h | Amine |

**Points:** 8 | **Sprint:** 3

---

#### US-3.3: Traitement des images pour extraction de données
**En tant que** système  
**Je veux** extraire les indicateurs de qualité des images  
**Afin de** compléter les données des capteurs  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-3.3.1 | Intégrer bibliothèque de traitement d'image (GDAL/GeoTools) | 3h | Amine |
| T-3.3.2 | Implémenter calcul NDWI (Normalized Difference Water Index) | 4h | Amine |
| T-3.3.3 | Implémenter détection de turbidité par imagerie | 4h | Amine |
| T-3.3.4 | Créer service de traitement asynchrone | 2h | Amine |
| T-3.3.5 | Stocker les résultats d'analyse | 2h | Amine |
| T-3.3.6 | Créer API pour récupérer les analyses | 2h | Amine |

**Points:** 13 | **Sprint:** 4

---

### 🏔️ EPIC 4: Modèle de Prédiction (STModel Service)

#### US-4.1: Calcul du Water Quality Index (WQI)
**En tant que** système  
**Je veux** calculer le score WQI  
**Afin de** évaluer la qualité globale de l'eau  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-4.1.1 | Rechercher et documenter la formule WQI | 2h | Amine |
| T-4.1.2 | Créer WQICalculator avec les poids des paramètres | 3h | Amine |
| T-4.1.3 | Implémenter sous-indices pour chaque paramètre | 4h | Amine |
| T-4.1.4 | Créer endpoint POST /predict | 2h | Amine |
| T-4.1.5 | Créer DTOs PredictionRequest/PredictionResponse | 1h | Amine |
| T-4.1.6 | Ajouter classification (Bon/Modéré/Mauvais) | 1h | Amine |
| T-4.1.7 | Tests unitaires avec différents scénarios | 2h | Amine |

**Points:** 8 | **Sprint:** 3

---

#### US-4.2: Génération de recommandations
**En tant que** utilisateur  
**Je veux** recevoir des recommandations basées sur le WQI  
**Afin de** savoir quelles actions prendre  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-4.2.1 | Créer base de règles pour recommandations | 2h | Amine |
| T-4.2.2 | Implémenter RecommendationEngine | 3h | Amine |
| T-4.2.3 | Ajouter recommandations par paramètre critique | 2h | Amine |
| T-4.2.4 | Intégrer recommandations dans PredictionResponse | 1h | Amine |
| T-4.2.5 | Internationalisation des messages (FR/EN) | 2h | Amine |

**Points:** 5 | **Sprint:** 4

---

#### US-4.3: Historique des prédictions
**En tant que** utilisateur  
**Je veux** voir l'historique des prédictions  
**Afin de** suivre l'évolution de la qualité  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-4.3.1 | Créer entité PredictionHistory | 1h | Amine |
| T-4.3.2 | Sauvegarder chaque prédiction en base | 1h | Amine |
| T-4.3.3 | Créer endpoint GET /predictions/history | 2h | Amine |
| T-4.3.4 | Ajouter filtres par station et période | 2h | Amine |
| T-4.3.5 | Implémenter pagination | 1h | Amine |

**Points:** 5 | **Sprint:** 4

---

### 🏔️ EPIC 5: Système d'Alertes (Alert Service)

#### US-5.1: Création et gestion des alertes
**En tant que** système  
**Je veux** créer des alertes automatiquement  
**Afin de** notifier les utilisateurs des problèmes  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-5.1.1 | Créer entité Alert (type, severity, status, message) | 1h | Yahya |
| T-5.1.2 | Créer AlertRepository | 30min | Yahya |
| T-5.1.3 | Implémenter AlertService | 2h | Yahya |
| T-5.1.4 | Définir les règles de seuils d'alerte | 2h | Yahya |
| T-5.1.5 | Créer endpoint POST /alerts | 1h | Yahya |
| T-5.1.6 | Créer endpoint GET /alerts (avec filtres) | 2h | Yahya |
| T-5.1.7 | Implémenter acknowledge et resolve | 2h | Yahya |

**Points:** 8 | **Sprint:** 3

---

#### US-5.2: Règles de déclenchement d'alertes
**En tant que** administrateur  
**Je veux** configurer les seuils d'alerte  
**Afin de** personnaliser les déclenchements  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-5.2.1 | Créer entité AlertRule (paramètre, seuil, severity) | 1h | Yahya |
| T-5.2.2 | Implémenter AlertRuleEngine | 3h | Yahya |
| T-5.2.3 | Créer CRUD pour AlertRule | 2h | Yahya |
| T-5.2.4 | Intégrer avec Sensor Service (écoute des mesures) | 2h | Yahya |
| T-5.2.5 | Ajouter règles par défaut (pH < 6.5, temp > 30, etc.) | 1h | Yahya |

**Points:** 5 | **Sprint:** 4

---

#### US-5.3: Communication inter-services pour alertes
**En tant que** système  
**Je veux** recevoir les événements des autres services  
**Afin de** déclencher les alertes appropriées  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-5.3.1 | Configurer communication REST entre services | 2h | Yahya |
| T-5.3.2 | Créer Feign Client pour Sensor Service | 2h | Yahya |
| T-5.3.3 | Créer Feign Client pour STModel Service | 2h | Yahya |
| T-5.3.4 | Implémenter écoute des nouvelles mesures | 2h | Yahya |
| T-5.3.5 | Implémenter écoute des prédictions critiques | 2h | Yahya |

**Points:** 5 | **Sprint:** 4

---

### 🏔️ EPIC 6: Cartographie (Map Service)

#### US-6.1: API de données géospatiales
**En tant que** frontend  
**Je veux** accéder aux données géolocalisées  
**Afin de** afficher les stations sur une carte  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-6.1.1 | Créer endpoint GET /map/stations | 2h | Yahya |
| T-6.1.2 | Retourner données GeoJSON pour les stations | 2h | Yahya |
| T-6.1.3 | Agréger les dernières mesures par station | 2h | Yahya |
| T-6.1.4 | Créer endpoint GET /map/stations/{id}/detail | 2h | Yahya |
| T-6.1.5 | Intégrer avec Sensor et Alert services | 2h | Yahya |

**Points:** 5 | **Sprint:** 3

---

#### US-6.2: Agrégation des données multi-services
**En tant que** map-service  
**Je veux** combiner les données de plusieurs services  
**Afin de** fournir une vue consolidée  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-6.2.1 | Créer Feign Clients pour tous les services | 3h | Yahya |
| T-6.2.2 | Implémenter MapAggregationService | 3h | Yahya |
| T-6.2.3 | Créer DTO StationDetailDTO (mesures + alertes + prédictions) | 1h | Yahya |
| T-6.2.4 | Gérer les erreurs de services indisponibles | 2h | Yahya |
| T-6.2.5 | Ajouter cache pour optimiser les performances | 2h | Yahya |

**Points:** 8 | **Sprint:** 4

---

### 🏔️ EPIC 7: Interface Utilisateur (Frontend Angular)

#### US-7.1: Structure du projet Angular et routing
**En tant que** développeur frontend  
**Je veux** une structure Angular bien organisée  
**Afin de** développer les fonctionnalités UI  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-7.1.1 | Initialiser projet Angular avec CLI | 1h | Yahya |
| T-7.1.2 | Configurer structure (core, pages, shared) | 2h | Yahya |
| T-7.1.3 | Installer dépendances (Bootstrap, Leaflet, Chart.js) | 1h | Yahya |
| T-7.1.4 | Configurer routing avec lazy loading | 2h | Yahya |
| T-7.1.5 | Créer layout principal (navbar, footer) | 2h | Yahya |
| T-7.1.6 | Configurer environnements (dev/prod) | 1h | Yahya |
| T-7.1.7 | Créer ApiService avec HttpClient | 2h | Yahya |

**Points:** 8 | **Sprint:** 2

---

#### US-7.2: Dashboard principal
**En tant que** utilisateur  
**Je veux** voir un tableau de bord synthétique  
**Afin de** avoir une vue d'ensemble rapide  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-7.2.1 | Créer composant DashboardComponent | 1h | Yahya |
| T-7.2.2 | Créer cartes de statistiques (KPIs) | 3h | Yahya |
| T-7.2.3 | Intégrer graphique de qualité (ng2-charts) | 3h | Yahya |
| T-7.2.4 | Afficher liste des dernières alertes | 2h | Yahya |
| T-7.2.5 | Afficher tableau des stations récentes | 2h | Yahya |
| T-7.2.6 | Ajouter animations et design HD | 3h | Yahya |
| T-7.2.7 | Tests et responsive design | 2h | Yahya |

**Points:** 13 | **Sprint:** 3

---

#### US-7.3: Page de gestion des stations
**En tant que** utilisateur  
**Je veux** gérer les stations  
**Afin de** ajouter/modifier/supprimer des points de surveillance  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-7.3.1 | Créer composant StationListComponent | 2h | Amine |
| T-7.3.2 | Créer tableau avec tri et pagination | 3h | Amine |
| T-7.3.3 | Créer composant StationAddComponent (formulaire) | 3h | Amine |
| T-7.3.4 | Créer composant StationDetailComponent | 3h | Amine |
| T-7.3.5 | Implémenter modales de confirmation | 2h | Amine |
| T-7.3.6 | Ajouter validations de formulaire | 2h | Amine |
| T-7.3.7 | Design HD et animations | 2h | Amine |

**Points:** 13 | **Sprint:** 4

---

#### US-7.4: Page de carte interactive
**En tant que** utilisateur  
**Je veux** voir les stations sur une carte  
**Afin de** visualiser géographiquement le réseau  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-7.4.1 | Créer composant StationMapComponent | 2h | Yahya |
| T-7.4.2 | Intégrer Leaflet avec OpenStreetMap | 3h | Yahya |
| T-7.4.3 | Afficher marqueurs des stations | 2h | Yahya |
| T-7.4.4 | Créer popups avec infos station | 2h | Yahya |
| T-7.4.5 | Ajouter couleurs selon qualité (vert/orange/rouge) | 2h | Yahya |
| T-7.4.6 | Implémenter clustering pour beaucoup de stations | 2h | Yahya |

**Points:** 8 | **Sprint:** 4

---

#### US-7.5: Page des alertes
**En tant que** utilisateur  
**Je veux** voir et gérer les alertes  
**Afin de** réagir aux problèmes de qualité  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-7.5.1 | Créer composant AlertsComponent | 2h | Amine |
| T-7.5.2 | Créer tableau des alertes avec filtres | 3h | Amine |
| T-7.5.3 | Afficher badges de sévérité et status | 1h | Amine |
| T-7.5.4 | Créer modale de détail d'alerte | 2h | Amine |
| T-7.5.5 | Implémenter actions (acknowledge, resolve) | 2h | Amine |
| T-7.5.6 | Ajouter compteurs par sévérité | 1h | Amine |

**Points:** 8 | **Sprint:** 5

---

#### US-7.6: Page de prédiction
**En tant que** utilisateur  
**Je veux** faire des prédictions de qualité  
**Afin de** anticiper les problèmes  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-7.6.1 | Créer composant PredictionComponent | 2h | Amine |
| T-7.6.2 | Créer formulaire de saisie des paramètres | 3h | Amine |
| T-7.6.3 | Afficher résultat avec score visuel (cercle) | 3h | Amine |
| T-7.6.4 | Afficher recommandations | 2h | Amine |
| T-7.6.5 | Créer historique des prédictions | 2h | Amine |
| T-7.6.6 | Ajouter graphiques des scores par paramètre | 2h | Amine |

**Points:** 8 | **Sprint:** 5

---

### 🏔️ EPIC 8: API Gateway & Sécurité

#### US-8.1: Configuration de Spring Cloud Gateway
**En tant que** système  
**Je veux** un point d'entrée unique pour les APIs  
**Afin de** centraliser le routing et la sécurité  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-8.1.1 | Créer projet gateway-service avec Spring Cloud Gateway | 2h | Issame |
| T-8.1.2 | Configurer routes vers chaque microservice | 2h | Issame |
| T-8.1.3 | Ajouter CORS configuration | 1h | Issame |
| T-8.1.4 | Configurer load balancing | 2h | Issame |
| T-8.1.5 | Ajouter logging des requêtes | 1h | Issame |
| T-8.1.6 | Configurer rate limiting | 2h | Issame |

**Points:** 5 | **Sprint:** 2

---

#### US-8.2: Authentification et autorisation (optionnel)
**En tant que** administrateur  
**Je veux** sécuriser l'accès aux APIs  
**Afin de** protéger les données sensibles  

| ID | Tâche | Estimation | Assigné à |
|----|-------|------------|-----------|
| T-8.2.1 | Intégrer Spring Security | 2h | Issame |
| T-8.2.2 | Configurer JWT authentication | 4h | Issame |
| T-8.2.3 | Créer endpoints login/register | 3h | Issame |
| T-8.2.4 | Implémenter roles (ADMIN, USER) | 2h | Issame |
| T-8.2.5 | Sécuriser les endpoints sensibles | 2h | Issame |
| T-8.2.6 | Intégrer auth dans le frontend | 3h | Issame |

**Points:** 13 | **Sprint:** 6 (optionnel)

---

## 📊 RÉPARTITION PAR SPRINT

### Sprint 1: Setup & Infrastructure (2 semaines)
| User Story | Points | Assigné |
|------------|--------|---------|
| US-1.1: Environnement de développement | 3 | Tous |
| US-1.2: Structure Maven | 5 | Issame |
| US-1.3: Configuration Docker | 8 | Yahya |
| **Total Sprint 1** | **16** | |

### Sprint 2: Services de base (2 semaines)
| User Story | Points | Assigné |
|------------|--------|---------|
| US-2.1: CRUD Stations | 8 | Issame |
| US-2.2: Mesures capteurs | 8 | Issame |
| US-3.1: Config API Satellite | 5 | Amine |
| US-7.1: Structure Angular | 8 | Yahya |
| US-8.1: Gateway | 5 | Issame |
| **Total Sprint 2** | **34** | |

### Sprint 3: Fonctionnalités core (2 semaines)
| User Story | Points | Assigné |
|------------|--------|---------|
| US-2.3: Statistiques mesures | 5 | Issame |
| US-4.1: Calcul WQI | 8 | Amine |
| US-5.1: Gestion alertes | 8 | Yahya |
| US-6.1: API géospatiale | 5 | Yahya |
| US-7.2: Dashboard | 13 | Yahya |
| **Total Sprint 3** | **39** | |

### Sprint 4: Intégrations avancées (2 semaines)
| User Story | Points | Assigné |
|------------|--------|---------|
| US-3.2: Images satellite | 8 | Amine |
| US-3.3: Traitement images | 13 | Amine |
| US-4.2: Recommandations | 5 | Amine |
| US-4.3: Historique prédictions | 5 | Amine |
| US-5.2: Règles d'alerte | 5 | Yahya |
| US-5.3: Communication inter-services | 5 | Yahya |
| US-6.2: Agrégation données | 8 | Yahya |
| US-7.3: Page stations | 13 | Amine |
| US-7.4: Carte interactive | 8 | Yahya |
| **Total Sprint 4** | **70** | |

### Sprint 5: UI complète (2 semaines)
| User Story | Points | Assigné |
|------------|--------|---------|
| US-7.5: Page alertes | 8 | Amine |
| US-7.6: Page prédiction | 8 | Amine |
| **Total Sprint 5** | **16** | |

### Sprint 6: Sécurité & Polish (optionnel)
| User Story | Points | Assigné |
|------------|--------|---------|
| US-8.2: Authentification | 13 | Issame |
| **Total Sprint 6** | **13** | |

---

## 📈 VÉLOCITÉ ET CHARGE DE TRAVAIL

### Répartition par membre

| Membre | Total Points | % du projet |
|--------|--------------|-------------|
| **Issame Imad** | ~60 points | 33% |
| **Agoumi Mohammed Amine** | ~65 points | 35% |
| **Jabrane Mohamed Yahya** | ~63 points | 32% |

### Vélocité recommandée
- **Vélocité par sprint:** 25-35 points
- **Durée sprint:** 2 semaines
- **Durée totale estimée:** 10-12 semaines

---

## 🛠️ OUTILS DE GESTION RECOMMANDÉS

### Option 1: GitHub Projects (Recommandé - Gratuit)
**URL:** https://github.com/iMaDissame/AquaWatch_ArchitectureMS/projects

#### Comment configurer:
1. Aller sur le repository GitHub
2. Cliquer sur l'onglet **"Projects"**
3. Créer un nouveau projet avec le template **"Team backlog"**
4. Créer les colonnes: `Backlog` | `Sprint X` | `In Progress` | `Review` | `Done`
5. Ajouter les issues depuis le backlog

### Option 2: Jira (Gratuit jusqu'à 10 users)
**URL:** https://www.atlassian.com/software/jira/free

### Option 3: Trello (Gratuit)
**URL:** https://trello.com

### Option 4: Azure DevOps (Gratuit)
**URL:** https://dev.azure.com

---

## 📋 TEMPLATE POUR CRÉER LES ISSUES

```markdown
## 📝 [US-X.X] Titre de la User Story

### Description
**En tant que** [rôle]
**Je veux** [fonctionnalité]
**Afin de** [bénéfice]

### Critères d'acceptation
- [ ] Critère 1
- [ ] Critère 2
- [ ] Critère 3

### Tâches
- [ ] T-X.X.1: Description tâche 1
- [ ] T-X.X.2: Description tâche 2

### Informations
- **Epic:** EPIC X - Nom
- **Points:** X
- **Sprint:** X
- **Assigné:** @username

### Labels
`epic-x` `sprint-x` `backend/frontend` `priority-high/medium/low`
```

---

## 🎯 PROCHAINES ÉTAPES

1. **Créer le projet GitHub Projects** avec le backlog
2. **Créer les labels** pour les épiques et priorités
3. **Créer les milestones** pour chaque sprint
4. **Assigner les premières tâches** du Sprint 1
5. **Planifier le Sprint Planning** de démarrage

---

*Document généré le 8 décembre 2025 - AquaWatch Team*
