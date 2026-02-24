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
    .featured   { background-color: #fffbcc; }       /* yellow highlight */
    .high-value { border-left: 4px solid crimson; }   /* red accent */
    table { border-collapse: collapse; width: 100%; }
    th, td { padding: 8px 12px; border: 1px solid #ddd; text-align: left; }
  `]
})
export class SalesReportComponent {
  sales: Sale[] = [
    { product: 'React Workshop',       amount: 1200, date: new Date('2026-02-10'), featured: true  },
    { product: 'Angular Basics',       amount:  450, date: new Date('2026-02-12'), featured: false },
    { product: 'Spring Boot Bootcamp', amount:  980, date: new Date('2026-02-14'), featured: true  },
    { product: 'Java Fundamentals',    amount: 2100, date: new Date('2026-02-16'), featured: false },
  ];
}
