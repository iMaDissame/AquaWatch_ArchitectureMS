export interface QualityObservation {
    stationId: number;
    timestamp: string;
    score: number;
    status: string;   // 'GOOD' | 'MODERATE' | 'BAD'
    details: string;
}
