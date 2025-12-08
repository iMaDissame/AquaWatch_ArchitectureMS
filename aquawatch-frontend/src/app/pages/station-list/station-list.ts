import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ApiService, Station, PredictionHistory } from '../../core/services/api.service';

@Component({
  selector: 'app-station-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './station-list.html',
  styleUrl: './station-list.scss'
})
export class StationListComponent implements OnInit {
  stations: Station[] = [];
  loading = true;
  error: string | null = null;
  
  // Modal d'édition
  editingStation: Station | null = null;
  editForm!: FormGroup;
  saving = false;
  
  // Modal d'historique
  selectedStationForHistory: Station | null = null;
  predictionHistory: PredictionHistory[] = [];
  loadingHistory = false;

  constructor(
    private apiService: ApiService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadStations();
  }

  initForm(): void {
    this.editForm = this.fb.group({
      code: ['', Validators.required],
      name: ['', Validators.required],
      type: ['SURFACE'],
      latitude: [0, [Validators.required, Validators.min(-90), Validators.max(90)]],
      longitude: [0, [Validators.required, Validators.min(-180), Validators.max(180)]],
      commune: [''],
      description: ['']
    });
  }

  loadStations(): void {
    this.loading = true;
    this.error = null;
    this.cdr.markForCheck();

    this.apiService.getAllStations().subscribe({
      next: (stations) => {
        this.stations = stations;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading stations:', err);
        this.error = 'Erreur lors du chargement des stations';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  openEditModal(station: Station): void {
    this.editingStation = station;
    this.editForm.patchValue({
      code: station.code,
      name: station.name,
      type: station.type || 'SURFACE',
      latitude: station.latitude,
      longitude: station.longitude,
      commune: station.commune || '',
      description: station.description || ''
    });
    this.cdr.markForCheck();
  }

  closeEditModal(): void {
    this.editingStation = null;
    this.editForm.reset();
    this.cdr.markForCheck();
  }

  saveStation(): void {
    if (this.editForm.invalid || !this.editingStation) return;

    this.saving = true;
    this.cdr.markForCheck();

    const updatedStation = { ...this.editForm.value };

    this.apiService.updateStation(this.editingStation.id, updatedStation).subscribe({
      next: (station) => {
        // Mettre à jour dans la liste
        const index = this.stations.findIndex(s => s.id === this.editingStation!.id);
        if (index !== -1) {
          this.stations[index] = station;
        }
        this.saving = false;
        this.closeEditModal();
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error updating station:', err);
        this.saving = false;
        this.cdr.markForCheck();
      }
    });
  }

  deleteStation(station: Station): void {
    if (!confirm(`Êtes-vous sûr de vouloir supprimer la station "${station.name}" ?`)) {
      return;
    }

    this.apiService.deleteStation(station.id).subscribe({
      next: () => {
        this.stations = this.stations.filter(s => s.id !== station.id);
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error deleting station:', err);
        alert('Erreur lors de la suppression de la station');
        this.cdr.markForCheck();
      }
    });
  }

  openHistoryModal(station: Station): void {
    this.selectedStationForHistory = station;
    this.loadingHistory = true;
    this.predictionHistory = [];
    this.cdr.markForCheck();

    this.apiService.getPredictionHistory(station.id).subscribe({
      next: (history) => {
        this.predictionHistory = history;
        this.loadingHistory = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading history:', err);
        this.loadingHistory = false;
        this.cdr.markForCheck();
      }
    });
  }

  closeHistoryModal(): void {
    this.selectedStationForHistory = null;
    this.predictionHistory = [];
    this.cdr.markForCheck();
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'GOOD': return 'bg-success';
      case 'MODERATE': return 'bg-warning text-dark';
      case 'BAD': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'GOOD': return 'Bonne';
      case 'MODERATE': return 'Modérée';
      case 'BAD': return 'Mauvaise';
      default: return 'Inconnue';
    }
  }

  getScoreColor(score: number): string {
    if (score >= 80) return '#28a745';
    if (score >= 60) return '#ffc107';
    return '#dc3545';
  }

  getScoreLevel(score: number): string {
    if (score >= 70) return 'good';
    if (score >= 40) return 'moderate';
    return 'bad';
  }

  getTypeLabel(type: string | null | undefined): string {
    switch (type) {
      case 'SURFACE': return 'Eau de surface';
      case 'GROUNDWATER': return 'Eau souterraine';
      case 'COASTAL': return 'Côtière';
      case 'RESERVOIR': return 'Réservoir';
      default: return 'Non défini';
    }
  }
}
