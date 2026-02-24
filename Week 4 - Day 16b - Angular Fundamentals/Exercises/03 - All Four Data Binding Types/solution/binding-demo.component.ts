import { Component } from '@angular/core';

@Component({
  selector: 'app-binding-demo',
  templateUrl: './binding-demo.component.html'
})
export class BindingDemoComponent {
  username: string = 'Player One';
  score: number = 0;
  isDisabled: boolean = true;
  imageUrl: string = 'https://via.placeholder.com/80';
  imageAlt: string = 'Placeholder image';
  lastMessage: string = '';

  // Event binding handler: called by (click)="levelUp()" in the template
  levelUp(): void {
    this.score += 10;
  }

  // Event binding handler: restores default state
  reset(): void {
    this.score = 0;
    this.username = 'Player One';
  }

  // Called via template reference variable: (click)="logMessage(messageInput.value)"
  logMessage(msg: string): void {
    this.lastMessage = msg;
  }
}
