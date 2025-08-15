import { TestBed } from '@angular/core/testing';

import { HttpClientMockService } from './http-client-mock.service';

describe('HttpClientMockService', () => {
  let service: HttpClientMockService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HttpClientMockService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
