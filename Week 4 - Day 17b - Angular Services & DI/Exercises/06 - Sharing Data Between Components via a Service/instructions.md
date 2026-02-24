# Exercise 06: Sharing Data Between Components via a Service

## Objective
Use a shared Angular service with a `BehaviorSubject` to broadcast state changes so that sibling components (no parent↔child relationship) stay synchronized without prop drilling.

## Background
When two components need to share state but don't have a direct parent-child relationship, a shared service with a `BehaviorSubject` is the Angular pattern. `BehaviorSubject` is an RxJS observable that always holds a current value and replays it immediately to new subscribers. You are building a notification system: `NotificationSenderComponent` posts messages, and `NotificationBannerComponent` displays the latest one — both wired through a single `NotificationService`.

## Requirements

### `NotificationService`
1. Create `notification.service.ts` with `@Injectable({ providedIn: 'root' })`.
2. Declare a private `BehaviorSubject<string>` initialized to an empty string `''`:
   ```ts
   private messageSubject = new BehaviorSubject<string>('');
   ```
3. Expose a public read-only observable:
   ```ts
   message$ = this.messageSubject.asObservable();
   ```
4. Implement a `send(message: string)` method that calls `this.messageSubject.next(message)`.
5. Implement a `clear()` method that calls `this.messageSubject.next('')`.

### `NotificationSenderComponent`
1. Inject `NotificationService`.
2. Declare a `draftMessage = ''` property bound to a text input with `[(ngModel)]`.
3. Implement `send()` — calls `notificationService.send(this.draftMessage)` then clears `draftMessage`.
4. Add three "quick send" buttons with pre-set messages ("✅ Success", "⚠️ Warning", "❌ Error") that call `send(text)` directly.

### `NotificationBannerComponent`
1. Inject `NotificationService`.
2. In `ngOnInit`, subscribe to `notificationService.message$` and store the latest value in a `currentMessage` string property.
3. Display `currentMessage` in the template — show nothing (or a placeholder) when the string is empty.
4. Add a "Dismiss" button that calls `notificationService.clear()`.
5. In `ngOnDestroy`, unsubscribe to prevent memory leaks (store the subscription and call `.unsubscribe()`).

### `AppComponent`
Render both sibling components in its template (no nesting — they are peers).

## Hints
- Import `BehaviorSubject` from `'rxjs'`.
- `.asObservable()` hides the `.next()` method from external callers — good encapsulation.
- Declare the subscription: `private sub!: Subscription;` then `this.sub = ...subscribe(...)`.
- Remember to implement `OnDestroy` and call `this.sub.unsubscribe()` in `ngOnDestroy`.
- `FormsModule` is required for `[(ngModel)]` — add it to `AppModule` imports.

## Expected Output
```
┌── Notification Banner ────────────────────────────────────────┐
│  (empty on start)                                             │
└───────────────────────────────────────────────────────────────┘

┌── Notification Sender ────────────────────────────────────────┐
│  [Type a message _________]  [Send]                           │
│  [✅ Success]  [⚠️ Warning]  [❌ Error]                       │
└───────────────────────────────────────────────────────────────┘

(After clicking "✅ Success"):
Banner shows: "✅ Success"  [Dismiss]

(After clicking Dismiss):
Banner is empty again
```
