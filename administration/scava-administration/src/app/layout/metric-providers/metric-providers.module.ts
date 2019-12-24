import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { MetricProvidersRoutingModule } from './metric-providers-routing.module';
import { MatTableModule, MatSortModule, MatFormFieldModule, MatListModule, MatInputModule } from '@angular/material';
import { MetricProvidersComponent } from './metric-providers.component';

@NgModule({
  imports: [
    CommonModule,
    MetricProvidersRoutingModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatListModule,
    MatSortModule,
    MatProgressSpinnerModule
  ],
  declarations: [
    MetricProvidersComponent
  ]
})
export class MetricProvidersModule { }
