// Exercise 08: Classes in TypeScript — Access Modifiers and Decorators — SOLUTION
// Run with: npx ts-node --experimentalDecorators index.ts

// ── PART A: Access Modifiers & readonly ───────────────────────────────────────

class BankAccount {
  private balance: number;
  readonly accountNumber: string;
  protected category: string = "account";

  constructor(accountNumber: string, initialBalance: number = 0) {
    this.accountNumber = accountNumber;
    this.balance = initialBalance;
  }

  public deposit(amount: number): void {
    this.balance += amount;
  }

  public withdraw(amount: number): void {
    if (amount > this.balance) {
      console.log("Insufficient funds");
    } else {
      this.balance -= amount;
    }
  }

  public getBalance(): number {
    return this.balance;
  }
}

const account = new BankAccount("ACC-001");
account.deposit(500);
account.withdraw(200);
account.withdraw(1000); // "Insufficient funds"
console.log("Balance after deposits/withdrawals:", account.getBalance()); // 300

// Abstract class — defines a contract but cannot be instantiated
abstract class Shape {
  abstract area(): number;
  abstract perimeter(): number;

  describe(): string {
    return `Area: ${this.area().toFixed(2)}, Perimeter: ${this.perimeter().toFixed(2)}`;
  }
}

class Circle extends Shape {
  constructor(private radius: number) { super(); }

  area(): number    { return Math.PI * this.radius ** 2; }
  perimeter(): number { return 2 * Math.PI * this.radius; }
}

class Rectangle extends Shape {
  constructor(private width: number, private height: number) { super(); }

  area(): number      { return this.width * this.height; }
  perimeter(): number { return 2 * (this.width + this.height); }
}

const c = new Circle(5);
const r = new Rectangle(4, 6);
console.log("Circle:", c.describe());
console.log("Rectangle:", r.describe());

// protected — accessible in subclasses but not outside
class SavingsAccount extends BankAccount {
  describe(): string {
    return "SavingsAccount category: " + this.category; // `category` is protected
  }
}

const savings = new SavingsAccount("SAV-001", 1000);
console.log(savings.describe());

// ── PART B: Decorators ────────────────────────────────────────────────────────

// Class decorator — called with the constructor; seals the class and its prototype
function Sealed(constructor: Function) {
  Object.seal(constructor);
  Object.seal(constructor.prototype);
}

@Sealed
class Config {
  version = "1.0";
}

// Method decorator — replaces the method with a wrapper that logs before calling
function Log(
  _target: object,
  propertyKey: string,
  descriptor: PropertyDescriptor
): PropertyDescriptor {
  const original = descriptor.value;
  descriptor.value = function (...args: unknown[]) {
    console.log(`Calling ${propertyKey}`);
    return original.apply(this, args);
  };
  return descriptor;
}

class Greeter {
  @Log
  sayHello(): void {
    console.log("Hello from Greeter!");
  }
}

const greeter = new Greeter();
greeter.sayHello();
