import { Component, OnInit } from '@angular/core';
import { PortfolioService } from 'app/_services';
import { first } from 'rxjs/operators';
import { CdkTableModule} from '@angular/cdk/table';
import {DataSource} from '@angular/cdk/table';

@Component({
  selector: 'app-trades',
  templateUrl: './trades.component.html',
  styleUrls: ['./trades.component.scss']
})
export class TradesComponent implements OnInit {

    constructor(private portfolioService: PortfolioService) { }
  dataSource : any ;
  userId = localStorage.getItem('UserId');

  columnsToDisplay = ['Time','Asset','Transaction','Price','Quantity','Commission',];
  ngOnInit() {

    this.portfolioService.getTransactions(this.userId)
      .subscribe(data =>{this.dataSource = data;
        for (var _i = 0; _i < this.dataSource.length; _i++) {
          this.dataSource[_i].Time= this.dataSource[_i].time;
 this.dataSource[_i].Asset= this.dataSource[_i].order.asset.symbol;
       this.dataSource[_i].Price= this.dataSource[_i].price;
    this.dataSource[_i].Quantity= this.dataSource[_i].volume;
      this.dataSource[_i].Transaction= this.dataSource[_i].order.transactionType+' at '+ this.dataSource[_i].order.orderType;
        this.dataSource[_i].Commission= this.dataSource[_i].commission;



  }

  console.log("success")});
  }

}
