import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = environment.apiUrl || 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  // ========== STATIONS (via map-service) ==========
  getStationsOverview(): Observable<StationOverview[]> {
    return this.http.get<StationOverview[]>(`${this.baseUrl}/map/api/map/stations`).pipe(
      catchError(this.handleError<StationOverview[]>('getStationsOverview', []))
    );
  }

  getStationDetail(stationId: number): Observable<StationDetail> {
    return this.http.get<StationDetail>(`${this.baseUrl}/map/api/map/stations/${stationId}`).pipe(
      catchError(this.handleError<StationDetail>('getStationDetail'))
    );
  }

  // ========== SENSORS ==========
  getAllStations(): Observable<Station[]> {
    return this.http.get<Station[]>(`${this.baseUrl}/sensor/api/stations`).pipe(
      catchError(this.handleError<Station[]>('getAllStations', []))
    );
  }

  getMeasurements(stationId: number): Observable<Measurement[]> {
    return this.http.get<Measurement[]>(`${this.baseUrl}/sensor/api/measurements?stationId=${stationId}`).pipe(
      catchError(this.handleError<Measurement[]>('getMeasurements', []))
    );
  }

  getLatestMeasurement(stationId: number): Observable<Measurement> {
    return this.http.get<Measurement>(`${this.baseUrl}/sensor/api/measurements/latest?stationId=${stationId}`).pipe(
      catchError(this.handleError<Measurement>('getLatestMeasurement'))
    );
  }

  addMeasurement(measurement: Partial<Measurement>): Observable<Measurement> {
    return this.http.post<Measurement>(`${this.baseUrl}/sensor/api/measurements`, measurement).pipe(
      catchError(this.handleError<Measurement>('addMeasurement'))
    );
  }

  // ========== QUALITY (stmodel-service) ==========
  getLatestQuality(stationId: number): Observable<QualityObservation> {
    return this.http.get<QualityObservation>(`${this.baseUrl}/stmodel/api/quality/latest?stationId=${stationId}`).pipe(
      catchError(this.handleError<QualityObservation>('getLatestQuality'))
    );
  }

  computeQuality(stationId: number): Observable<QualityObservation> {
    return this.http.post<QualityObservation>(`${this.baseUrl}/stmodel/api/quality/compute?stationId=${stationId}`, {}).pipe(
      catchError(this.handleError<QualityObservation>('computeQuality'))
    );
  }

  predictQuality(request: PredictionRequest): Observable<PredictionResponse> {
    return this.http.post<PredictionResponse>(`${this.baseUrl}/stmodel/api/quality/predict`, request);
  }

  // Récupérer l'historique des prédictions pour une station
  getPredictionHistory(stationId: number): Observable<PredictionHistory[]> {
    return this.http.get<PredictionHistory[]>(`${this.baseUrl}/stmodel/api/quality/history/${stationId}`).pipe(
      catchError(this.handleError<PredictionHistory[]>('getPredictionHistory', []))
    );
  }

  // Récupérer tout l'historique des prédictions
  getAllPredictionHistory(): Observable<PredictionHistory[]> {
    return this.http.get<PredictionHistory[]>(`${this.baseUrl}/stmodel/api/quality/history`).pipe(
      catchError(this.handleError<PredictionHistory[]>('getAllPredictionHistory', []))
    );
  }

  // ========== STATIONS CRUD ==========
  createStation(station: Partial<Station>): Observable<Station> {
    return this.http.post<Station>(`${this.baseUrl}/sensor/api/stations`, station).pipe(
      catchError(this.handleError<Station>('createStation'))
    );
  }

  updateStation(id: number, station: Partial<Station>): Observable<Station> {
    return this.http.put<Station>(`${this.baseUrl}/sensor/api/stations/${id}`, station).pipe(
      catchError(this.handleError<Station>('updateStation'))
    );
  }

  deleteStation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/sensor/api/stations/${id}`).pipe(
      catchError(this.handleError<void>('deleteStation'))
    );
  }

  getStationById(id: number): Observable<Station> {
    return this.http.get<Station>(`${this.baseUrl}/sensor/api/stations/${id}`).pipe(
      catchError(this.handleError<Station>('getStationById'))
    );
  }

  // ========== FORECAST ==========
  getLatestForecast(stationId: number): Observable<QualityForecast> {
    return this.http.get<QualityForecast>(`${this.baseUrl}/stmodel/api/forecast/simple?stationId=${stationId}`).pipe(
      catchError(this.handleError<QualityForecast>('getLatestForecast'))
    );
  }

  createForecast(stationId: number, horizonHours: number = 24): Observable<QualityForecast> {
    return this.http.post<QualityForecast>(
      `${this.baseUrl}/stmodel/api/forecast/create?stationId=${stationId}&horizonHours=${horizonHours}`, {}
    ).pipe(
      catchError(this.handleError<QualityForecast>('createForecast'))
    );
  }

  // ========== ALERTS ==========
  getActiveAlerts(): Observable<Alert[]> {
    return this.http.get<Alert[]>(`${this.baseUrl}/alerts/api/alerts/active`).pipe(
      catchError(this.handleError<Alert[]>('getActiveAlerts', []))
    );
  }

  getAlertsForStation(stationId: number): Observable<Alert[]> {
    return this.http.get<Alert[]>(`${this.baseUrl}/alerts/api/alerts/station/${stationId}/active`).pipe(
      catchError(this.handleError<Alert[]>('getAlertsForStation', []))
    );
  }

  acknowledgeAlert(alertId: number): Observable<Alert> {
    return this.http.patch<Alert>(`${this.baseUrl}/alerts/api/alerts/${alertId}/acknowledge`, {}).pipe(
      catchError(this.handleError<Alert>('acknowledgeAlert'))
    );
  }

  resolveAlert(alertId: number): Observable<Alert> {
    return this.http.patch<Alert>(`${this.baseUrl}/alerts/api/alerts/${alertId}/resolve`, {}).pipe(
      catchError(this.handleError<Alert>('resolveAlert'))
    );
  }

  // ========== SATELLITE ==========
  getSatelliteScenes(): Observable<SatelliteScene[]> {
    return this.http.get<SatelliteScene[]>(`${this.baseUrl}/satellite/api/scenes`).pipe(
      catchError(this.handleError<SatelliteScene[]>('getSatelliteScenes', []))
    );
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed:`, error);
      return of(result as T);
    };
  }
}

// ========== INTERFACES ==========
export interface StationOverview {
  stationId: number;
  code: string;
  name: string;
  latitude: number;
  longitude: number;
  currentScore: number | null;
  currentStatus: string | null;
  hasActiveAlert: boolean;
  latestAlertSeverity: string | null;
  lastMeasurementTime: string | null;
}

export interface StationDetail {
  stationId: number;
  code: string;
  name: string;
  latitude: number;
  longitude: number;
  commune: string;
  description: string;
  latestMeasurement: Measurement;
  latestObservation: QualityObservation;
  latestForecast: QualityForecast;
  activeAlerts: Alert[];
}

export interface Station {
  id: number;
  code: string;
  name: string;
  type: string;
  latitude: number;
  longitude: number;
  commune: string;
  description: string;
}

export interface Measurement {
  id: number;
  stationId: number;
  timestamp: string;
  ph: number;
  temperature: number;
  turbidity: number;
  dissolvedOxygen: number;
  conductivity: number;
}

export interface QualityObservation {
  id?: number;
  stationId: number;
  timestamp: string;
  score: number;
  status: string;
  details: string;
}

export interface QualityForecast {
  id?: number;
  stationId: number;
  forecastTime: string;
  createdAt: string;
  predictedScore: number;
  predictedStatus: string;
  modelName: string;
  confidence: number;
  lowerBound: number;
  upperBound: number;
}

export interface Alert {
  id: number;
  stationId: number;
  sourceType: string;
  sourceId: number;
  type: string;
  severity: string;
  status: string;
  title: string;
  message: string;
  score: number;
  eventTime: string;
  createdAt: string;
  resolvedAt: string | null;
}

export interface SatelliteScene {
  id: number;
  sceneId: string;
  satellite: string;
  acquisitionDate: string;
  cloudCover: number;
  boundingBox: string;
}

export interface PredictionRequest {
  stationId?: number;
  ph: number;
  temperature: number;
  turbidity: number;
  dissolvedOxygen: number;
  conductivity: number;
}

export interface PredictionResponse {
  stationId?: number;
  score: number;
  status: string;
  details: string;
  parameterScores: { [key: string]: number };
  recommendations: string[];
}

export interface PredictionHistory {
  id: number;
  stationId: number;
  predictionTimestamp: string;
  ph: number;
  temperature: number;
  turbidity: number;
  dissolvedOxygen: number;
  conductivity: number;
  score: number;
  status: string;
  details: string;
  parameterScores: { [key: string]: number };
  recommendations: string[];
}
