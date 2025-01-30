import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TournamentFormService } from './tournament-form.service';
import { TournamentService } from '../service/tournament.service';
import { ITournament } from '../tournament.model';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { PlayerDataService } from 'app/entities/player-data/service/player-data.service';

import { TournamentUpdateComponent } from './tournament-update.component';

describe('Tournament Management Update Component', () => {
  let comp: TournamentUpdateComponent;
  let fixture: ComponentFixture<TournamentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tournamentFormService: TournamentFormService;
  let tournamentService: TournamentService;
  let playerDataService: PlayerDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TournamentUpdateComponent],
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
      .overrideTemplate(TournamentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TournamentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tournamentFormService = TestBed.inject(TournamentFormService);
    tournamentService = TestBed.inject(TournamentService);
    playerDataService = TestBed.inject(PlayerDataService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call PlayerData query and add missing value', () => {
      const tournament: ITournament = { id: 456 };
      const creator: IPlayerData = { id: 58465 };
      tournament.creator = creator;

      const playerDataCollection: IPlayerData[] = [{ id: 93106 }];
      jest.spyOn(playerDataService, 'query').mockReturnValue(of(new HttpResponse({ body: playerDataCollection })));
      const additionalPlayerData = [creator];
      const expectedCollection: IPlayerData[] = [...additionalPlayerData, ...playerDataCollection];
      jest.spyOn(playerDataService, 'addPlayerDataToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tournament });
      comp.ngOnInit();

      expect(playerDataService.query).toHaveBeenCalled();
      expect(playerDataService.addPlayerDataToCollectionIfMissing).toHaveBeenCalledWith(
        playerDataCollection,
        ...additionalPlayerData.map(expect.objectContaining)
      );
      expect(comp.playerDataSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const tournament: ITournament = { id: 456 };
      const creator: IPlayerData = { id: 2542 };
      tournament.creator = creator;

      activatedRoute.data = of({ tournament });
      comp.ngOnInit();

      expect(comp.playerDataSharedCollection).toContain(creator);
      expect(comp.tournament).toEqual(tournament);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITournament>>();
      const tournament = { id: 123 };
      jest.spyOn(tournamentFormService, 'getTournament').mockReturnValue(tournament);
      jest.spyOn(tournamentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tournament });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tournament }));
      saveSubject.complete();

      // THEN
      expect(tournamentFormService.getTournament).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tournamentService.update).toHaveBeenCalledWith(expect.objectContaining(tournament));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITournament>>();
      const tournament = { id: 123 };
      jest.spyOn(tournamentFormService, 'getTournament').mockReturnValue({ id: null });
      jest.spyOn(tournamentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tournament: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tournament }));
      saveSubject.complete();

      // THEN
      expect(tournamentFormService.getTournament).toHaveBeenCalled();
      expect(tournamentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITournament>>();
      const tournament = { id: 123 };
      jest.spyOn(tournamentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tournament });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tournamentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePlayerData', () => {
      it('Should forward to playerDataService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(playerDataService, 'comparePlayerData');
        comp.comparePlayerData(entity, entity2);
        expect(playerDataService.comparePlayerData).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
