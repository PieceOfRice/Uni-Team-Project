import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchSubmitComponent } from './match-submit.component';

describe('MatchSubmitComponent', () => {
  let component: MatchSubmitComponent;
  let fixture: ComponentFixture<MatchSubmitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MatchSubmitComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MatchSubmitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
