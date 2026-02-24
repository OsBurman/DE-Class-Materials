import { Component } from '@angular/core';

@Component({
  selector: 'app-binding-demo',
  templateUrl: './binding-demo.component.html'
})
export class BindingDemoComponent {
  // Properties used in the template
  username: string = 'Player One';
  score: number = 0;
  isDisabled: boolean = true;
  imageUrl: string = 'https://via.placeholder.com/80';
  imageAlt: string = 'Placeholder image';
  lastMessage: string = '';

  // TODO 1: Implement levelUp() — it should increment score by 10.
  levelUp(): void {
    // TODO: add 10 to this.score
  }

  // TODO 2: Implement reset() — it should reset score to 0 and username to 'Player One'.
  reset(): void {
    // TODO: restore default values
  }

  // TODO 3: Implement logMessage(msg: string) — it should set this.lastMessage = msg.
  logMessage(msg: string): void {
    // TODO: store the message
  }
}
