import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';
@Injectable({ providedIn: 'root' })
export class AuthenticationService {
    constructor(private http: HttpClient) { }
    jwt: any;
   APIEndpoint = environment.APIEndpoint;
    login(email: string, password: string){
    return this.http.post<any>(`${this.APIEndpoint}/fms/auth/signin`,{
  "email":  email,
  "password": password}).pipe(map(token => {
                // login successful if there's a jwt token in the response
                if (token.type == 'Bearer' && token.token) {
                    // store user details and jwt token in local storage to keep user logged in between page refreshes
                       localStorage.setItem('Username',token.user.username);
		       localStorage.setItem('UserId',token.user.id);
                  
                       this.jwt = {'tokenType':token.type,'accessToken':token.token};
                    localStorage.setItem('currentUsersToken',JSON.stringify(this.jwt));
                    console.log('currentUsersToken:'+JSON.stringify(this.jwt));
                }

                return token;
            }));
    }


    logout() {
        // remove user from local storage to log user out
        localStorage.removeItem('currentUsersToken');
    }
}
