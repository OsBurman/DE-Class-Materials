import { Component, OnInit } from '@angular/core';
// TODO: import PostService and Post from './post.service'

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
  post: any = null;
  loading = true;
  error   = '';
  message = '';

  // TODO: inject PostService

  ngOnInit(): void {
    // TODO: call this.postService.getPost(1).subscribe(...)
    //   on next:  set this.post, set this.loading = false
    //   on error: set this.error, set this.loading = false
  }

  save(): void {
    // TODO: call this.postService.updatePost(this.post.id, this.post).subscribe(...)
    //   on next:  this.message = 'Saved!'
    //   on error: this.error = err.message
  }

  remove(): void {
    // TODO: call this.postService.deletePost(this.post.id).subscribe(...)
    //   on next: this.message = 'Deleted.'
  }
}
