import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';
@Injectable({ providedIn: 'root' })
export class PortfolioService {
    constructor(private http: HttpClient) { }

   APIEndpoint = environment.APIEndpoint;
    getAssets(){
    return this.http.get(`${this.APIEndpoint}/fms/portfolio/assets`)
}
    order(form :any){
return this.http.post(`${this.APIEndpoint}/fms/portfolio/create-order`,{
      "assetSymbol": form.assetSymbol,

      "transactionType":form.transactionType,
      "quantity":form.quantity,
"orderType":form.orderType,
"limitPrice":form.limitPrice,
"stopPrice":form.stopPrice,
"duration":form.duration,
"accountId":localStorage.getItem('AccountId'),

}).pipe(
map(data =>{console.log(data)}));
        }
//
createAccount(form :any){
return this.http.post(`${this.APIEndpoint}/fms/portfolio/create-account`,{
  "userId":localStorage.getItem('UserId'),
  "balance": form.balance,
  "currency":form.currency
}
  ).pipe(
map(data =>{console.log(data)}));
    }
    //



  getOrders(accountId){
    return this.http.get(`${this.APIEndpoint}/fms/portfolio/orders/${accountId}`)
}
  getTransactions(accountId){
    return this.http.get(`${this.APIEndpoint}/fms/portfolio/transactions/${accountId}`)
}

getAccounts(userId){
    return this.http.get(`${this.APIEndpoint}/fms/portfolio/accounts/${userId}`)
}

}
