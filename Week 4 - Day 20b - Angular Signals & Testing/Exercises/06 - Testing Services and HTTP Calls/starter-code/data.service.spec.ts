import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { DataService, Post } from './data.service';

describe('DataService', () => {
  let service: DataService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    // TODO: Configure TestBed with HttpClientTestingModule and DataService
    //   TestBed.configureTestingModule({
    //     imports: [HttpClientTestingModule],
    //     providers: [DataService]
    //   });

    // TODO: Inject the service with TestBed.inject(DataService)
    // TODO: Inject the controller with TestBed.inject(HttpTestingController)
  });

  afterEach(() => {
    // TODO: Call httpMock.verify() to ensure no unexpected HTTP requests were made
  });

  // Test 1
  it('should be created', () => {
    // TODO: Assert that service is truthy
  });

  // Test 2
  it('getPosts() should send GET /api/posts', () => {
    const mockPosts: Post[] = [
      { id: 1, title: 'Post 1', body: 'Body 1', userId: 1 },
      { id: 2, title: 'Post 2', body: 'Body 2', userId: 1 }
    ];

    // TODO: Call service.getPosts() and subscribe to capture the result
    // TODO: Use httpMock.expectOne('/api/posts') to get the pending request
    // TODO: Assert req.request.method === 'GET'
    // TODO: Flush with mockPosts: req.flush(mockPosts)
    // TODO: Assert the Observable emitted mockPosts
  });

  // Test 3
  it('getPost() should send GET /api/posts/:id', () => {
    const mockPost: Post = { id: 1, title: 'Post 1', body: 'Body 1', userId: 1 };

    // TODO: Call service.getPost(1), expect the URL '/api/posts/1', verify method, flush mockPost
  });

  // Test 4
  it('createPost() should send POST /api/posts with the correct body', () => {
    const newPost = { title: 'Test', body: 'Body', userId: 1 };
    const mockResponse: Post = { id: 10, ...newPost };

    // TODO: Call service.createPost(newPost)
    // TODO: expectOne('/api/posts')
    // TODO: Assert req.request.method === 'POST'
    // TODO: Assert req.request.body.title === 'Test'
    // TODO: Flush mockResponse
  });

  // Test 5
  it('should handle 404 error on getPost()', () => {
    let errorOccurred = false;

    // TODO: Call service.getPost(999), subscribe with an error callback that sets errorOccurred = true
    // TODO: expectOne('/api/posts/999')
    // TODO: Flush with error: req.flush('Not found', { status: 404, statusText: 'Not Found' })
    // TODO: Assert errorOccurred is true
  });
});
