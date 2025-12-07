export interface Alert {
    id: number;
    stationId: number;
    sourceType: string;
    sourceId: number;
    type: string;
    severity: string;   // 'INFO' | 'WARNING' | 'CRITICAL'
    status: string;     // 'OPEN' ...
    title: string;
    message: string;
    score: number | null;
    eventTime: string;
    createdAt: string;
    resolvedAt: string | null;
}
