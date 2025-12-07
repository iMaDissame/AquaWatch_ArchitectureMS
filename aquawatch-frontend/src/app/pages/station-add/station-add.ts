import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-station-add',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './station-add.html',
  styleUrl: './station-add.scss'
})
export class StationAddComponent implements OnInit {
  stationForm!: FormGroup;
  loading = false;
  error: string | null = null;
  success: string | null = null;

  stationTypes = ['RIVER', 'RESERVOIR', 'COASTAL', 'WELL', 'LAKE'];

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.stationForm = this.fb.group({
      code: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(10)]],
      name: ['', [Validators.required, Validators.minLength(5)]],
      type: ['RIVER', Validators.required],
      latitude: ['', [Validators.required, Validators.min(-90), Validators.max(90)]],
      longitude: ['', [Validators.required, Validators.min(-180), Validators.max(180)]],
      commune: [''],
      description: ['']
    });
  }

  onSubmit(): void {
    if (this.stationForm.invalid) {
      this.markFormGroupTouched();
      return;
    }

    this.loading = true;
    this.error = null;
    this.success = null;

    const stationData = this.stationForm.value;

    this.apiService.createStation(stationData).subscribe({
      next: (result) => {
        this.success = `Station "${result.name}" créée avec succès (ID: ${result.id})`;
        this.loading = false;
        this.cdr.markForCheck();
        // Reset form
        this.stationForm.reset({ type: 'RIVER' });
        // Redirect after 2 seconds
        setTimeout(() => {
          this.router.navigate(['/stations']);
        }, 2000);
      },
      error: (err) => {
        console.error('Error creating station:', err);
        this.error = err.error?.message || 'Erreur lors de la création de la station';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  private markFormGroupTouched(): void {
    Object.keys(this.stationForm.controls).forEach(key => {
      this.stationForm.get(key)?.markAsTouched();
    });
  }

  isFieldInvalid(field: string): boolean {
    const control = this.stationForm.get(field);
    return control ? control.invalid && control.touched : false;
  }

  getFieldError(field: string): string {
    const control = this.stationForm.get(field);
    if (control?.errors) {
      if (control.errors['required']) return 'Ce champ est requis';
      if (control.errors['minlength']) return `Minimum ${control.errors['minlength'].requiredLength} caractères`;
      if (control.errors['maxlength']) return `Maximum ${control.errors['maxlength'].requiredLength} caractères`;
      if (control.errors['min']) return `Valeur minimum: ${control.errors['min'].min}`;
      if (control.errors['max']) return `Valeur maximum: ${control.errors['max'].max}`;
    }
    return '';
  }
}
