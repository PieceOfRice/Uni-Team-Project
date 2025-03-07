import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TournamentCreateComponent } from './tournament-create.component';

describe('TournamentCreateComponent', () => {
  let component: TournamentCreateComponent;
  let fixture: ComponentFixture<TournamentCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TournamentCreateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TournamentCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
