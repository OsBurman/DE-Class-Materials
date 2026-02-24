import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { DataService, Post } from './data.service';

describe('DataService', () => {
  let service: DataService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      // HttpClientTestingModule replaces HttpClientModule and intercepts all HTTP calls
      imports: [HttpClientTestingModule],
      providers: [DataService]
    });

    service  = TestBed.inject(DataService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // verify() throws if any requests were made but not expected — catches accidental HTTP calls
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getPosts() should send GET /api/posts', () => {
    const mockPosts: Post[] = [
      { id: 1, title: 'Post 1', body: 'Body 1', userId: 1 },
      { id: 2, title: 'Post 2', body: 'Body 2', userId: 1 }
    ];

    let result: Post[] | undefined;
    // Subscribe BEFORE flushing — the Observable is cold and only created on subscribe
    service.getPosts().subscribe(posts => result = posts);

    // expectOne() asserts exactly one request was made to this URL and returns the TestRequest
    const req = httpMock.expectOne('/api/posts');
    expect(req.request.method).toBe('GET');

    // flush() provides the response data; Angular completes the Observable with this value
    req.flush(mockPosts);

    expect(result).toEqual(mockPosts);
  });

  it('getPost() should send GET /api/posts/:id', () => {
    const mockPost: Post = { id: 1, title: 'Post 1', body: 'Body 1', userId: 1 };

    let result: Post | undefined;
    service.getPost(1).subscribe(post => result = post);

    const req = httpMock.expectOne('/api/posts/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockPost);

    expect(result).toEqual(mockPost);
  });

  it('createPost() should send POST /api/posts with the correct body', () => {
    const newPost = { title: 'Test', body: 'Body', userId: 1 };
    const mockResponse: Post = { id: 10, ...newPost };

    let result: Post | undefined;
    service.createPost(newPost).subscribe(post => result = post);

    const req = httpMock.expectOne('/api/posts');
    expect(req.request.method).toBe('POST');
    // Inspect the request body to verify the correct data was sent
    expect(req.request.body.title).toBe('Test');
    req.flush(mockResponse);

    expect(result?.id).toBe(10);
  });

  it('should handle 404 error on getPost()', () => {
    let errorOccurred = false;

    service.getPost(999).subscribe({
      next: () => fail('Expected an error, not a value'),
      // The error callback fires when flush() simulates an HTTP error
      error: () => errorOccurred = true
    });

    const req = httpMock.expectOne('/api/posts/999');
    // Flush with a non-2xx status to simulate a server error response
    req.flush('Not found', { status: 404, statusText: 'Not Found' });

    expect(errorOccurred).toBeTrue();
  });
});
