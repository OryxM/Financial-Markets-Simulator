import { Component, OnInit,Inject } from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { FormBuilder, FormGroup,FormControl, Validators } from '@angular/forms';
      import { CdkTableModule} from '@angular/cdk/table';
       import {DataSource} from '@angular/cdk/table';
import { PortfolioService } from 'app/_services';
import { first } from 'rxjs/operators';


@Component({
  selector: 'app-assets',
  templateUrl: './assets.component.html',
  styleUrls: ['./assets.component.css'],
 animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0', display: 'none'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})


export class AssetsComponent  implements OnInit{
    constructor(private portfolioService: PortfolioService,public dialog: MatDialog) {}
  dataSource : any ;

  columnsToDisplay = ['symbol','price'];
expandedElement: Asset | null;
selectedAsset : Asset;


ngOnInit() {

  this.portfolioService.getAssets()
    .subscribe(data =>{this.dataSource = data;
console.log("success")});
}
openDialog(asset): void {
  this.selectedAsset = asset;
  console.log(this.selectedAsset.id);
   const dialogRef = this.dialog.open(OrderFormComponent, {
     width: '300px',
     data: this.selectedAsset

   });
 }

 }

export interface Asset {
  id : string;
  symbol: string;
  price: number;
}

@Component({
  selector: 'app-order-form',
  templateUrl: './order-form.component.html',

})
export class OrderFormComponent implements OnInit {
  orderForm: FormGroup;
  constructor(private formBuilder: FormBuilder,
    private portfolioService: PortfolioService,
    public dialogRef: MatDialogRef<OrderFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {

        this.orderForm = formBuilder.group({
          assetSymbol:[''],
                    transactionType:['', Validators.required],
                    quantity:['', Validators.required],
                   orderType:['', Validators.required],
 marketPrice:[''],
                    limitPrice:['', Validators.required],
                    stopPrice:['', Validators.required],
                    duration:['', Validators.required]
        });

    }
 ngOnInit() {}
    save(assetSymbol,assetPrice) {
      this.dialogRef.close(this.orderForm.value);
      this.orderForm.value.assetSymbol=assetSymbol;
this.orderForm.value.marketPrice=assetPrice;
           console.log(this.orderForm.value);
           this.portfolioService.order(this.orderForm.value).pipe(first())
      .subscribe(
          data => {
        console.log("ok");
          },
          error => {
              console.log(error);
          });
}

  close() {
       this.dialogRef.close();
   }


  onNoClick(): void {
    this.dialogRef.close();
  }

}
