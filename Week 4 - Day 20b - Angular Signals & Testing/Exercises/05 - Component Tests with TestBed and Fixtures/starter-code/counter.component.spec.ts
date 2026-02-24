import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CounterComponent } from './counter.component';
import { CounterService } from './counter.service';

describe('CounterComponent', () => {
  let fixture: ComponentFixture<CounterComponent>;
  let component: CounterComponent;
  let mockService: jasmine.SpyObj<CounterService>;

  beforeEach(async () => {
    // TODO: Create a spy object for CounterService with methods 'increment', 'decrement', 'getCount'
    //       Use jasmine.createSpyObj('CounterService', ['increment', 'decrement', 'getCount'])
    //       and set mockService.getCount.and.returnValue(0) as the default

    // TODO: Configure TestBed:
    //   await TestBed.configureTestingModule({
    //     imports: [CounterComponent],               // standalone component goes in imports
    //     providers: [{ provide: CounterService, useValue: mockService }]
    //   }).compileComponents();

    // TODO: Create the component fixture:
    //   fixture = TestBed.createComponent(CounterComponent);
    //   component = fixture.componentInstance;
  });

  // Test 1
  it('should create the component', () => {
    // TODO: Assert that 'component' is truthy
  });

  // Test 2
  it('should display the initial count as 0', () => {
    // TODO: Call fixture.detectChanges() to trigger change detection
    // TODO: Query the DOM for '#count' and assert its textContent is '0'
  });

  // Test 3
  it('should call service.increment() when Increment button is clicked', () => {
    fixture.detectChanges();
    // TODO: Query '#btn-inc', call .click() on it, then call fixture.detectChanges()
    // TODO: Assert mockService.increment was called with expect(mockService.increment).toHaveBeenCalledTimes(1)
  });

  // Test 4
  it('should call service.decrement() when Decrement button is clicked', () => {
    fixture.detectChanges();
    // TODO: Same pattern for '#btn-dec' and mockService.decrement
  });

  // Test 5
  it('should display "positive" when count > 0', () => {
    // TODO: Set mockService.getCount.and.returnValue(1)
    // TODO: Call fixture.detectChanges()
    // TODO: Query '#sign' and assert textContent is 'positive'
  });

  // Test 6
  it('should display "negative/zero" when count <= 0', () => {
    // TODO: Ensure mockService.getCount returns 0 (already the default)
    // TODO: Call fixture.detectChanges()
    // TODO: Query '#sign' and assert textContent is 'negative/zero'
  });
});
