import { Injectable } from '@angular/core';

// TODO 5: Define a Post interface with:
//   id: number, title: string, excerpt: string, content: string
//   author: string, date: string, tags: string[]
export interface Post {
  // your fields here
}

@Injectable({ providedIn: 'root' })
export class PostsService {

  // TODO 6: Create a private `posts` array with 6 sample blog posts.
  private posts: Post[] = [];

  // TODO 6: Implement getPosts() — returns all posts
  getPosts(): Post[] {
    return this.posts;
  }

  // TODO 7: Implement getPostById(id: number) — returns a post or undefined
  getPostById(id: number): Post | undefined {
    // your code here
    return undefined;
  }
}
