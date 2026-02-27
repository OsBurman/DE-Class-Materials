import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PostsService, Post } from '../../services/posts.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './post-detail.component.html',
  styleUrls: ['./post-detail.component.css'],
})
export class PostDetailComponent implements OnInit {

  private route = inject(ActivatedRoute);
  private postsService = inject(PostsService);

  // TODO 12: Declare a `post` property of type Post | undefined | null
  post: Post | undefined | null = undefined;

  ngOnInit(): void {
    // TODO 13: Read the 'id' route param (it comes as a string — convert to number with +)
    // const id = ...
    // Use postsService.getPostById(id) and assign to this.post
    // If no id is found, set this.post = null
  }
}
