// article.model.ts — provided, do not modify
export interface Article {
  article_id: string;
  title: string;
  description: string | null;
  link: string;
  source_id: string;
  pubDate: string;
  image_url: string | null;
  category: string[];
}

export interface ApiResponse {
  status: string;
  totalResults: number;
  results: Article[];
}

// Mock articles for offline/development mode
export const MOCK_ARTICLES: Article[] = [
  { article_id: '1', title: 'Angular 17 Released', description: 'Angular team releases v17 with new control flow syntax and improved performance.', link: '#', source_id: 'angular.io', pubDate: '2024-01-15 10:00:00', image_url: null, category: ['technology'] },
  { article_id: '2', title: 'RxJS 7 Best Practices', description: 'A guide to writing clean, maintainable RxJS code in Angular applications.', link: '#', source_id: 'blog.dev', pubDate: '2024-01-14 14:30:00', image_url: null, category: ['technology'] },
  { article_id: '3', title: 'TypeScript 5.4 Features', description: 'Exploring the new features and improvements in TypeScript 5.4.', link: '#', source_id: 'typescript.org', pubDate: '2024-01-13 09:00:00', image_url: null, category: ['technology'] },
  { article_id: '4', title: 'Spring Boot 3.2 Overview', description: 'What is new in Spring Boot 3.2, including virtual threads support.', link: '#', source_id: 'spring.io', pubDate: '2024-01-12 11:00:00', image_url: null, category: ['technology'] },
];
