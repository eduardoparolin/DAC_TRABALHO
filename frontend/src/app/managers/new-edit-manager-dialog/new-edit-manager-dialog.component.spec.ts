import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewEditManagerDialogComponent } from './new-edit-manager-dialog.component';

describe('NewEditManagerDialogComponent', () => {
  let component: NewEditManagerDialogComponent;
  let fixture: ComponentFixture<NewEditManagerDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewEditManagerDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewEditManagerDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
