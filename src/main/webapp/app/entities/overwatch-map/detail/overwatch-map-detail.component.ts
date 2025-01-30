import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOverwatchMap } from '../overwatch-map.model';

@Component({
  selector: 'jhi-overwatch-map-detail',
  templateUrl: './overwatch-map-detail.component.html',
})
export class OverwatchMapDetailComponent implements OnInit {
  overwatchMap: IOverwatchMap | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ overwatchMap }) => {
      this.overwatchMap = overwatchMap;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
