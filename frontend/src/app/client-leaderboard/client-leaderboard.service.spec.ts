import { TestBed } from '@angular/core/testing';

import { ClientLeaderboardService } from './client-leaderboard.service';

describe('ClientLeaderboardService', () => {
  let service: ClientLeaderboardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClientLeaderboardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
