import { Component } from '@angular/core';

@Component({
  selector: 'app-pipe-demo',
  templateUrl: './pipe-demo.component.html'
})
export class PipeDemoComponent {
  descriptions: string[] = [
    'The quick brown fox jumped over the lazy dog near the river bank.',
    'Short',
    'Angular pipes transform template values elegantly.',
    'Hi!',
  ];

  phoneNumbers: string[] = [
    '1234567890',
    '(800) 555-0199',
    '123-456',
    '9876543210',
  ];
}
