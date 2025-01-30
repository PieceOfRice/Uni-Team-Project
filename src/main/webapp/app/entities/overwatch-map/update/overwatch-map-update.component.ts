import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { OverwatchMapFormService, OverwatchMapFormGroup } from './overwatch-map-form.service';
import { IOverwatchMap } from '../overwatch-map.model';
import { OverwatchMapService } from '../service/overwatch-map.service';
import { MapMode } from 'app/entities/enumerations/map-mode.model';

@Component({
  selector: 'jhi-overwatch-map-update',
  templateUrl: './overwatch-map-update.component.html',
})
export class OverwatchMapUpdateComponent implements OnInit {
  isSaving = false;
  overwatchMap: IOverwatchMap | null = null;
  mapModeValues = Object.keys(MapMode);

  editForm: OverwatchMapFormGroup = this.overwatchMapFormService.createOverwatchMapFormGroup();

  constructor(
    protected overwatchMapService: OverwatchMapService,
    protected overwatchMapFormService: OverwatchMapFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ overwatchMap }) => {
      this.overwatchMap = overwatchMap;
      if (overwatchMap) {
        this.updateForm(overwatchMap);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const overwatchMap = this.overwatchMapFormService.getOverwatchMap(this.editForm);
    if (overwatchMap.id !== null) {
      this.subscribeToSaveResponse(this.overwatchMapService.update(overwatchMap));
    } else {
      this.subscribeToSaveResponse(this.overwatchMapService.create(overwatchMap));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOverwatchMap>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(overwatchMap: IOverwatchMap): void {
    this.overwatchMap = overwatchMap;
    this.overwatchMapFormService.resetForm(this.editForm, overwatchMap);
  }
}
