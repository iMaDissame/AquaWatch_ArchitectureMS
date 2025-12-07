import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { StationOverview } from '../../core/models/station-overview.model';
import { Map } from '../../core/services/map';
import { Router } from '@angular/router';

import * as L from 'leaflet';

@Component({
  selector: 'app-station-map',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './station-map.html',
  styleUrl: './station-map.scss',
})
export class StationMap{
  stations: StationOverview[] = [];
  loading = false;
  error: string | null = null;
  private map: L.Map | null = null;
  private markersLayer: L.LayerGroup | null = null;


  constructor(
    private mapService: Map,
    private router: Router
  ) {}

   ngOnInit(): void {
    this.loadStations();
  }
  loadStations(): void {
    this.loading = true;
    this.error = null;

    this.mapService.getStationsOverview().subscribe({
      next: (data) => {
        this.stations = data;
        this.loading = false;

        // initialiser la carte si ce n'est pas déjà fait
        if (!this.map) {
          this.initMap();
        }

        // mettre à jour les marqueurs avec les stations
        this.updateMarkers();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Erreur lors du chargement des stations.';
        this.loading = false;
      }
    });
  }

  // --------- Initialisation de la carte Leaflet ---------
  private initMap(): void {
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