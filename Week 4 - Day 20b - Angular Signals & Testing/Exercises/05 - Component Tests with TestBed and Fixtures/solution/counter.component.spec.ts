import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CounterComponent } from './counter.component';
import { CounterService } from './counter.service';

describe('CounterComponent', () => {
  let fixture: ComponentFixture<CounterComponent>;
  let component: CounterComponent;
  let mockService: jasmine.SpyObj<CounterService>;

  beforeEach(async () => {
    // jasmine.createSpyObj creates a mock object with spy methods.
    // We set getCount to return 0 by default — tests can override this per-spec.
    mockService = jasmine.createSpyObj('CounterService', ['increment', 'decrement', 'getCount']);
    mockService.getCount.and.returnValue(0);

    await TestBed.configureTestingModule({
      // Standalone components go in 'imports', not 'declarations'
      imports: [CounterComponent],
      providers: [
        // Replace the real CounterService with the mock
        { provide: CounterService, useValue: mockService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CounterComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    // Basic sanity check — the component instance should be truthy
    expect(component).toBeTruthy();
  });

  it('should display the initial count as 0', () => {
    // detectChanges() triggers the initial change detection cycle (equivalent to ngOnInit + first render)
    fixture.detectChanges();
    const span: HTMLElement = fixture.nativeElement.querySelector('#count');
    expect(span.textContent).toBe('0');
  });

  it('should call service.increment() when Increment button is clicked', () => {
    fixture.detectChanges();
    const btn: HTMLButtonElement = fixture.nativeElement.querySelector('#btn-inc');
    btn.click();
    fixture.detectChanges();
    // toHaveBeenCalledTimes verifies the spy was called exactly once
    expect(mockService.increment).toHaveBeenCalledTimes(1);
  });

  it('should call service.decrement() when Decrement button is clicked', () => {
    fixture.detectChanges();
    const btn: HTMLButtonElement = fixture.nativeElement.querySelector('#btn-dec');
    btn.click();
    fixture.detectChanges();
    expect(mockService.decrement).toHaveBeenCalledTimes(1);
  });

  it('should display "positive" when count > 0', () => {
    // Override the mock's return value before detectChanges() re-evaluates the template
    mockService.getCount.and.returnValue(1);
    fixture.detectChanges();
    const sign: HTMLElement = fixture.nativeElement.querySelector('#sign');
    expect(sign.textContent).toBe('positive');
  });

  it('should display "negative/zero" when count <= 0', () => {
    // Default is already 0; this test documents the expected output clearly
    mockService.getCount.and.returnValue(0);
    fixture.detectChanges();
    const sign: HTMLElement = fixture.nativeElement.querySelector('#sign');
    expect(sign.textContent).toBe('negative/zero');
  });
});
