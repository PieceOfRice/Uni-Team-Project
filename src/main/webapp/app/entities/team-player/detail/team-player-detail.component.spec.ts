import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TeamPlayerDetailComponent } from './team-player-detail.component';

describe('TeamPlayer Management Detail Component', () => {
  let comp: TeamPlayerDetailComponent;
  let fixture: ComponentFixture<TeamPlayerDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TeamPlayerDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ teamPlayer: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TeamPlayerDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TeamPlayerDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load teamPlayer on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.teamPlayer).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
