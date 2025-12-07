export interface StationOverview {
    stationId: number;
    code: string;
    name: string;
    latitude: number | null;
    longitude: number | null;
    currentScore: number | null;
    currentStatus: string | null;         // 'GOOD' | 'MODERATE' | 'BAD'
    hasActiveAlert: boolean;
    latestAlertSeverity: string | null;   // 'INFO' | 'WARNING' | 'CRITICAL'
    lastMeasurementTime: string | null;   // déjà formatté par map-service
}
