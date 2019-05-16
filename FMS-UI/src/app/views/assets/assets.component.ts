import { Component, OnInit,Inject } from '@angular/core';
import { formatCurrency, getCurrencySymbol } from '@angular/common';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { FormBuilder, FormGroup,FormControl, Validators } from '@angular/forms';
      import { CdkTableModule} from '@angular/cdk/table';
       import {DataSource} from '@angular/cdk/table';
import { PortfolioService } from 'app/_services';
import { getStyle, hexToRgba } from '@coreui/coreui/dist/js/coreui-utilities';
import { CustomTooltips } from '@coreui/coreui-plugin-chartjs-custom-tooltips';
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

  columnsToDisplay = ['symbol','bid','ask'];
expandedElement: Asset | null;
selectedAsset : Asset;



  /* tslint:disable:max-line-length */
  public chartLabels: Array<any> = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'];
  /* tslint:enable:max-line-length */

  public chartColours: Array<any> = [
    {  backgroundColor: '#ddeedd',
      borderColor: getStyle('--info'),
       pointBackgroundColor: 'transparent',
       pointBorderColor: getStyle('--info'),
       pointHoverBackgroundColor: '#000000',
       pointHoverBorderColor: 'rgba(77,83,96,1)',
       borderWidth:'2'
    }
  ];
  public data : Array<any>=[];
  public chartLegend = false;
  public chartType = 'line';

  public chartData(asset): Array<any>{
  var prices :Array<any> =[];
  for (var i=0;i<10;i++){
    prices.push(asset.price[i].value)
  };
  let  chartdata: Array<any> = [
      {
        data: prices,
        label: 'Current'
      }
    ];
    return chartdata;
  }
  public chartOptions(asset){
    var max = asset.price[0].value;
    var min = asset.price[0].value;
    for (var i =1;i<10;i++){
      if (asset.price[i].value > max) {max = asset.price[i].value};
      if (asset.price[i].value< min){ min = asset.price[i].value};
    }

  let  chartoptions={
    responsive: true,
    tooltips: {
    enabled: true,
    custom: CustomTooltips,
    intersect: true,
    mode: 'nearest',
    position: 'nearest',
    callbacks: {
      labelColor: function(tooltipItem, chart) {
        return { backgroundColor: chart.data.datasets[tooltipItem.datasetIndex].borderColor };
      }
    }
  },
    maintainAspectRatio: false,
    scales: {
      xAxes: [{
        gridLines: {
          drawOnChartArea: false,
        },
        ticks: {
          callback: function(value: any) {
            return value.charAt(0);
          }
        }
      }],
    yAxes: [{
      gridLines: {
        drawOnChartArea: false,
      },
      ticks: {
        beginAtZero: false,
        maxTicksLimit: 7,
        //stepSize: (max-min)/10,
        max: max,
        min:min
      }
    }]
  },

  elements: {
    line: {
      borderWidth: 1
    },
    point: {
      radius: 1,
      hitRadius: 20,
      hoverRadius: 20,
    }
  },
  legend: {
    display: false
  }
};
    return chartoptions;
  }
  public random(min: number, max: number) {
     return Math.floor(Math.random() * (max - min + 1) + min);
   }

ngOnInit() {


  for (let i = 0; i <= 10; i++) {
      this.data.push(this.random(1916, 1921));
    }

  this.portfolioService.getAssets()
    .subscribe(data =>{this.dataSource = data;
      for (var _i = 0; _i < this.dataSource.length; _i++) {
        this.dataSource[_i].bid= getCurrencySymbol(this.dataSource[_i].bid.currency,"wide")+this.dataSource[_i].bid.value;
          this.dataSource[_i].ask= getCurrencySymbol(this.dataSource[_i].ask.currency,"wide")+this.dataSource[_i].ask.value;
        }
console.log("success")});
}
openDialog(asset): void {
  this.selectedAsset = asset;
  console.log(this.selectedAsset.id);
   const dialogRef = this.dialog.open(OrderFormComponent, {
  width: '240px',
     data: this.selectedAsset

   });
 }

 }

export interface Asset {
  id : string;
  symbol: string;
  price: number;
}
export interface Error {
  message : string;
  details: string;

}

@Component({
  selector: 'app-order-form',
  templateUrl: './order-form.component.html',

})
export class OrderFormComponent implements OnInit {
  orderForm: FormGroup;
  error:any;
  constructor(private formBuilder: FormBuilder,
    private portfolioService: PortfolioService,
    public dialogRef: MatDialogRef<OrderFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {

        this.orderForm = formBuilder.group({
          assetSymbol:[''],
                    transactionType:['', Validators.required],
                    quantity:['', Validators.required],
                   orderType:['', Validators.required],
                    limitPrice:['', Validators.required],
                    stopPrice:['', Validators.required],
                    duration:['', Validators.required]
        });

    }
 ngOnInit() {}
    save(assetSymbol) {
      this.orderForm.value.assetSymbol=assetSymbol;

           console.log(this.orderForm.value);
           this.portfolioService.order(this.orderForm.value)
      .subscribe(
          data => {
        console.log("data "+data);
        localStorage.setItem('error','false');

          this.dialogRef.close(this.orderForm.value);
      },
      error=> {
    console.log("error "+error);
    this.error= true;

      });

}

  close() {
       this.dialogRef.close();
   }


  onNoClick(): void {
    this.dialogRef.close();
  }

}
