import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  message: string = 'Hello from Parent';
  showChild: boolean = true;

  changeMessage(): void {
    this.message = 'Updated message at ' + new Date().toLocaleTimeString();
  }

  // Toggling showChild destroys the child component (ngOnDestroy) and re-creates it (ngOnChanges + ngOnInit)
  toggleChild(): void {
    this.showChild = !this.showChild;
  }
}
