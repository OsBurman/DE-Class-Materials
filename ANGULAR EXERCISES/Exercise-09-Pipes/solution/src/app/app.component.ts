import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AsyncPipe, CurrencyPipe, DatePipe, TitleCasePipe, UpperCasePipe } from '@angular/common';
import { of, delay } from 'rxjs';
import { SearchFilterPipe, Employee } from './pipes/search-filter.pipe';
import { TimeAgoPipe } from './pipes/time-ago.pipe';
import { TruncatePipe } from './pipes/truncate.pipe';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule, AsyncPipe, CurrencyPipe, DatePipe, TitleCasePipe, UpperCasePipe, SearchFilterPipe, TimeAgoPipe, TruncatePipe],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  searchQuery = '';

  employees: Employee[] = [
    { id: 1, name: 'Alice Johnson', department: 'engineering', role: 'Senior Frontend Developer', salary: 125000, startDate: new Date(Date.now() - 1000 * 60 * 30), bio: 'Alice has 8 years of experience building performant web applications with Angular and React. She leads the frontend guild and mentors junior developers.' },
    { id: 2, name: 'Bob Martinez', department: 'product', role: 'Product Manager', salary: 110000, startDate: new Date(Date.now() - 1000 * 60 * 60 * 3), bio: 'Bob bridges the gap between engineering and business. He has shipped over 20 major product features in 5 years.' },
    { id: 3, name: 'Carol Smith', department: 'engineering', role: 'Backend Engineer', salary: 115000, startDate: new Date(Date.now() - 1000 * 60 * 60 * 25), bio: 'Carol specializes in Java Spring Boot microservices and has a strong background in distributed systems architecture.' },
    { id: 4, name: 'David Lee', department: 'design', role: 'UX Designer', salary: 95000, startDate: new Date(Date.now() - 1000 * 60 * 60 * 24 * 3), bio: 'David creates intuitive user experiences through research-driven design. Proficient in Figma, Sketch, and usability testing.' },
    { id: 5, name: 'Emma Wilson', department: 'marketing', role: 'Marketing Lead', salary: 88000, startDate: new Date(Date.now() - 1000 * 60 * 60 * 24 * 15), bio: 'Emma drives demand generation and brand awareness. She grew organic traffic by 300% in her first year.' },
    { id: 6, name: 'Frank Chen', department: 'engineering', role: 'DevOps Engineer', salary: 118000, startDate: new Date(Date.now() - 1000 * 60 * 60 * 24 * 45), bio: 'Frank manages CI/CD pipelines, Kubernetes clusters, and cloud infrastructure on AWS and GCP.' },
    { id: 7, name: 'Grace Kim', department: 'hr', role: 'HR Manager', salary: 85000, startDate: new Date(Date.now() - 1000 * 60 * 60 * 24 * 90), bio: 'Grace oversees recruiting, onboarding, and company culture initiatives. Employee satisfaction scores improved 40% under her leadership.' },
    { id: 8, name: 'Henry Brown', department: 'engineering', role: 'Data Engineer', salary: 122000, startDate: new Date(Date.now() - 1000 * 60 * 60 * 24 * 200), bio: 'Henry builds data pipelines and warehouses using Apache Spark and dbt. He ensures clean, reliable data for business intelligence.' },
  ];

  // Async data using Observable with delay to simulate HTTP call
  employees$ = of(this.employees).pipe(delay(1200));
}
