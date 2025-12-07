import { Injectable } from '@angular/core';
import { Alert } from '../models/alert.model';
import { Observable } from 'rxjs';
import { StationDetail } from '../models/station-detail.model';
import { StationOverview } from '../models/station-overview.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class MapService {
  // URL directe pour éviter toute duplication
  private baseUrl = 'http://localhost:8080/map/api/map';

  constructor(private http: HttpClient) {}

  getStationsOverview(): Observable<StationOverview[]> {
    console.log('Fetching stations from:', `${this.baseUrl}/stations`);
    return this.http.get<StationOverview[]>(`${this.baseUrl}/stations`);
  }

  getStationDetail(stationId: number): Observable<StationDetail> {
    return this.http.get<StationDetail>(`${this.baseUrl}/stations/${stationId}`);
  }

  getActiveAlerts(): Observable<Alert[]> {
    return this.http.get<Alert[]>(`${this.baseUrl}/alerts/active`);
  }
  
}
