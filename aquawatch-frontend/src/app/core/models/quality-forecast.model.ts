export interface QualityForecast {
    stationId: number;
    forecastTime: string;
    predictedScore: number;
    predictedStatus: string;
    modelName: string;
    modelVersion: string;
}
