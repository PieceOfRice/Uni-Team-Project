import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TeamPlayerService } from '../service/team-player.service';

import { TeamPlayerComponent } from './team-player.component';

describe('TeamPlayer Management Component', () => {
  let comp: TeamPlayerComponent;
  let fixture: ComponentFixture<TeamPlayerComponent>;
  let service: TeamPlayerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'team-player', component: TeamPlayerComponent }]), HttpClientTestingModule],
      declarations: [TeamPlayerComponent],
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
      .overrideTemplate(TeamPlayerComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeamPlayerComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TeamPlayerService);

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
    expect(comp.teamPlayers?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to teamPlayerService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTeamPlayerIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTeamPlayerIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
