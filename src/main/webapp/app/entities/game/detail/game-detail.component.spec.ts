import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GameDetailComponent } from './game-detail.component';

describe('Game Management Detail Component', () => {
  let comp: GameDetailComponent;
  let fixture: ComponentFixture<GameDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GameDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ game: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GameDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GameDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load game on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.game).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
