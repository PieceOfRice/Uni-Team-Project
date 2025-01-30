import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GameFormService } from './game-form.service';
import { GameService } from '../service/game.service';
import { IGame } from '../game.model';
import { IOverwatchMap } from 'app/entities/overwatch-map/overwatch-map.model';
import { OverwatchMapService } from 'app/entities/overwatch-map/service/overwatch-map.service';
import { IMatch } from 'app/entities/match/match.model';
import { MatchService } from 'app/entities/match/service/match.service';

import { GameUpdateComponent } from './game-update.component';

describe('Game Management Update Component', () => {
  let comp: GameUpdateComponent;
  let fixture: ComponentFixture<GameUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let gameFormService: GameFormService;
  let gameService: GameService;
  let overwatchMapService: OverwatchMapService;
  let matchService: MatchService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GameUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(GameUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GameUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gameFormService = TestBed.inject(GameFormService);
    gameService = TestBed.inject(GameService);
    overwatchMapService = TestBed.inject(OverwatchMapService);
    matchService = TestBed.inject(MatchService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call overwatchMap query and add missing value', () => {
      const game: IGame = { id: 456 };
      const overwatchMap: IOverwatchMap = { id: 92032 };
      game.overwatchMap = overwatchMap;

      const overwatchMapCollection: IOverwatchMap[] = [{ id: 13620 }];
      jest.spyOn(overwatchMapService, 'query').mockReturnValue(of(new HttpResponse({ body: overwatchMapCollection })));
      const expectedCollection: IOverwatchMap[] = [overwatchMap, ...overwatchMapCollection];
      jest.spyOn(overwatchMapService, 'addOverwatchMapToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ game });
      comp.ngOnInit();

      expect(overwatchMapService.query).toHaveBeenCalled();
      expect(overwatchMapService.addOverwatchMapToCollectionIfMissing).toHaveBeenCalledWith(overwatchMapCollection, overwatchMap);
      expect(comp.overwatchMapsCollection).toEqual(expectedCollection);
    });

    it('Should call Match query and add missing value', () => {
      const game: IGame = { id: 456 };
      const match: IMatch = { id: 58148 };
      game.match = match;

      const matchCollection: IMatch[] = [{ id: 85200 }];
      jest.spyOn(matchService, 'query').mockReturnValue(of(new HttpResponse({ body: matchCollection })));
      const additionalMatches = [match];
      const expectedCollection: IMatch[] = [...additionalMatches, ...matchCollection];
      jest.spyOn(matchService, 'addMatchToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ game });
      comp.ngOnInit();

      expect(matchService.query).toHaveBeenCalled();
      expect(matchService.addMatchToCollectionIfMissing).toHaveBeenCalledWith(
        matchCollection,
        ...additionalMatches.map(expect.objectContaining)
      );
      expect(comp.matchesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const game: IGame = { id: 456 };
      const overwatchMap: IOverwatchMap = { id: 9524 };
      game.overwatchMap = overwatchMap;
      const match: IMatch = { id: 59744 };
      game.match = match;

      activatedRoute.data = of({ game });
      comp.ngOnInit();

      expect(comp.overwatchMapsCollection).toContain(overwatchMap);
      expect(comp.matchesSharedCollection).toContain(match);
      expect(comp.game).toEqual(game);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGame>>();
      const game = { id: 123 };
      jest.spyOn(gameFormService, 'getGame').mockReturnValue(game);
      jest.spyOn(gameService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ game });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: game }));
      saveSubject.complete();

      // THEN
      expect(gameFormService.getGame).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(gameService.update).toHaveBeenCalledWith(expect.objectContaining(game));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGame>>();
      const game = { id: 123 };
      jest.spyOn(gameFormService, 'getGame').mockReturnValue({ id: null });
      jest.spyOn(gameService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ game: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: game }));
      saveSubject.complete();

      // THEN
      expect(gameFormService.getGame).toHaveBeenCalled();
      expect(gameService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGame>>();
      const game = { id: 123 };
      jest.spyOn(gameService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ game });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gameService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareOverwatchMap', () => {
      it('Should forward to overwatchMapService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(overwatchMapService, 'compareOverwatchMap');
        comp.compareOverwatchMap(entity, entity2);
        expect(overwatchMapService.compareOverwatchMap).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareMatch', () => {
      it('Should forward to matchService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(matchService, 'compareMatch');
        comp.compareMatch(entity, entity2);
        expect(matchService.compareMatch).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
