import { Component } from '@angular/core';

@Component({
  selector: 'app-user-panel',
  templateUrl: './user-panel.component.html'
})
export class UserPanelComponent {
  isLoggedIn: boolean = false;
  username: string = 'Alice';

  toggle(): void {
    this.isLoggedIn = !this.isLoggedIn;
  }
}
