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

  // TODO 1: Implement ngOnChanges(changes: SimpleChanges).
  //         Angular calls this BEFORE ngOnInit, and again every time an @Input changes.
  //         Log: "[ngOnChanges] Input changed: " + changes['message'].currentValue
  ngOnChanges(changes: SimpleChanges): void {
    // TODO: log the change
  }

  // TODO 2: Implement ngOnInit().
  //         Angular calls this once, after the first ngOnChanges.
  //         Log: "[ngOnInit] Component initialized. message = " + this.message
  //         Set this.initTime = new Date().toLocaleTimeString()
  ngOnInit(): void {
    // TODO: log and set initTime
  }

  // TODO 3: Implement ngAfterViewInit().
  //         Angular calls this after the component's view (and child views) are rendered.
  //         Log: "[ngAfterViewInit] View is fully initialized"
  //         Set this.viewReady = true
  ngAfterViewInit(): void {
    // TODO: log and set viewReady
  }

  // TODO 4: Implement ngOnDestroy().
  //         Angular calls this just before the component is removed from the DOM.
  //         This is where you cancel timers, unsubscribe from Observables, etc.
  //         Log: "[ngOnDestroy] Component is being destroyed"
  //         Set this.cleanupDone = true
  ngOnDestroy(): void {
    // TODO: log and set cleanupDone
  }
}
