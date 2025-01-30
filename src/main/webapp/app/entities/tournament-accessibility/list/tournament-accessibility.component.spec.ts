import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TournamentAccessibilityService } from '../service/tournament-accessibility.service';

import { TournamentAccessibilityComponent } from './tournament-accessibility.component';

describe('TournamentAccessibility Management Component', () => {
  let comp: TournamentAccessibilityComponent;
  let fixture: ComponentFixture<TournamentAccessibilityComponent>;
  let service: TournamentAccessibilityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'tournament-accessibility', component: TournamentAccessibilityComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [TournamentAccessibilityComponent],
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
      .overrideTemplate(TournamentAccessibilityComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TournamentAccessibilityComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TournamentAccessibilityService);

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
    expect(comp.tournamentAccessibilities?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to tournamentAccessibilityService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTournamentAccessibilityIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTournamentAccessibilityIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
