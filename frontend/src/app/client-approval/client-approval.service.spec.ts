import { TestBed } from '@angular/core/testing';

import { ClientApprovalService } from './client-approval.service';

describe('ClientApprovalService', () => {
  let service: ClientApprovalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClientApprovalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
