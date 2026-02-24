import { TestBed } from '@angular/core/testing';
import { CounterService } from '../services/counter.service';

// TODO Task 5: Write service tests
describe('CounterService', () => {
  let service: CounterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CounterService);
  });

  // TODO Task 5: Implement each test
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start at 0', () => {
    // TODO: expect(service.count()).toBe(0);
    expect(true).toBeTrue(); // replace with real assertion
  });

  it('should increment by 1', () => {
    // TODO: Call service.increment() and assert count() === 1
    expect(true).toBeTrue();
  });

  it('should decrement by 1', () => {
    // TODO: Call service.decrement() and assert count() === -1
    expect(true).toBeTrue();
  });

  it('should reset to 0', () => {
    // TODO: Increment a few times, reset, assert count() === 0
    expect(true).toBeTrue();
  });

  it('should compute doubled value correctly', () => {
    // TODO: Set count to 5, assert doubled() === 10
    expect(true).toBeTrue();
  });

  it('should reflect isPositive correctly', () => {
    // TODO: Assert isPositive() is false at 0, true after increment
    expect(true).toBeTrue();
  });
});
