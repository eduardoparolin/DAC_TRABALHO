import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonIdentificationComponent } from './person-identification.component';

describe('PersonIdentificationComponent', () => {
  let component: PersonIdentificationComponent;
  let fixture: ComponentFixture<PersonIdentificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PersonIdentificationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PersonIdentificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
