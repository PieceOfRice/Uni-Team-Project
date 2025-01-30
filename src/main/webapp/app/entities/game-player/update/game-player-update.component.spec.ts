import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GamePlayerFormService } from './game-player-form.service';
import { GamePlayerService } from '../service/game-player.service';
import { IGamePlayer } from '../game-player.model';
import { IGame } from 'app/entities/game/game.model';
import { GameService } from 'app/entities/game/service/game.service';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { PlayerDataService } from 'app/entities/player-data/service/player-data.service';

import { GamePlayerUpdateComponent } from './game-player-update.component';

describe('GamePlayer Management Update Component', () => {
  let comp: GamePlayerUpdateComponent;
  let fixture: ComponentFixture<GamePlayerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let gamePlayerFormService: GamePlayerFormService;
  let gamePlayerService: GamePlayerService;
  let gameService: GameService;
  let playerDataService: PlayerDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GamePlayerUpdateComponent],
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
      .overrideTemplate(GamePlayerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GamePlayerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gamePlayerFormService = TestBed.inject(GamePlayerFormService);
    gamePlayerService = TestBed.inject(GamePlayerService);
    gameService = TestBed.inject(GameService);
    playerDataService = TestBed.inject(PlayerDataService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Game query and add missing value', () => {
      const gamePlayer: IGamePlayer = { id: 456 };
      const game: IGame = { id: 83934 };
      gamePlayer.game = game;

      const gameCollection: IGame[] = [{ id: 32667 }];
      jest.spyOn(gameService, 'query').mockReturnValue(of(new HttpResponse({ body: gameCollection })));
      const additionalGames = [game];
      const expectedCollection: IGame[] = [...additionalGames, ...gameCollection];
      jest.spyOn(gameService, 'addGameToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ gamePlayer });
      comp.ngOnInit();

      expect(gameService.query).toHaveBeenCalled();
      expect(gameService.addGameToCollectionIfMissing).toHaveBeenCalledWith(
        gameCollection,
        ...additionalGames.map(expect.objectContaining)
      );
      expect(comp.gamesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call PlayerData query and add missing value', () => {
      const gamePlayer: IGamePlayer = { id: 456 };
      const playerData: IPlayerData = { id: 71037 };
      gamePlayer.playerData = playerData;

      const playerDataCollection: IPlayerData[] = [{ id: 25818 }];
      jest.spyOn(playerDataService, 'query').mockReturnValue(of(new HttpResponse({ body: playerDataCollection })));
      const additionalPlayerData = [playerData];
      const expectedCollection: IPlayerData[] = [...additionalPlayerData, ...playerDataCollection];
      jest.spyOn(playerDataService, 'addPlayerDataToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ gamePlayer });
      comp.ngOnInit();

      expect(playerDataService.query).toHaveBeenCalled();
      expect(playerDataService.addPlayerDataToCollectionIfMissing).toHaveBeenCalledWith(
        playerDataCollection,
        ...additionalPlayerData.map(expect.objectContaining)
      );
      expect(comp.playerDataSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const gamePlayer: IGamePlayer = { id: 456 };
      const game: IGame = { id: 82253 };
      gamePlayer.game = game;
      const playerData: IPlayerData = { id: 88225 };
      gamePlayer.playerData = playerData;

      activatedRoute.data = of({ gamePlayer });
      comp.ngOnInit();

      expect(comp.gamesSharedCollection).toContain(game);
      expect(comp.playerDataSharedCollection).toContain(playerData);
      expect(comp.gamePlayer).toEqual(gamePlayer);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGamePlayer>>();
      const gamePlayer = { id: 123 };
      jest.spyOn(gamePlayerFormService, 'getGamePlayer').mockReturnValue(gamePlayer);
      jest.spyOn(gamePlayerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gamePlayer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gamePlayer }));
      saveSubject.complete();

      // THEN
      expect(gamePlayerFormService.getGamePlayer).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(gamePlayerService.update).toHaveBeenCalledWith(expect.objectContaining(gamePlayer));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGamePlayer>>();
      const gamePlayer = { id: 123 };
      jest.spyOn(gamePlayerFormService, 'getGamePlayer').mockReturnValue({ id: null });
      jest.spyOn(gamePlayerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gamePlayer: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gamePlayer }));
      saveSubject.complete();

      // THEN
      expect(gamePlayerFormService.getGamePlayer).toHaveBeenCalled();
      expect(gamePlayerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGamePlayer>>();
      const gamePlayer = { id: 123 };
      jest.spyOn(gamePlayerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gamePlayer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gamePlayerService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareGame', () => {
      it('Should forward to gameService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(gameService, 'compareGame');
        comp.compareGame(entity, entity2);
        expect(gameService.compareGame).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
