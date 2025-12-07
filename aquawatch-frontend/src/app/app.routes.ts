import { Routes } from '@angular/router';
import { StationMap } from './pages/station-map/station-map';
import { StationDetail } from './pages/station-detail/station-detail';
import { DashboardComponent } from './pages/dashboard/dashboard';
import { StationAddComponent } from './pages/station-add/station-add';
import { PredictionComponent } from './pages/prediction/prediction';
import { StationListComponent } from './pages/station-list/station-list';
import { AlertsComponent } from './pages/alerts/alerts';

export const routes: Routes = [
    { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    { path: 'dashboard', component: DashboardComponent },
    { path: 'stations', component: StationMap },
    { path: 'stations/list', component: StationListComponent },
    { path: 'stations/add', component: StationAddComponent },
    { path: 'stations/:id', component: StationDetail },
    { path: 'prediction', component: PredictionComponent },
    { path: 'alerts', component: AlertsComponent },
    { path: '**', redirectTo: 'dashboard' },
];
