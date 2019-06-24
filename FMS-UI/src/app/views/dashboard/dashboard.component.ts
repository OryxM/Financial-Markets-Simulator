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
  error:boolean= false;
  message:boolean= false;
balance:any;
accountValue:any;
stockData:any;
customerData:any;
  dataSource : any;
  data:any;

    columnsToDisplay = ['Asset','Transaction','Price','CurrentPrice','Volume','Commission','Profit_Loss'];

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
    console.log(localStorage.getItem('AccountId'));
       this.dataSource=[];
       this.portfolioService.getTransactions(localStorage.getItem('AccountId'))
         .subscribe(data =>{this.dataSource= data;
          /* for (var _i = 0; _i < this.data.length; _i++){
             if (this.data[_i].order.transactionType == 'BUY'){
             this.dataSource.push(this.data[_i]);

           }

         }*/

           for (var _i = 0; _i < this.dataSource.length; _i++) {


             this.dataSource[_i].Asset= this.dataSource[_i].order.asset.symbol;
             this.dataSource[_i].Price= getCurrencySymbol(localStorage.getItem("AccountCurrency"),"wide")+this.dataSource[_i].price;
             this.dataSource[_i].CurrentPrice= getCurrencySymbol(localStorage.getItem("AccountCurrency"),"wide")+this.dataSource[_i].currentPrice;
             this.dataSource[_i].Volume= this.dataSource[_i].volume;
             this.dataSource[_i].Transaction= this.dataSource[_i].order.transactionType+' at '+ this.dataSource[_i].order.orderType;
             this.dataSource[_i].Commission= getCurrencySymbol(localStorage.getItem("AccountCurrency"),"wide")+this.dataSource[_i].commission;
this.dataSource[_i].Profit_Loss=( Number(this.dataSource[_i].currentPrice) -  Number(this.dataSource[_i].price)).toFixed(2);
   }
    });
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
        this.message=true;
        console.log("data "+data);
        this.error= false;

   },
   error=> {
 console.log("error "+error);
 this.error= true;
 this.message= false

   });

}
refresh(){
  this.portfolioService.refresh(localStorage.getItem('AccountId'))
.subscribe(
 data => {// localStorage.setItem('Balance',data.balance);
 //localStorage.setItem('AccountId',data.AccountId);
console.log(data);
},
error=> {
console.log("error "+error);
});
}




   ngOnDestroy() {
     this._onDestroy.next();
     this._onDestroy.complete();
   }
}
