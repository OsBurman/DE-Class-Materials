import { Component, OnInit } from '@angular/core';
import { PostService, Post } from './post.service';

@Component({
  selector: 'app-post-editor',
  template: `
    <div *ngIf="loading">Loading...</div>
    <div *ngIf="error" style="color:red">{{ error }}</div>
    <div *ngIf="message" style="color:green">{{ message }}</div>

    <div *ngIf="!loading && post">
      <input [(ngModel)]="post.title" style="width:100%" />
      <br><br>
      <button (click)="save()">Save</button>
      <button (click)="remove()">Delete</button>
    </div>
  `,
})
export class PostEditorComponent implements OnInit {
  post: Post | null = null;
  loading = true;
  error   = '';
  message = '';

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.postService.getPost(1).subscribe({
      next:  post => { this.post = post; this.loading = false; },
      error: err  => { this.error = err.message; this.loading = false; },
    });
  }

  save(): void {
    if (!this.post) return;
    this.postService.updatePost(this.post.id, this.post).subscribe({
      next:  ()  => (this.message = 'Saved!'),
      error: err => (this.error = err.message),
    });
  }

  remove(): void {
    if (!this.post) return;
    this.postService.deletePost(this.post.id).subscribe({
      next: () => (this.message = 'Deleted.'),
    });
  }
}
