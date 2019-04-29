import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { map } from 'rxjs/operators';
@Injectable({ providedIn: 'root' })
export class RegistrationService {
    constructor(private http: HttpClient) { }

   APIEndpoint = environment.APIEndpoint;
    register(email: string,username: string, password: string){
    return this.http.post(`${this.APIEndpoint}/fms/auth/signup`,{
      "username": username,
      "email":  email,
      "password": password}).pipe(map(data =>{console.log("user registered successfully")}));
        }

}
