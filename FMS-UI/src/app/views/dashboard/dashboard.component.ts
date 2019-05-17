import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormControl } from '@angular/forms';
import { getStyle, hexToRgba } from '@coreui/coreui/dist/js/coreui-utilities';
import { CustomTooltips } from '@coreui/coreui-plugin-chartjs-custom-tooltips';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { formatCurrency, getCurrencySymbol } from '@angular/common';
import { ReplaySubject, Subject } from 'rxjs';
import {debounceTime, delay, tap, filter, map, takeUntil} from 'rxjs/operators';
import { PortfolioService } from 'app/_services';
@Component({
  templateUrl: 'dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})

export class DashboardComponent implements OnInit {
  constructor(private formBuilder: FormBuilder,private portfolioService:PortfolioService){}
  orderForm: FormGroup;
  error:any;
balance:any;
accountValue:any;
stockData:any;
customerData:any;
/** list of banks */
protected assets:any;

/** control for the selected bank for server side filtering */
public bankServerSideCtrl: FormControl = new FormControl();

/** control for filter for server side. */
public bankServerSideFilteringCtrl: FormControl = new FormControl();

/** indicate search operation is in progress */
public searching: boolean = false;

/** list of banks filtered after simulating server side search */
public  filteredServerSideBanks: ReplaySubject<any> = new ReplaySubject<any>(1);
  protected _onDestroy = new Subject<void>();

/** Subject that emits when the component has been destroyed. */




  ngOnInit(): void {
    this.orderForm = this.formBuilder.group({
      assetSymbol:[''],
                transactionType:['', Validators.required],
                quantity:['', Validators.required],
               orderType:['', Validators.required],
                limitPrice:['', Validators.required],
                stopPrice:['', Validators.required],
                duration:['', Validators.required]
    });
    this.portfolioService.getAssets()
    .subscribe(data =>{this.assets = data;
      for (var _i = 0; _i < this.assets.length; _i++) {
        this.assets[_i].bid= getCurrencySymbol(this.assets[_i].bid.currency,"wide")+this.assets[_i].bid.value;
          this.assets[_i].ask= getCurrencySymbol(this.assets[_i].ask.currency,"wide")+this.assets[_i].ask.value;
        }});

    // listen for search field value changes
    this.bankServerSideFilteringCtrl.valueChanges
      .pipe(
        filter(search => !!search),
        tap(() => this.searching = true),
        takeUntil(this._onDestroy),
        debounceTime(200),
        map(search => {
          if (!this.assets) {
            return [];
          }

          // simulate server fetching and filtering data
          return this.assets.filter(asset => asset.symbol.toLowerCase().indexOf(search) > -1);
        }),
        delay(500)
      )
      .subscribe(filteredBanks => {
        this.searching = false;
        this.filteredServerSideBanks.next(filteredBanks);
      },
        error => {
          // no errors in our simulated example
          this.searching = false;
          // handle error...
        });



     this.balance=getCurrencySymbol(localStorage.getItem("AccountCurrency"),"wide")+localStorage.getItem("Balance");
this.accountValue=getCurrencySymbol(localStorage.getItem("AccountCurrency"),"wide")+localStorage.getItem("Balance");
 }
 save() {
   this.orderForm.value.assetSymbol=this.bankServerSideCtrl.value.symbol;

        console.log(this.orderForm.value);
        this.portfolioService.order(this.orderForm.value)
   .subscribe(
       data => {
     console.log("data "+data);
     localStorage.setItem('error','false');

   },
   error=> {
 console.log("error "+error);
 this.error= true;

   });

}





   ngOnDestroy() {
     this._onDestroy.next();
     this._onDestroy.complete();
   }
}
