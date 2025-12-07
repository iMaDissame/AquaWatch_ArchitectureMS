import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService, Alert, Station, PredictionHistory } from '../../core/services/api.service';

@Component({
  selector: 'app-alerts',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './alerts.html',
  styleUrl: './alerts.scss'
})
export class AlertsComponent implements OnInit {
  alerts: Alert[] = [];
  stations: Station[] = [];
  loading = true;
  error: string | null = null;
  
  // Filtres
  filterStatus: 'all' | 'OPEN' | 'ACKNOWLEDGED' | 'RESOLVED' = 'all';
  filterSeverity: 'all' | 'CRITICAL' | 'WARNING' | 'INFO' = 'all';
  
  // Détails de l'alerte sélectionnée
  selectedAlert: Alert | null = null;
  selectedAlertHistory: PredictionHistory | null = null;
  loadingDetails = false;

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.error = null;
    this.cdr.markForCheck();

    // Charger les alertes et les stations
    this.apiService.getActiveAlerts().subscribe({
      next: (alerts) => {
        this.alerts = alerts;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading alerts:', err);
        this.error = 'Erreur lors du chargement des alertes';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });

    this.apiService.getAllStations().subscribe({
      next: (stations) => {
        this.stations = stations;
        this.cdr.markForCheck();
      }
    });
  }

  get filteredAlerts(): Alert[] {
    return this.alerts.filter(alert => {
      const matchStatus = this.filterStatus === 'all' || alert.status === this.filterStatus;
      const matchSeverity = this.filterSeverity === 'all' || alert.severity === this.filterSeverity;
      return matchStatus && matchSeverity;
    });
  }

  getStationName(stationId: number): string {
    const station = this.stations.find(s => s.id === stationId);
    return station ? station.name : `Station #${stationId}`;
  }

  acknowledgeAlert(alert: Alert): void {
    this.apiService.acknowledgeAlert(alert.id).subscribe({
      next: (updatedAlert) => {
        const index = this.alerts.findIndex(a => a.id === alert.id);
        if (index !== -1) {
          this.alerts[index] = updatedAlert;
        }
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error acknowledging alert:', err);
        this.cdr.markForCheck();
      }
    });
  }

  resolveAlert(alert: Alert): void {
    this.apiService.resolveAlert(alert.id).subscribe({
      next: (updatedAlert) => {
        const index = this.alerts.findIndex(a => a.id === alert.id);
        if (index !== -1) {
          this.alerts[index] = updatedAlert;
        }
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error resolving alert:', err);
        this.cdr.markForCheck();
      }
    });
  }

  getSeverityClass(severity: string): string {
    switch (severity) {
      case 'CRITICAL': return 'bg-danger';
      case 'WARNING': return 'bg-warning text-dark';
      case 'INFO': return 'bg-info';
      default: return 'bg-secondary';
    }
  }

  getSeverityIcon(severity: string): string {
    switch (severity) {
      case 'CRITICAL': return 'bi-exclamation-triangle-fill';
      case 'WARNING': return 'bi-exclamation-circle-fill';
      case 'INFO': return 'bi-info-circle-fill';
      default: return 'bi-bell';
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'OPEN': return 'bg-danger';
      case 'ACKNOWLEDGED': return 'bg-warning text-dark';
      case 'RESOLVED': return 'bg-success';
      default: return 'bg-secondary';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'OPEN': return 'Ouvert';
      case 'ACKNOWLEDGED': return 'Reconnu';
      case 'RESOLVED': return 'Résolu';
      default: return status;
    }
  }

  getTypeLabel(type: string): string {
    switch (type) {
      case 'THRESHOLD_BREACH': return 'Seuil dépassé';
      case 'FORECAST_RISK': return 'Risque prévu';
      case 'SENSOR_FAILURE': return 'Capteur défaillant';
      default: return type;
    }
  }

  viewAlertDetails(alert: Alert): void {
    this.selectedAlert = alert;
    this.selectedAlertHistory = null;
    this.loadingDetails = true;
    this.cdr.markForCheck();

    // Charger l'historique des prédictions pour trouver les recommandations
    this.apiService.getPredictionHistory(alert.stationId).subscribe({
      next: (history) => {
        // Trouver la prédiction la plus proche de l'événement d'alerte
        if (history.length > 0) {
          this.selectedAlertHistory = history[0]; // La plus récente
        }
        this.loadingDetails = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading prediction history:', err);
        this.loadingDetails = false;
        this.cdr.markForCheck();
      }
    });
  }

  closeAlertDetails(): void {
    this.selectedAlert = null;
    this.selectedAlertHistory = null;
    this.cdr.markForCheck();
  }

  getScoreColor(score: number): string {
    if (score >= 80) return '#28a745';
    if (score >= 60) return '#ffc107';
    return '#dc3545';
  }

  getParamLabel(param: string): string {
    const labels: { [key: string]: string } = {
      'ph': 'pH',
      'temperature': 'Température',
      'turbidity': 'Turbidité',
      'dissolvedOxygen': 'Oxygène dissous',
      'conductivity': 'Conductivité'
    };
    return labels[param] || param;
  }
}
