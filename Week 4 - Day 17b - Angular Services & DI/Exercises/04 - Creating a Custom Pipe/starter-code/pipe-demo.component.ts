import { Component } from '@angular/core';

@Component({
  selector: 'app-pipe-demo',
  templateUrl: './pipe-demo.component.html'
})
export class PipeDemoComponent {
  // TODO 5: Declare a 'descriptions' array with 4 strings of varying lengths.
  descriptions: string[] = [
    // 'The quick brown fox jumped over the lazy dog near the river bank.',
    // 'Short',
    // 'Angular pipes transform template values elegantly.',
    // 'Hi!',
  ];

  // TODO 6: Declare a 'phoneNumbers' array with 4 strings.
  //         Include valid 10-digit strings, already-formatted numbers, and an invalid one.
  phoneNumbers: string[] = [
    // '1234567890',
    // '(800) 555-0199',
    // '123-456',
    // '9876543210',
  ];
}
