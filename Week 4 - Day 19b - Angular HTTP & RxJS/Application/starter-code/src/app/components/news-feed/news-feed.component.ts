import { Component, OnInit } from '@angular/core';
import { NewsService } from '../../services/news.service';

@Component({
  selector: 'app-news-feed',
  templateUrl: './news-feed.component.html',
})
export class NewsFeedComponent implements OnInit {
  selectedCategory = 'technology';

  // TODO Task 5: Inject NewsService
  constructor(public newsService: NewsService) {}

  ngOnInit(): void {
    // TODO Task 5: Call newsService.fetchArticles(this.selectedCategory) on init
  }

  onCategoryChange(category: string): void {
    this.selectedCategory = category;
    // TODO Task 5: Fetch articles for the new category
  }
}
