import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TournamentAccessibilityDetailComponent } from './tournament-accessibility-detail.component';

describe('TournamentAccessibility Management Detail Component', () => {
  let comp: TournamentAccessibilityDetailComponent;
  let fixture: ComponentFixture<TournamentAccessibilityDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TournamentAccessibilityDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ tournamentAccessibility: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TournamentAccessibilityDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TournamentAccessibilityDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load tournamentAccessibility on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.tournamentAccessibility).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
