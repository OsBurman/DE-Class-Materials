import { Component } from '@angular/core';

@Component({
  selector: 'app-user-panel',
  templateUrl: './user-panel.component.html'
})
export class UserPanelComponent {
  isLoggedIn: boolean = false;
  username: string = 'Alice';

  // TODO 1: Implement toggle() — flip this.isLoggedIn between true and false.
  toggle(): void {
    // TODO: toggle isLoggedIn
  }
}
