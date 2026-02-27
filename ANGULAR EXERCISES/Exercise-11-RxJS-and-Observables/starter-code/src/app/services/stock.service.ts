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

  // TODO 4: Create a private BehaviorSubject<Stock[]> initialized with INITIAL_STOCKS
  // private stocks$ = new BehaviorSubject<Stock[]>(INITIAL_STOCKS);

  constructor() {
    // TODO 5: Use interval(2000) to simulate price fluctuations every 2 seconds.
    //   Each tick: update the BehaviorSubject with stocks.map(stock => {
    //     const changePercent = (Math.random() - 0.48) * 0.02; // small random change
    //     const newPrice = +(stock.price * (1 + changePercent)).toFixed(2);
    //     const change = +(newPrice - stock.price).toFixed(2);
    //     return { ...stock, price: newPrice, change };
    //   })
  }

  // TODO 6: Implement getStocks(): Observable<Stock[]>
  //   Return this.stocks$.asObservable()
  getStocks(): Observable<Stock[]> {
    // your code here
    return new BehaviorSubject<Stock[]>([]).asObservable();
  }
}
