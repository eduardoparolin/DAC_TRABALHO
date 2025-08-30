import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientLeaderboardComponent } from './client-leaderboard.component';

describe('ClientLeaderboardComponent', () => {
  let component: ClientLeaderboardComponent;
  let fixture: ComponentFixture<ClientLeaderboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientLeaderboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientLeaderboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
