import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BracketLineComponent } from './bracket-line.component';

describe('BracketLineComponent', () => {
  let component: BracketLineComponent;
  let fixture: ComponentFixture<BracketLineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BracketLineComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BracketLineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
