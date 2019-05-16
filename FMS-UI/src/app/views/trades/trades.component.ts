import { Component, OnInit } from '@angular/core';
import { PortfolioService } from 'app/_services';
import { first } from 'rxjs/operators';
import { CdkTableModule} from '@angular/cdk/table';
import {DataSource} from '@angular/cdk/table';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { formatCurrency, getCurrencySymbol } from '@angular/common';

@Component({
  selector: 'app-trades',
  templateUrl: './trades.component.html',
  styleUrls: ['./trades.component.scss']
})
export class TradesComponent implements OnInit {

    constructor(private portfolioService: PortfolioService) { }
  dataSource : any ;
 trades: string[] = [];
  showConversation: boolean = false;
  ws: any;
  name: string;
  disabled: boolean;

  columnsToDisplay = ['Time','Asset','Transaction','Price','Quantity','Commission',];
connect() {
    //connect to stomp where stomp endpoint is exposed

    let socket = new WebSocket("ws://localhost:8089/transactions");
    this.ws = Stomp.over(socket);
    let that = this;
    this.ws.connect({}, function(frame) {
      that.ws.subscribe("/errors", function(message) {
        alert("Error " + message.body);
      });
      that.ws.subscribe("/topic/"+localStorage.getItem('AccountId'), function(message) {
        console.log(message)
        that.showTrades(message.body);
      });
      that.disabled = true;
    }, function(error) {
      alert("STOMP error " + error);
    });
  }

  disconnect() {
    if (this.ws != null) {
      this.ws.ws.close();
    }
    this.setConnected(false);
    console.log("Disconnected");
  }

  sendName() {
    let data = JSON.stringify({
      'name' : this.name
    })
    this.ws.send("/app/message", {}, data);
  }

  showTrades(message) {
    this.showConversation = true;
    let rawTrade= JSON.parse(message);
    rawTrade.Time= (new Date(rawTrade.time)).toLocaleString('en-US');
    rawTrade.Asset= rawTrade.order.asset.symbol;
    rawTrade.Price= getCurrencySymbol(localStorage.getItem("AccountCurrency"),"wide")+rawTrade.price;
    rawTrade.Quantity= rawTrade.volume;
    rawTrade.Transaction= rawTrade.order.transactionType+' at '+ rawTrade.order.orderType;
    rawTrade.Commission= rawTrade.commission;

    this.trades.push(rawTrade);
  }

  setConnected(connected) {
    this.disabled = connected;
    this.showConversation = connected;
    //this.trades = [];
  }
  ngOnInit() {
   this.connect();
   this.portfolioService.getTransactions(localStorage.getItem('AccountId'))
     .subscribe(data =>{this.dataSource = data;
       for (var _i = 0; _i < this.dataSource.length; _i++) {
         this.dataSource[_i].Time= (new Date(this.dataSource[_i].time)).toLocaleString('en-US');
         this.dataSource[_i].Asset= this.dataSource[_i].order.asset.symbol;
         this.dataSource[_i].Price= getCurrencySymbol(localStorage.getItem("AccountCurrency"),"wide")+this.dataSource[_i].price;
         this.dataSource[_i].Quantity= this.dataSource[_i].volume;
         this.dataSource[_i].Transaction= this.dataSource[_i].order.transactionType+' at '+ this.dataSource[_i].order.orderType;
         this.dataSource[_i].Commission= this.dataSource[_i].commission;

 }
 console.log("success")});
  }

}
