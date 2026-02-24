import { Component, OnInit } from '@angular/core';
import { PostService, Post } from './post.service';

@Component({
  selector: 'app-post-list',
  template: `
    <div *ngIf="error" style="color:red">{{ error }}</div>
    <button (click)="addPost()">Add Post</button>
    <ul>
      <li *ngFor="let p of posts">{{ p.title }}</li>
    </ul>
  `,
})
export class PostListComponent implements OnInit {
  posts: Post[] = [];
  error = '';

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.postService.getPosts().subscribe({
      next:  posts => (this.posts = posts),
      error: err   => (this.error = err.message),
    });
  }

  addPost(): void {
    const body: Partial<Post> = { title: 'My new post', body: 'Hello!', userId: 1 };
    this.postService.createPost(body).subscribe({
      next: post => (this.posts = [post, ...this.posts]),
    });
  }
}
