import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, interval } from 'rxjs';

export interface Stock {
  symbol: string;
  name: string;
  price: number;
  change: number;
}

const INITIAL_STOCKS: Stock[] = [
  { symbol: 'ANGL', name: 'Angular Inc.',   price: 187.45, change: 0 },
  { symbol: 'RXJS', name: 'RxJS Corp.',     price: 62.10,  change: 0 },
  { symbol: 'TSLA', name: 'TypeScript Ltd', price: 312.80, change: 0 },
  { symbol: 'NGKS', name: 'NgRx Systems',   price: 44.20,  change: 0 },
  { symbol: 'SGNT', name: 'Signals Co.',    price: 99.99,  change: 0 },
];

@Injectable({ providedIn: 'root' })
export class StockService {
  private stocks$ = new BehaviorSubject<Stock[]>(INITIAL_STOCKS);

  constructor() {
    interval(2000).subscribe(() => {
      const updated = this.stocks$.getValue().map(stock => {
        const changePercent = (Math.random() - 0.48) * 0.02;
        const newPrice = +(stock.price * (1 + changePercent)).toFixed(2);
        const change = +(newPrice - stock.price).toFixed(2);
        return { ...stock, price: newPrice, change };
      });
      this.stocks$.next(updated);
    });
  }

  getStocks(): Observable<Stock[]> {
    return this.stocks$.asObservable();
  }
}
