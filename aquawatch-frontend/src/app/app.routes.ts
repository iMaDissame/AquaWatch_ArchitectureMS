import { Routes } from '@angular/router';
import { StationMap } from './pages/station-map/station-map';
import { StationDetail } from './pages/station-detail/station-detail';

export const routes: Routes = [
    { path: '', redirectTo: 'stations', pathMatch: 'full' },
    { path: 'stations', component: StationMap },
    { path: 'stations/:id', component: StationDetail },
    { path: '**', redirectTo: 'stations' },
];
