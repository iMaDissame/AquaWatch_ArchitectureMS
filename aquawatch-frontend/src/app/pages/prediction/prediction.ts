import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ApiService, Station, PredictionHistory } from '../../core/services/api.service';

interface PredictionResult {
  stationId: number;
  stationName: string;
  score: number;
  status: string;
  timestamp: string;
  details: string;
  parameters: {
    ph: number;
    temperature: number;
    turbidity: number;
    dissolvedOxygen: number;
    conductivity: number;
  };
  parameterScores: { [key: string]: number };
  recommendations: string[];
}

@Component({
  selector: 'app-prediction',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './prediction.html',
  styleUrl: './prediction.scss'
})
export class PredictionComponent implements OnInit {
  predictionForm!: FormGroup;
  stations: Station[] = [];
  loading = false;
  loadingStations = true;
  loadingHistory = false;
  error: string | null = null;
  result: PredictionResult | null = null;
  predictionHistory: PredictionHistory[] = [];
  selectedStationId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.predictionForm = this.fb.group({
      stationId: ['', Validators.required],
      ph: [7.0, [Validators.required, Validators.min(0), Validators.max(14)]],
      temperature: [22.0, [Validators.required, Validators.min(0), Validators.max(50)]],
      turbidity: [3.0, [Validators.required, Validators.min(0), Validators.max(100)]],
      dissolvedOxygen: [8.0, [Validators.required, Validators.min(0), Validators.max(20)]],
      conductivity: [400, [Validators.required, Validators.min(0), Validators.max(5000)]]
    });

    this.loadStations();
  }

  loadStations(): void {
    this.apiService.getAllStations().subscribe({
      next: (stations) => {
        this.stations = stations;
        this.loadingStations = false;
        if (stations.length > 0) {
          this.predictionForm.patchValue({ stationId: stations[0].id });
          this.selectedStationId = stations[0].id;
          this.loadHistory(stations[0].id);
        }
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading stations:', err);
        this.loadingStations = false;
        this.cdr.markForCheck();
      }
    });
  }

  onStationChange(): void {
    const stationId = Number(this.predictionForm.get('stationId')?.value);
    if (stationId && stationId !== this.selectedStationId) {
      this.selectedStationId = stationId;
      this.loadHistory(stationId);
    }
  }

  loadHistory(stationId: number): void {
    this.loadingHistory = true;
    this.cdr.markForCheck();
    
    this.apiService.getPredictionHistory(stationId).subscribe({
      next: (history) => {
        this.predictionHistory = history;
        this.loadingHistory = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading prediction history:', err);
        this.predictionHistory = [];
        this.loadingHistory = false;
        this.cdr.markForCheck();
      }
    });
  }

  onPredict(): void {
    if (this.predictionForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = null;
    this.result = null;
    this.cdr.markForCheck();

    const formData = this.predictionForm.value;
    const stationId = Number(formData.stationId);
    const station = this.stations.find(s => s.id === stationId);

    // Utiliser l'endpoint de prédiction rapide - convertir en nombres
    const predictionRequest = {
      stationId: stationId,
      ph: Number(formData.ph),
      temperature: Number(formData.temperature),
      turbidity: Number(formData.turbidity),
      dissolvedOxygen: Number(formData.dissolvedOxygen),
      conductivity: Number(formData.conductivity)
    };

    console.log('Sending prediction request:', predictionRequest);

    this.apiService.predictQuality(predictionRequest).subscribe({
      next: (predictionResult) => {
        console.log('Prediction result:', predictionResult);
        this.result = {
          stationId: stationId,
          stationName: station?.name || 'Station',
          score: predictionResult.score,
          status: predictionResult.status,
          timestamp: new Date().toISOString(),
          details: predictionResult.details,
          parameters: {
            ph: Number(formData.ph),
            temperature: Number(formData.temperature),
            turbidity: Number(formData.turbidity),
            dissolvedOxygen: Number(formData.dissolvedOxygen),
            conductivity: Number(formData.conductivity)
          },
          parameterScores: predictionResult.parameterScores || {},
          recommendations: predictionResult.recommendations || []
        };
        this.loading = false;
        // Recharger l'historique après la prédiction
        this.loadHistory(stationId);
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error predicting quality:', err);
        this.error = 'Erreur lors de la prédiction de la qualité: ' + (err.message || err.statusText || 'Erreur inconnue');
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'GOOD': return 'status-good';
      case 'MODERATE': return 'status-moderate';
      case 'BAD': return 'status-bad';
      default: return 'status-unknown';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'GOOD': return 'Bonne qualité';
      case 'MODERATE': return 'Qualité modérée';
      case 'BAD': return 'Mauvaise qualité';
      default: return 'Inconnue';
    }
  }

  getScoreColor(score: number): string {
    if (score >= 80) return '#28a745';
    if (score >= 60) return '#ffc107';
    return '#dc3545';
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'GOOD': return 'success';
      case 'MODERATE': return 'warning';
      case 'BAD': return 'danger';
      default: return 'secondary';
    }
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
