import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { LocationStrategy, HashLocationStrategy } from '@angular/common';
import {FormsModule, ReactiveFormsModule }    from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';
import { PERFECT_SCROLLBAR_CONFIG } from 'ngx-perfect-scrollbar';
import { PerfectScrollbarConfigInterface } from 'ngx-perfect-scrollbar';
import { AlertConfig } from 'ngx-bootstrap/alert';

import { MatCardModule } from '@angular/material/card';

import { MatButtonModule,MatSelectModule, MatFormFieldModule, MatInputModule, MatRippleModule,MatTableModule } from '@angular/material';
import {MatRadioModule} from '@angular/material/radio';
import {CdkTableModule} from '@angular/cdk/table';
import {MatMenuModule} from '@angular/material/menu';
import {MatDialogModule} from '@angular/material';
import { AlertModule } from 'ngx-bootstrap';


const DEFAULT_PERFECT_SCROLLBAR_CONFIG: PerfectScrollbarConfigInterface = {
  suppressScrollX: true
};
export function getAlertConfig(): AlertConfig {
  return Object.assign(new AlertConfig(), { type: 'success' });
}
import { AppComponent } from './app.component';

// Import containers
import { DefaultLayoutComponent } from './containers';

import { P404Component } from './views/error/404.component';
import { P500Component } from './views/error/500.component';
import { LoginComponent } from './views/login/login.component';
import { RegisterComponent } from './views/register/register.component';
import { JwtInterceptor, ErrorInterceptor } from './_interceptors';
import { AuthGuard } from './_guards';
import { AssetsComponent } from './views/assets/assets.component';
import { OrdersComponent } from './views/orders/orders.component';
import { OrderFormComponent} from './views/assets';
const APP_CONTAINERS = [
  DefaultLayoutComponent
];

import {
  AppAsideModule,
  AppBreadcrumbModule,
  AppHeaderModule,
  AppFooterModule,
  AppSidebarModule,
} from '@coreui/angular';

// Import routing module
import { AppRoutingModule } from './app.routing';

// Import 3rd party components
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { ChartsModule } from 'ng2-charts/ng2-charts';
import { TradesComponent } from './views/trades/trades.component';
@NgModule({
  imports: [
    BrowserModule,
BrowserAnimationsModule,
    AppRoutingModule,
    AppAsideModule,
    AppBreadcrumbModule.forRoot(),
    AppFooterModule,
    AppHeaderModule,
    AppSidebarModule,
    PerfectScrollbarModule,
    BsDropdownModule.forRoot(),
    TabsModule.forRoot(),
    ChartsModule,
FormsModule,
  HttpClientModule,
        ReactiveFormsModule,

MatInputModule,
MatSelectModule,
MatFormFieldModule,
MatButtonModule,
MatRippleModule,
MatTableModule,
MatDialogModule,
MatRadioModule,
MatMenuModule,
  BrowserModule,
AlertModule
  ],
  declarations: [
    AppComponent,

    ...APP_CONTAINERS,
    P404Component,
    P500Component,
    LoginComponent,
    RegisterComponent,
 AssetsComponent,
OrderFormComponent,
OrdersComponent,
TradesComponent

  ],
 entryComponents :[
      OrderFormComponent
    ],
  providers: [
{ provide: AlertConfig, useFactory: getAlertConfig },
 { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
      { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
AuthGuard,
{
    provide: LocationStrategy,
    useClass: HashLocationStrategy
  }],
  bootstrap: [ AppComponent ]

})
export class AppModule { }
