import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Map } from '../../core/services/map';
import { StationDetail as StationDetailModel } from '../../core/models/station-detail.model';

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

  constructor(
    private route: ActivatedRoute,
    private mapService: Map
  ) {}

  // plus besoin d'implements OnInit, Angular appelle quand même ngOnInit
  ngOnInit(): void {
    this.stationId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadDetail();
  }

  loadDetail(): void {
    this.loading = true;
    this.error = null;

    this.mapService.getStationDetail(this.stationId).subscribe({
      next: (data) => {
        this.detail = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Erreur lors du chargement des détails de la station.';
        this.loading = false;
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

}
