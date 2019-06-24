import { Component, OnInit } from '@angular/core';
import { PortfolioService } from 'app/_services';
import { first } from 'rxjs/operators';
import { CdkTableModule} from '@angular/cdk/table';
import {DataSource} from '@angular/cdk/table';

import { formatCurrency, getCurrencySymbol } from '@angular/common';

@Component({
  selector: 'app-trades',
  templateUrl: './trades.component.html',
  styleUrls: ['./trades.component.scss']
})
export class TradesComponent implements OnInit {

    constructor(private portfolioService: PortfolioService) { }
  dataSource : any ;


  columnsToDisplay = ['Time','Asset','Transaction','Price','Volume','Commission'];





  ngOnInit() {

console.log(localStorage.getItem('AccountId'));
   this.portfolioService.getTransactions(localStorage.getItem('AccountId'))
     .subscribe(data =>{this.dataSource = data;
       for (var _i = 0; _i < this.dataSource.length; _i++) {
         this.dataSource[_i].Time= (new Date(this.dataSource[_i].time)).toLocaleString('en-US');
         this.dataSource[_i].Asset= this.dataSource[_i].order.asset.symbol;
         this.dataSource[_i].Price= getCurrencySymbol(localStorage.getItem("AccountCurrency"),"wide")+this.dataSource[_i].price;
         this.dataSource[_i].Volume= this.dataSource[_i].volume;
         this.dataSource[_i].Transaction= this.dataSource[_i].order.transactionType+' at '+ this.dataSource[_i].order.orderType;
         this.dataSource[_i].Commission= getCurrencySymbol(localStorage.getItem("AccountCurrency"),"wide")+this.dataSource[_i].commission;


 }
 console.log("success")});
  }

}
