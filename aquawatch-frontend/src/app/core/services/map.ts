import { Injectable } from '@angular/core';
import { Alert } from '../models/alert.model';
import { Observable } from 'rxjs';
import { StationDetail } from '../models/station-detail.model';
import { StationOverview } from '../models/station-overview.model';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class Map {
  private baseUrl = `${environment.apiBaseUrl}/map`;

  constructor(private http: HttpClient) {}

  getStationsOverview(): Observable<StationOverview[]> {
    return this.http.get<StationOverview[]>(`${this.baseUrl}/stations`);
  }

  getStationDetail(stationId: number): Observable<StationDetail> {
    return this.http.get<StationDetail>(`${this.baseUrl}/stations/${stationId}`);
  }

  getActiveAlerts(): Observable<Alert[]> {
    return this.http.get<Alert[]>(`${this.baseUrl}/alerts/active`);
  }
  
}
