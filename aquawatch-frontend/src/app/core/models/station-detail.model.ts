import { Alert } from "./alert.model";
import { Measurement } from "./measurement.model";
import { QualityForecast } from "./quality-forecast.model";
import { QualityObservation } from "./quality-observation.model";

export interface StationDetail {
    stationId: number;
    code: string;
    name: string;
    latitude: number | null;
    longitude: number | null;
    commune: string | null;
    description: string | null;

    latestMeasurement: Measurement | null;
    latestObservation: QualityObservation | null;
    latestForecast: QualityForecast | null;

    activeAlerts: Alert[];
}
