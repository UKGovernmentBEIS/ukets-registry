import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBackInWizards, navigateTo } from '@report-publication/actions';
import { selectCurrentActivatedRoute } from '@shared/shared.selector';
import { Observable } from 'rxjs';
import {
  selectPublicationReportFile,
  selectPublicationReportFileYear,
} from '@report-publication/components/upload-publication-file/reducers/upload-publication-file.selector';
import { FileBase } from '@shared/model/file';
import { submitPublicationFileRequest } from '@report-publication/components/upload-publication-file/actions/upload-publication-file.actions';
import { Section } from "@report-publication/model";
import { selectSection } from "@report-publication/selectors";

@Component({
  selector: 'app-check-answers-and-submit-container',
  template: ` <app-check-anwers-and-submit
      [fileHeader]="fileHeader$ | async"
      [fileYear]="fileYear$ | async"
      [sectionDetails]="sectionDetails$ | async"
      (navigateToEmitter)="navigateTo($event)"
      (displayTypeEmitter)="canGoBack($event)"
      (submitRequest)="submitChanges()"
    >
    </app-check-anwers-and-submit>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckAnswersAndSubmitContainerComponent implements OnInit {
  currentActivatedRoute$: Observable<string>;
  fileHeader$: Observable<FileBase>;
  fileYear$: Observable<number>;
  sectionDetails$: Observable<Section>;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit(): void {
    this.currentActivatedRoute$ = this.store.pipe(
      select(selectCurrentActivatedRoute)
    );
    this.fileHeader$ = this.store.pipe(select(selectPublicationReportFile));
    this.fileYear$ = this.store.pipe(select(selectPublicationReportFileYear));
    this.sectionDetails$ = this.store.pipe(select(selectSection));
  }

  canGoBack(isOneFileDisplayType: boolean) {
    if (isOneFileDisplayType) {
      this.store.dispatch(
        canGoBackInWizards({
          specifyBackLink: '/upload-publication-file',
        })
      );
    }
    else {
      this.store.dispatch(
        canGoBackInWizards({
          specifyBackLink: '/upload-publication-file/add-publication-year',
        })
      );
    }
  }

  navigateTo(routePath: string) {
    this.store.dispatch(
      navigateTo({
        specifyLink: routePath,
        extras: { skipLocationChange: true },
      })
    );
  }

  submitChanges() {
    this.store.dispatch(submitPublicationFileRequest());
  }

  onCancel() {
    this.store.dispatch(
      canGoBackInWizards({
        specifyBackLink: '/upload-publication-file/check-answers-and-submit',
      })
    );
    this.store.dispatch(
      navigateTo({ specifyLink: '/upload-publication-file/cancel' })
    );
  }
}
