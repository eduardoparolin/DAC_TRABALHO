import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ClientApprovalService } from './client-approval.service';

describe('ClientApprovalService', () => {
  let service: ClientApprovalService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, MatDialogModule, MatSnackBarModule]
    });
    service = TestBed.inject(ClientApprovalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
