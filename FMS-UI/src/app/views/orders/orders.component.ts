import { Component, OnInit } from '@angular/core';
import { PortfolioService } from 'app/_services';
import { first } from 'rxjs/operators';
import { CdkTableModule} from '@angular/cdk/table';
import {DataSource} from '@angular/cdk/table';
import { formatCurrency, getCurrencySymbol } from '@angular/common';
@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss']
})
export class OrdersComponent implements OnInit {

  constructor(private portfolioService: PortfolioService) { }
  dataSource : any ;
  accountId = localStorage.getItem('AccountId');

  columnsToDisplay = ['Asset','Transaction','Quantity','Targetprice','Status'];
  ngOnInit() {

    this.portfolioService.getOrders(this.accountId)
      .subscribe(data =>{this.dataSource = data;
        for (var _i = 0; _i < this.dataSource.length; _i++) {
          this.dataSource[_i].Transaction= this.dataSource[_i].transactionType+' at '+ this.dataSource[_i].orderType;
if (this.dataSource[_i].limitPrice) 
 this.dataSource[_i].Targetprice=getCurrencySymbol(localStorage.getItem("AccountCurrency"),"wide")+this.dataSource[_i].limitPrice;
       this.dataSource[_i].Asset= this.dataSource[_i].asset.symbol;
    this.dataSource[_i].Status= this.dataSource[_i].status;
        this.dataSource[_i].Quantity= this.dataSource[_i].quantity;



  }

  console.log("success")});
  }

}
