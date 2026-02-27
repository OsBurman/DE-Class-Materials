import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({ selector: 'app-about', standalone: true, imports: [],
  template: `<div><h1>About This Blog</h1><p>This is a demo blog built to teach Angular routing concepts. Built with Angular 17+ and standalone components.</p></div>`,
  styles: [`h1 { margin-bottom: 1rem; } p { color: #718096; font-size: 1.1rem; }`]
})
export class AboutComponent {}

