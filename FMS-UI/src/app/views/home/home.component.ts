import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PortfolioService } from 'app/_services';
import { Router, ActivatedRoute } from '@angular/router';
import { first } from 'rxjs/operators';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { formatCurrency, getCurrencySymbol } from '@angular/common';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  accountForm: FormGroup;
  constructor(private formBuilder: FormBuilder,private route: ActivatedRoute,private router: Router,private portfolioService: PortfolioService) { }
  isCollapsed: boolean = false;
  iconCollapse: string = 'icon-arrow-up';
  dataSource : any ;
user:any;
accounts: string[] = [];
  showConversation: boolean = false;
  ws: any;
  name: string;
  disabled: boolean;
  columnsToDisplay = ['Id','Balance','Currency'];
  collapsed(event: any): void {
    // console.log(event);
  }

  expanded(event: any): void {
    // console.log(event);
  }

  toggleCollapse(): void {
    this.isCollapsed = !this.isCollapsed;
    this.iconCollapse = this.isCollapsed ? 'icon-arrow-down' : 'icon-arrow-up';
  }
  //websocket
  connect() {
      //connect to stomp where stomp endpoint is exposed

      let socket = new WebSocket("ws://localhost:8080/newlyCreatedAccounts");
      this.ws = Stomp.over(socket);
      let that = this;
      this.ws.connect({}, function(frame) {
        that.ws.subscribe("/errors", function(message) {
          alert("Error " + message.body);
        });
        that.ws.subscribe("/topic/"+localStorage.getItem('UserId'), function(message) {
          console.log(message)
          that.showAccounts(message.body);
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

    showAccounts(message) {
      this.showConversation = true;
      let account= JSON.parse(message);
      account.Id=account.id;
      account.Currency=account.currency;
      account.Balance=  getCurrencySymbol(account.currency,"wide")+account.balance;
      this.accounts.push(account);
    }
    setConnected(connected) {
      this.disabled = connected;
      this.showConversation = connected;

    }
    //
  ngOnInit() {
  this.user = localStorage.getItem('Username');
    this.accountForm = this.formBuilder.group({
        balance: ['', Validators.required],
        currency: ['', Validators.required]
    });
       this.connect();
       this.portfolioService.getAccounts(localStorage.getItem('UserId'))
         .subscribe(data =>{this.dataSource = data;
           for (var _i = 0; _i < this.dataSource.length; _i++) {
            this.dataSource[_i].Id=this.dataSource[_i].id;
            this.dataSource[_i].Currency=this.dataSource[_i].currency;
             this.dataSource[_i].Balance=  getCurrencySymbol(this.dataSource[_i].currency,"wide")+this.dataSource[_i].balance;
           }
         }
       );


  }
  changeAccount(id,balance,currency){
  localStorage.setItem("AccountId",id);
  localStorage.setItem("Balance",balance);
  localStorage.setItem("AccountValue",balance);
  localStorage.setItem("Currency",currency);
    this.router.navigate(['']);
  }
  onSubmit() {

         this.portfolioService.createAccount(this.accountForm.value)
          .pipe(first())
          .subscribe(
              data => {
            console.log(data);
              },
              error => {
                  console.log(error);
              });
  }

}
