import { Component } from '@angular/core';

interface Sale {
  product: string;
  amount: number;
  date: Date;
  featured: boolean;
}

@Component({
  selector: 'app-sales-report',
  templateUrl: './sales-report.component.html',
  styles: [`
    /* TODO 6: Add styles for .featured and .high-value here */
    .featured   { /* TODO: yellow background */ }
    .high-value { /* TODO: red left border   */ }
    table { border-collapse: collapse; width: 100%; }
    th, td { padding: 8px 12px; border: 1px solid #ddd; text-align: left; }
  `]
})
export class SalesReportComponent {
  // TODO 1: Populate the sales array with at least 4 Sale objects.
  //         Include at least one with featured:true and one with amount > 1000.
  sales: Sale[] = [
    // { product: '...', amount: N, date: new Date('...'), featured: false },
  ];
}
