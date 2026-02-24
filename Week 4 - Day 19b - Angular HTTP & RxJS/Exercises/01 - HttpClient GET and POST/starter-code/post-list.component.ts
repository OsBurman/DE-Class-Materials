import { Component, OnInit } from '@angular/core';
// TODO: import PostService and Post from './post.service'

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
  posts: any[] = [];
  error = '';

  // TODO: inject PostService

  ngOnInit(): void {
    // TODO: call this.postService.getPosts().subscribe(...)
    //   on next:  set this.posts
    //   on error: set this.error to err.message
  }

  addPost(): void {
    const body = { title: 'My new post', body: 'Hello!', userId: 1 };
    // TODO: call this.postService.createPost(body).subscribe(...)
    //   on next: this.posts = [post, ...this.posts]
  }
}
