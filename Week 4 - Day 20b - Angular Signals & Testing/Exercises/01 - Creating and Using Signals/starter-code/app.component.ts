import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>Signal Counter</h1>

    <!-- TODO: Display the current value of the count signal using count() -->
    <p>Count: </p>

    <!-- TODO: Display the username signal using username() -->
    <p>Hello, </p>

    <div>
      <!-- TODO: Bind each button's click event to the correct method -->
      <button>Increment</button>
      <button>Decrement</button>
      <button>Reset</button>
    </div>

    <div>
      <input [(ngModel)]="inputName" placeholder="Enter name" />
      <!-- TODO: Bind the click event to setUsername(inputName) -->
      <button>Set Name</button>
    </div>
  `
})
export class AppComponent {
  // TODO: Create a writable signal called 'count' initialised to 0
  //       import signal from '@angular/core'

  // TODO: Create a writable signal called 'username' initialised to 'Guest'

  inputName = '';

  // TODO: Implement increment() — use .update() to add 1 to count
  increment() {

  }

  // TODO: Implement decrement() — use .update() to subtract 1, but clamp at 0 (never go negative)
  decrement() {

  }

  // TODO: Implement reset() — use .set() to return count to 0
  reset() {

  }

  // TODO: Implement setUsername(name: string) — use .set() to update the username signal
  setUsername(name: string) {

  }
}
