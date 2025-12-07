import { CommonModule } from '@angular/common';
import { Component, AfterViewChecked, OnDestroy, OnInit, ChangeDetectorRef } from '@angular/core';
import { StationOverview } from '../../core/models/station-overview.model';
import { MapService } from '../../core/services/map';
import { Router } from '@angular/router';

import * as L from 'leaflet';

// Fix for Leaflet marker icons in Angular
const iconRetinaUrl = 'assets/marker-icon-2x.png';
const iconUrl = 'assets/marker-icon.png';
const shadowUrl = 'assets/marker-shadow.png';
L.Marker.prototype.options.icon = L.icon({
  iconRetinaUrl,
  iconUrl,
  shadowUrl,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  tooltipAnchor: [16, -28],
  shadowSize: [41, 41]
});

@Component({
  selector: 'app-station-map',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './station-map.html',
  styleUrl: './station-map.scss',
})
export class StationMap implements OnInit, AfterViewChecked, OnDestroy {
  stations: StationOverview[] = [];
  loading = true;
  error: string | null = null;
  private map: L.Map | null = null;
  private markersLayer: L.LayerGroup | null = null;
  private mapInitialized = false;

  constructor(
    private mapService: MapService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadStations();
  }

  ngAfterViewChecked(): void {
    // La carte est maintenant initialisée dans loadStations après le chargement des données
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  loadStations(): void {
    this.loading = true;
    this.error = null;

    this.mapService.getStationsOverview().subscribe({
      next: (data) => {
        console.log('Stations loaded:', data);
        this.stations = data;
        this.loading = false;
        this.cdr.markForCheck();
        // Initialiser la carte après le chargement des données
        setTimeout(() => {
          if (!this.mapInitialized) {
            this.initMap();
            this.updateMarkers();
            this.mapInitialized = true;
          }
        }, 200);
      },
      error: (err) => {
        console.error('Error loading stations:', err);
        this.error = 'Erreur lors du chargement des stations.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  // --------- Initialisation de la carte Leaflet ---------
  private initMap(): void {
    // Vérifier que le conteneur existe
    const mapContainer = document.getElementById('map');
    if (!mapContainer) {
      console.error('Map container not found');
      return;
    }

    // centre initial (Maroc / zone projet)
    const initialCenter: L.LatLngExpression = [31.6, -7.9]; // Marrakech approx
    const initialZoom = 6;

    this.map = L.map('map', {
      center: initialCenter,
      zoom: initialZoom
    });

    // fond de carte OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    // couche pour les marqueurs
    this.markersLayer = L.layerGroup().addTo(this.map);

    // Forcer le rafraîchissement de la carte après un court délai
    setTimeout(() => {
      if (this.map) {
        this.map.invalidateSize();
      }
    }, 100);
  }

   // --------- Ajout / mise à jour des marqueurs ---------
  private updateMarkers(): void {
    if (!this.map || !this.markersLayer) {
      return;
    }

    // effacer les anciens marqueurs
    this.markersLayer.clearLayers();

    const bounds: L.LatLngBounds = L.latLngBounds([]);

    this.stations.forEach(st => {
      if (st.latitude != null && st.longitude != null) {
        const color = this.getStatusColor(st.currentStatus);

        const marker = L.circleMarker([st.latitude, st.longitude], {
          radius: 8,
          color: color,      // bordure
          weight: 2,
          fillColor: color,  // remplissage
          fillOpacity: 0.9
        });

        marker.bindPopup(`
          <strong>${st.name}</strong><br/>
          Code: ${st.code}<br/>
          Statut: ${st.currentStatus || 'N/A'}<br/>
          Score: ${st.currentScore ?? 'N/A'}
        `);

        // clic sur le marqueur => aller sur la page détail
        marker.on('click', () => {
          this.openDetail(st.stationId);
        });

        marker.addTo(this.markersLayer!);
        bounds.extend([st.latitude, st.longitude]);
      }
    });

    // adapter le zoom à toutes les stations si on a au moins 1 point
    if (bounds.isValid()) {
      this.map.fitBounds(bounds.pad(0.2)); // padding pour respirer
    }
  }

  // --------- Couleurs de statut (pour les marqueurs) ---------
  private getStatusColor(status: string | null): string {
    switch (status) {
      case 'GOOD':
        return '#28a745'; // vert
      case 'MODERATE':
        return '#ffc107'; // jaune/orange
      case 'BAD':
        return '#dc3545'; // rouge
      default:
        return '#6c757d'; // gris
    }
  }


  getStatusBadgeClass(status: string | null): string {
    switch (status) {
      case 'GOOD':
        return 'badge bg-success';
      case 'MODERATE':
        return 'badge bg-warning text-dark';
      case 'BAD':
        return 'badge bg-danger';
      default:
        return 'badge bg-secondary';
    }
  }

  openDetail(stationId: number): void {
    this.router.navigate(['/stations', stationId]);
  }
}