import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CounterPanelComponent } from '../components/counter-panel/counter-panel.component';
import { CounterService } from '../services/counter.service';

// TODO Task 6: Write component tests
describe('CounterPanelComponent', () => {
  let component: CounterPanelComponent;
  let fixture: ComponentFixture<CounterPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CounterPanelComponent],
      providers: [CounterService]
    }).compileComponents();

    fixture = TestBed.createComponent(CounterPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display initial count as 0', () => {
    // TODO: const el = fixture.nativeElement.querySelector('.count-display');
    // expect(el.textContent.trim()).toBe('0');
    expect(true).toBeTrue();
  });

  it('should increment count when increment button is clicked', () => {
    // TODO: Find the "+1" button using querySelector or debugElement
    // Click it, call fixture.detectChanges(), then assert count is 1
    expect(true).toBeTrue();
  });

  it('should reset count when reset button is clicked', () => {
    // TODO: Increment, then click reset, assert count is 0
    expect(true).toBeTrue();
  });
});
