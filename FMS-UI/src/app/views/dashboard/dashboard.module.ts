import { CommonModule } from  '@angular/common';
import { NgModule } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { ChartsModule } from 'ng2-charts/ng2-charts';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ButtonsModule } from 'ngx-bootstrap/buttons';
import {CdkTableModule} from '@angular/cdk/table';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { AlertModule } from 'ngx-bootstrap';
import { DashboardComponent } from './dashboard.component';
import { DashboardRoutingModule } from './dashboard-routing.module';
import {MatIconModule} from '@angular/material/icon';
import { MatButtonModule,MatSelectModule, MatFormFieldModule, MatInputModule, MatRippleModule,MatTableModule } from '@angular/material';
import {MatRadioModule} from '@angular/material/radio';
import { GrdFilterPipe } from '../grd-filter.pipe';

@NgModule({
  imports: [
CommonModule,
    FormsModule,
ReactiveFormsModule,
    DashboardRoutingModule,
    ChartsModule,
NgxMatSelectSearchModule,
CdkTableModule,
MatRadioModule,
MatButtonModule,
AlertModule,
MatIconModule,
MatSelectModule,
 MatFormFieldModule, MatInputModule, MatRippleModule,MatTableModule,
    BsDropdownModule,
    ButtonsModule.forRoot()
  ],
  declarations: [ DashboardComponent,
GrdFilterPipe ]
})
export class DashboardModule { }
