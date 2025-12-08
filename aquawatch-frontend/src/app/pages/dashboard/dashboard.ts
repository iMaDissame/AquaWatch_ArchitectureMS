import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { ApiService, StationOverview, Alert, QualityForecast } from '../../core/services/api.service';
import { interval, Subscription, forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, BaseChartDirective],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit, OnDestroy {
  stations: StationOverview[] = [];
  alerts: Alert[] = [];
  forecasts: Map<number, QualityForecast> = new Map();
  
  loading = true;
  error: string | null = null;
  lastUpdate: Date = new Date();
  
  private refreshSubscription?: Subscription;

  // Stats
  totalStations = 0;
  goodStations = 0;
  moderateStations = 0;
  badStations = 0;
  activeAlerts = 0;

  // Chart configurations
  qualityPieChartData: ChartData<'pie'> = {
    labels: ['Bonne', 'Modérée', 'Mauvaise'],
    datasets: [{
      data: [0, 0, 0],
      backgroundColor: ['#28a745', '#ffc107', '#dc3545']
    }]
  };

  qualityPieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: {
        position: 'bottom'
      },
      title: {
        display: true,
        text: 'Qualité de l\'eau par station'
      }
    }
  };

  scoreBarChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [{
      label: 'Score WQI',
      data: [],
      backgroundColor: []
    }]
  };

  scoreBarChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    scales: {
      y: {
        beginAtZero: true,
        max: 100,
        title: {
          display: true,
          text: 'Score WQI'
        }
      }
    },
    plugins: {
      legend: {
        display: false
      },
      title: {
        display: true,
        text: 'Scores de qualité par station'
      }
    }
  };

  constructor(private apiService: ApiService, private cdr: ChangeDetectorRef, private router: Router) {}

  ngOnInit(): void {
    this.loadData();
    // Rafraîchir toutes les 30 secondes
    this.refreshSubscription = interval(30000).subscribe(() => {
      this.loadData();
    });
  }

  ngOnDestroy(): void {
    this.refreshSubscription?.unsubscribe();
  }

  loadData(): void {
    this.loading = true;
    this.error = null;
    
    // Charger les stations et alertes en parallèle avec forkJoin
    forkJoin({
      stations: this.apiService.getStationsOverview(),
      alerts: this.apiService.getActiveAlerts()
    }).pipe(
      finalize(() => {
        this.loading = false;
        this.lastUpdate = new Date();
        this.cdr.detectChanges();
        console.log('Loading finished, loading =', this.loading);
      })
    ).subscribe({
      next: (result) => {
        this.stations = result.stations || [];
        this.alerts = result.alerts || [];
        this.activeAlerts = this.alerts.length;
        this.updateStats();
        this.updateCharts();
        console.log('Dashboard data loaded:', { stations: this.stations.length, alerts: this.alerts.length });
      },
      error: (err) => {
        console.error('Error loading dashboard data:', err);
        this.error = 'Erreur lors du chargement des données. Vérifiez que les services sont démarrés.';
      }
    });
  }

  private updateStats(): void {
    this.totalStations = this.stations.length;
    this.goodStations = this.stations.filter(s => s.currentStatus === 'GOOD').length;
    this.moderateStations = this.stations.filter(s => s.currentStatus === 'MODERATE').length;
    this.badStations = this.stations.filter(s => s.currentStatus === 'BAD').length;
  }

  private updateCharts(): void {
    // Mise à jour du pie chart
    this.qualityPieChartData = {
      labels: ['Bonne', 'Modérée', 'Mauvaise'],
      datasets: [{
        data: [this.goodStations, this.moderateStations, this.badStations],
        backgroundColor: ['#28a745', '#ffc107', '#dc3545']
      }]
    };

    // Mise à jour du bar chart
    const labels = this.stations.map(s => s.code);
    const scores = this.stations.map(s => s.currentScore || 0);
    const colors = this.stations.map(s => this.getStatusColor(s.currentStatus));

    this.scoreBarChartData = {
      labels: labels,
      datasets: [{
        label: 'Score WQI',
        data: scores,
        backgroundColor: colors
      }]
    };
  }

  getStatusColor(status: string | null): string {
    switch (status) {
      case 'GOOD': return '#28a745';
      case 'MODERATE': return '#ffc107';
      case 'BAD': return '#dc3545';
      default: return '#6c757d';
    }
  }

  getStatusLabel(status: string | null): string {
    switch (status) {
      case 'GOOD': return 'Bonne';
      case 'MODERATE': return 'Modérée';
      case 'BAD': return 'Mauvaise';
      default: return 'Inconnu';
    }
  }

  getSeverityClass(severity: string): string {
    switch (severity) {
      case 'CRITICAL': return 'badge bg-danger';
      case 'WARNING': return 'badge bg-warning text-dark';
      case 'INFO': return 'badge bg-info';
      default: return 'badge bg-secondary';
    }
  }

  refreshData(): void {
    this.loadData();
  }

  computeQuality(stationId: number): void {
    this.apiService.computeQuality(stationId).subscribe({
      next: () => {
        this.loadData();
      },
      error: (err) => console.error('Erreur calcul qualité:', err)
    });
  }

  acknowledgeAlert(alertId: number): void {
    this.apiService.acknowledgeAlert(alertId).subscribe({
      next: () => {
        this.loadData();
      },
      error: (err) => console.error('Erreur acknowledge:', err)
    });
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }
}
