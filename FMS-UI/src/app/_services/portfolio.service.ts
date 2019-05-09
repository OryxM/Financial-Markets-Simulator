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
"marketPrice":form.marketPrice,
"limitPrice":form.limitPrice,
"stopPrice":form.stopPrice,
"duration":form.duration,
"userId":localStorage.getItem('UserId'),

}).pipe(map(data =>{console.log("order created successfully")}));
        }

  getOrders(userId){
    return this.http.get(`${this.APIEndpoint}/fms/portfolio/orders/${userId}`)
}
  getTransactions(userId){
    return this.http.get(`${this.APIEndpoint}/fms/portfolio/transactions/${userId}`)
}

}
