export interface Measurement {
    id: number;
    stationId: number;
    stationName: string;
    timestamp: string;
    ph: number;
    temperature: number;
    turbidity: number;
    dissolvedOxygen: number;
    conductivity: number;
}
