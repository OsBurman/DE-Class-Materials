import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PostsService, Post } from '../../services/posts.service';

@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css'],
})
export class PostListComponent implements OnInit {

  // TODO 8: Inject PostsService, ActivatedRoute, and Router
  private postsService = inject(PostsService);
  // private route = inject(ActivatedRoute);
  // private router = inject(Router);

  // TODO 9: Declare searchQuery string property
  searchQuery = '';

  // TODO 10: Create a filteredPosts getter that filters posts by searchQuery
  get filteredPosts(): Post[] {
    // Filter this.postsService.getPosts() where
    // post.title.toLowerCase() includes searchQuery.toLowerCase()
    return this.postsService.getPosts();
  }

  ngOnInit(): void {
    // TODO 9: Read the 'search' query param from the URL and set searchQuery
    // HINT: this.route.snapshot.queryParamMap.get('search')
  }

  // TODO 11: Implement onSearch() — navigate to '/posts' with queryParams: { search: searchQuery }
  onSearch(): void {
    // your code here
  }
}
