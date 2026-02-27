# Exercise 11 — RxJS & Observables

## 🎯 Learning Objectives
- Create Observables manually and with creation operators (`of`, `from`, `interval`, `fromEvent`)
- Use essential **RxJS operators**: `map`, `filter`, `tap`, `switchMap`, `mergeMap`, `debounceTime`, `distinctUntilChanged`, `catchError`, `takeUntilDestroyed`, `combineLatest`, `forkJoin`
- Use **`Subject`** and **`BehaviorSubject`** as event buses
- Implement a **live search** using `debounceTime` + `distinctUntilChanged` + `switchMap`
- Avoid **memory leaks** with `takeUntilDestroyed` and `async` pipe
- Understand **hot vs. cold** Observables

---

## 📋 What You're Building
A **Real-time Dashboard** with:
- A **live search** input that debounces requests (300ms) and cancels in-flight ones with `switchMap`
- A **notification center** using a `Subject` as a global event bus
- An **auto-refresh data table** using `interval` + `switchMap` to poll an API every 10 seconds
- A stock price ticker using `BehaviorSubject` that simulates fluctuating prices

---

## 🏗️ Project Setup
```bash
ng new exercise-11-rxjs --standalone --routing=false --style=css
cd exercise-11-rxjs
# Copy starter-code/src/app files into your src/app/
ng serve
```

---

## ✅ TODOs

### `services/notification.service.ts`
- [ ] **TODO 1**: Create a `private notifications$ = new Subject<Notification>()`
- [ ] **TODO 2**: Implement `send(notification: Notification)` that calls `notifications$.next(...)`
- [ ] **TODO 3**: Implement `getNotifications()` that returns `notifications$.asObservable()`

### `services/stock.service.ts`
- [ ] **TODO 4**: Create stocks as a `BehaviorSubject<Stock[]>`
- [ ] **TODO 5**: Implement `getStocks()` returning the Observable
- [ ] **TODO 6**: Simulate price fluctuation every 2 seconds using `interval(2000)` and `update(...)`

### `search/search.component.ts`
- [ ] **TODO 7**: Create a `FormControl` for the search input
- [ ] **TODO 8**: Set up the search pipeline: `valueChanges.pipe(debounceTime(300), distinctUntilChanged(), switchMap(term => searchService.search(term)))` and subscribe
- [ ] **TODO 9**: Use `takeUntilDestroyed()` to clean up the subscription

### `app.component.ts`
- [ ] **TODO 10**: Subscribe to `notificationService.getNotifications()` and keep last 5 in an array
- [ ] **TODO 11**: Subscribe to `stockService.getStocks()` and display them

---

## 💡 Key Concepts Reminder

```typescript
// Subject — manually push values
const subject = new Subject<string>();
subject.next('hello');
subject.asObservable().subscribe(v => console.log(v));

// BehaviorSubject — has a current value
const bs = new BehaviorSubject<number>(0);
bs.next(42);
console.log(bs.getValue()); // 42

// Live search pipeline
this.searchControl.valueChanges.pipe(
  debounceTime(300),           // wait 300ms after last keystroke
  distinctUntilChanged(),       // only emit if value changed
  switchMap(term =>            // cancel previous, start new request
    this.searchService.search(term).pipe(
      catchError(() => of([]))  // handle errors gracefully
    )
  ),
  takeUntilDestroyed()          // unsubscribe when component destroyed
).subscribe(results => this.results = results);
```
