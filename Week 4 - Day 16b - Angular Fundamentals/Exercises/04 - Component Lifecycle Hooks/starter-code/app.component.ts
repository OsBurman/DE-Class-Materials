import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  // TODO 5: Add a message property initialized to 'Hello from Parent'.
  message: string = 'Hello from Parent';

  // TODO 6: Add a showChild boolean property initialized to true.
  showChild: boolean = true;

  // TODO 7: Implement changeMessage() — set message to 'Updated message at ' + new Date().toLocaleTimeString()
  changeMessage(): void {
    // TODO: update this.message
  }

  // TODO 8: Implement toggleChild() — flip this.showChild between true and false.
  toggleChild(): void {
    // TODO: toggle this.showChild
  }
}
