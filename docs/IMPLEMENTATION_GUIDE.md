# AquaWatch-MS - Guide d'implémentation

## 🎯 Résumé de l'architecture

AquaWatch-MS est une plateforme de surveillance de la qualité de l'eau basée sur une architecture microservices.

## 📊 Services implémentés

| Service | Port | Description |
|---------|------|-------------|
| discovery-service | 8761 | Eureka Discovery Server |
| gateway-service | 8080 | Spring Cloud Gateway (point d'entrée API) |
| sensor-service | 8081 | Gestion des capteurs et mesures |
| satellite-service | 8082 | Données satellite Sentinel-1/2 |
| stmodel-service | 8083 | Fusion et scoring qualité (WQI) |
| alert-service | 8084 | Gestion des alertes |
| map-service | 8085 | Agrégation pour cartographie |

## 🧪 Algorithme Water Quality Index (WQI)

Basé sur les normes OMS et le dataset Water Quality Testing (500 échantillons):

### Paramètres et poids

| Paramètre | Plage idéale | Poids |
|-----------|-------------|-------|
| pH | 6.5 - 8.5 | 20% |
| Température | 15 - 25°C | 15% |
| Turbidité | < 5 NTU | 20% |
| Oxygène dissous | > 8 mg/L | 25% |
| Conductivité | 200 - 500 µS/cm | 10% |
| Données satellite | NDWI, turbidité | 10% |

### Classification du score

- **GOOD** (≥70): Qualité excellente à bonne
- **MODERATE** (40-69): Qualité acceptable avec surveillance
- **BAD** (<40): Qualité dégradée, action requise

## 🚀 Démarrage des services

### Ordre de démarrage

```bash
# 1. Discovery Service (Eureka) - PREMIER
cd discovery-service
./mvnw spring-boot:run

# 2. Attendre 30s puis lancer les autres
cd sensor-service && ./mvnw spring-boot:run &
cd satellite-service && ./mvnw spring-boot:run &
cd stmodel-service && ./mvnw spring-boot:run &
cd alert-service && ./mvnw spring-boot:run &

# 3. Gateway (après que les services soient enregistrés)
cd gateway-service && ./mvnw spring-boot:run &

# 4. Map Service (dépend des autres)
cd map-service && ./mvnw spring-boot:run &
```

### Scripts Windows

```batch
# Démarrer tous les services
start-all-services.bat

# Arrêter tous les services
stop-all-services.bat
```

## 📡 Points d'entrée API (via Gateway :8080)

### Sensor Service
```
GET  /sensor/api/stations              # Liste des stations
GET  /sensor/api/stations/{id}         # Détail station
GET  /sensor/api/measurements/{id}     # Dernière mesure
POST /sensor/api/measurements          # Nouvelle mesure
```

### Satellite Service
```
GET  /satellite/api/scenes             # Scènes satellite
GET  /satellite/api/metrics/{stationId} # Métriques pour station
```

### STModel Service
```
GET  /stmodel/api/quality/observation/{stationId}  # Observation qualité
POST /stmodel/api/quality/compute/{stationId}      # Calculer score
GET  /stmodel/api/forecast/{stationId}             # Prévisions
```

### Alert Service
```
GET  /alerts/api/alerts/active                # Alertes actives
GET  /alerts/api/alerts/station/{stationId}   # Alertes par station
PUT  /alerts/api/alerts/{id}/acknowledge      # Acquitter alerte
```

### Map Service
```
GET  /map/api/map/stations             # Vue agrégée pour carte
GET  /map/api/map/stations/{id}        # Détail complet station
GET  /map/api/map/alerts/active        # Alertes pour dashboard
```

## 🗃️ Données de test

Les services incluent des `DataInitializer` qui créent automatiquement:

- **6 stations** (Rabat, Casablanca, Meknès, Kénitra, Toubkal)
- **24 mesures/station** (dernières 24h)
- **4 scènes satellite** (Sentinel-1/2)
- **7 alertes** de différents types

Paramètres générés basés sur le dataset réel:
- pH: 6.83 - 7.48
- Température: 20.3 - 23.6°C
- Turbidité: 3.1 - 5.1 NTU
- Oxygène dissous: 6.0 - 9.9 mg/L
- Conductivité: 316 - 370 µS/cm

## 📈 Modèle de prévision

Le `ForecastService` utilise:

1. **TrendRegressionModel** (si ≥5 observations):
   - Régression linéaire sur historique
   - Facteurs saisonniers
   - Intervalles de confiance

2. **SimpleDegradationModel** (fallback):
   - Dégradation linéaire (-3 points/jour)
   - Utilisé quand historique insuffisant

## 🔧 Configuration

### application.properties (profil dev)

```properties
# Base de données H2 en mémoire
spring.datasource.url=jdbc:h2:mem:servicedb
spring.h2.console.enabled=true

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.instance.prefer-ip-address=true
```

### Eureka Dashboard

Accéder à http://localhost:8761 pour voir tous les services enregistrés.

## 🐳 Prochaines étapes

1. **Containerisation**: Ajouter Dockerfiles
2. **Tests**: Compléter les tests unitaires et d'intégration
3. **Frontend**: Connecter l'application Angular
4. **Sécurité**: Ajouter Spring Security + JWT
5. **Monitoring**: Intégrer Prometheus + Grafana
