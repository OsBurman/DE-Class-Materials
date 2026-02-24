import { Component, Input } from '@angular/core';
import { Article } from '../../models/article.model';

// TODO Task 7: Build ArticleCardComponent
@Component({
  selector: 'app-article-card',
  templateUrl: './article-card.component.html',
})
export class ArticleCardComponent {
  // TODO Task 7: @Input() article: Article
  @Input() article!: Article;
}
