import { CommonModule } from '@angular/common';
import { Component, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MapService } from '../../core/services/map';
import { StationDetail as StationDetailModel } from '../../core/models/station-detail.model';
import { ApiService, PredictionHistory } from '../../core/services/api.service';

@Component({
  selector: 'app-station-detail',
  standalone: true, 
  imports: [CommonModule, RouterModule],
  templateUrl: './station-detail.html',
  styleUrl: './station-detail.scss',
})
export class StationDetail {

  stationId!: number;
  detail: StationDetailModel | null = null;
  loading = false;
  error: string | null = null;
  predictionHistory: PredictionHistory[] = [];
  loadingHistory = false;

  constructor(
    private route: ActivatedRoute,
    private mapService: MapService,
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  // plus besoin d'implements OnInit, Angular appelle quand même ngOnInit
  ngOnInit(): void {
    this.stationId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadDetail();
    this.loadPredictionHistory();
  }

  loadDetail(): void {
    this.loading = true;
    this.error = null;
    this.cdr.markForCheck();

    this.mapService.getStationDetail(this.stationId).subscribe({
      next: (data: StationDetailModel) => {
        this.detail = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error(err);
        this.error = 'Erreur lors du chargement des détails de la station.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadPredictionHistory(): void {
    this.loadingHistory = true;
    this.cdr.markForCheck();

    this.apiService.getPredictionHistory(this.stationId).subscribe({
      next: (history: PredictionHistory[]) => {
        this.predictionHistory = history;
        this.loadingHistory = false;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Error loading prediction history:', err);
        this.predictionHistory = [];
        this.loadingHistory = false;
        this.cdr.markForCheck();
      }
    });
  }

  getStatusBadgeClass(status: string | null | undefined): string {
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

  getScoreColor(score: number): string {
    if (score >= 80) return '#28a745';
    if (score >= 60) return '#ffc107';
    return '#dc3545';
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'GOOD': return 'Bonne';
      case 'MODERATE': return 'Modérée';
      case 'BAD': return 'Mauvaise';
      default: return 'Inconnue';
    }
  }

}
