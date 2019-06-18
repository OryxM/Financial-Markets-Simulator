import { Component, OnDestroy,OnInit,Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { navItems } from '../../_nav';
import { AuthenticationService } from 'app/_services';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { formatCurrency, getCurrencySymbol } from '@angular/common';


@Component({
  selector: 'app-dashboard',
  templateUrl: './default-layout.component.html'
})
export class DefaultLayoutComponent implements OnInit,OnDestroy {
  public navItems = navItems;
  public sidebarMinimized = true;
  private changes: MutationObserver;
  public element: HTMLElement;
  public user: any;
  constructor(private authenticationService : AuthenticationService,@Inject(DOCUMENT) _document?: any) {

    this.changes = new MutationObserver((mutations) => {
      this.sidebarMinimized = _document.body.classList.contains('sidebar-minimized');
    });
    this.element = _document.body;
    this.changes.observe(<Element>this.element, {
      attributes: true,
      attributeFilter: ['class']
    });
    this.user = localStorage.getItem('Username');
  }
  trades: string[] = [];
 showNotifications: boolean = false;
 notificationCount: number=0;
 ws: any;
 disabled: boolean;
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
setConnected(connected) {
   this.disabled = connected;
   this.showNotifications = connected;

 }
 onLogout(){
     this.authenticationService.logout();
   }
   showTrades(message) {
    this.showNotifications = true;
    let rawTrade= JSON.parse(message);
    rawTrade.Time= (new Date(rawTrade.time)).toLocaleString('en-US');
    rawTrade.Asset= rawTrade.order.asset.symbol;
    rawTrade.Price= getCurrencySymbol(localStorage.getItem("AccountCurrency"),"wide")+rawTrade.price;
    rawTrade.Quantity= rawTrade.volume;
    rawTrade.Transaction= rawTrade.order.transactionType+' at '+ rawTrade.order.orderType;
    rawTrade.Commission= rawTrade.commission;
    this.trades.push(rawTrade);
    this.notificationCount++;
  }

   ngOnInit(){this.connect();}
  ngOnDestroy(): void {
    this.changes.disconnect();
  }
}
