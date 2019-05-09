import { Component, OnInit,Inject } from '@angular/core';
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
public mainChartElements = 10;
  public 
mainChartData1: Array<number> = [];
  public mainChartData2: Array<number> = [];
  public mainChartData3: Array<number> = [];

  public mainChartData: Array<any> = [
    {
      data: this.mainChartData1,
      label: 'Current'
    },
    {
      data: this.mainChartData2,
      label: 'Previous'
    },
    {
      data: this.mainChartData3,
      label: 'BEP'
    }
  ];
  /* tslint:disable:max-line-length */
  public mainChartLabels: Array<any> = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday', 'Monday', 'Tuesday', 'Wednesday'];
  /* tslint:enable:max-line-length */
  public mainChartOptions: any = {
    tooltips: {
      enabled: false,
      custom: CustomTooltips,
      intersect: true,
      mode: 'index',
      position: 'nearest',
      callbacks: {
        labelColor: function(tooltipItem, chart) {
          return { backgroundColor: chart.data.datasets[tooltipItem.datasetIndex].borderColor };
        }
      }
    },
    responsive: true,
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
        ticks: {
          beginAtZero: true,
          maxTicksLimit: 5,
          stepSize: Math.ceil(250 / 5),
          max: 250
        }
      }]
    },
    elements: {
      line: {
        borderWidth: 2
      },
      point: {
        radius: 0,
        hitRadius: 10,
        hoverRadius: 4,
        hoverBorderWidth: 3,
      }
    },
    legend: {
      display: false
    }
  };
  public mainChartColours: Array<any> = [
    { // brandInfo
      backgroundColor: hexToRgba(getStyle('--info'), 10),
      borderColor: getStyle('--info'),
      pointHoverBackgroundColor: '#fff'
    },
    { // brandSuccess
      backgroundColor: 'transparent',
      borderColor: getStyle('--success'),
      pointHoverBackgroundColor: '#fff'
    },
    { // brandDanger
      backgroundColor: 'transparent',
      borderColor: getStyle('--danger'),
      pointHoverBackgroundColor: '#fff',
      borderWidth: 1,
      borderDash: [8, 5]
    }
  ];
  public mainChartLegend = false;
  public mainChartType = 'line';

 public random(min: number, max: number) {
    return Math.floor(Math.random() * (max - min + 1) + min);
  }
ngOnInit() {
for (let i = 0; i <= this.mainChartElements; i++) {
      this.mainChartData1.push(this.random(50, 200));
      this.mainChartData2.push(this.random(80, 100));
      this.mainChartData3.push(65);
    };
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
