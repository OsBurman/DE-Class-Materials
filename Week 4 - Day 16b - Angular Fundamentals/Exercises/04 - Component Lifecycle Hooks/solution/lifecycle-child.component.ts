import { Component, Input, OnInit, OnChanges, AfterViewInit, OnDestroy, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-lifecycle-child',
  templateUrl: './lifecycle-child.component.html'
})
export class LifecycleChildComponent implements OnInit, OnChanges, AfterViewInit, OnDestroy {

  @Input() message: string = '';

  initTime: string = '';
  viewReady: boolean = false;
  cleanupDone: boolean = false;

  // ngOnChanges fires BEFORE ngOnInit on creation, then again for every @Input change.
  // SimpleChanges maps input property names to a SimpleChange object with previousValue/currentValue.
  ngOnChanges(changes: SimpleChanges): void {
    console.log('[ngOnChanges] Input changed:', changes['message'].currentValue);
  }

  // ngOnInit fires once, after the first ngOnChanges. This is where you fetch initial data.
  ngOnInit(): void {
    console.log('[ngOnInit] Component initialized. message =', this.message);
    this.initTime = new Date().toLocaleTimeString();
  }

  // ngAfterViewInit fires after Angular renders this component's template AND all child templates.
  // Safe to interact with ViewChild references here.
  ngAfterViewInit(): void {
    console.log('[ngAfterViewInit] View is fully initialized');
    this.viewReady = true;
  }

  // ngOnDestroy fires just before Angular removes the component from the DOM.
  // Cancel intervals, unsubscribe from Observables, and release resources here.
  ngOnDestroy(): void {
    console.log('[ngOnDestroy] Component is being destroyed');
    this.cleanupDone = true;
  }
}
