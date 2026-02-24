import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  template: `
    <h2>HTTP Interceptor Demo</h2>
    <p>Open DevTools → Network and inspect request headers.</p>
    <pre>{{ response | json }}</pre>
  `,
})
export class AppComponent implements OnInit {
  response: any;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http
      .get('https://jsonplaceholder.typicode.com/posts/1')
      .subscribe(data => (this.response = data));
  }
}
