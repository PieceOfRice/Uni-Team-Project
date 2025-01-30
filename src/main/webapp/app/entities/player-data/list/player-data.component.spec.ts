import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PlayerDataService } from '../service/player-data.service';

import { PlayerDataComponent } from './player-data.component';

describe('PlayerData Management Component', () => {
  let comp: PlayerDataComponent;
  let fixture: ComponentFixture<PlayerDataComponent>;
  let service: PlayerDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'player-data', component: PlayerDataComponent }]), HttpClientTestingModule],
      declarations: [PlayerDataComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(PlayerDataComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlayerDataComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PlayerDataService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.playerData?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to playerDataService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getPlayerDataIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getPlayerDataIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
