import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors } from '@registry-web/shared/shared.action';
import { Observable } from 'rxjs';
import { PublicationHistory, Section } from '@report-publication/model';
import {
  selectReportPublicationHistory,
  selectSection,
} from '@report-publication/selectors';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import { getUrlIdentifier } from '@shared/shared.util';
import { ActivatedRoute } from '@angular/router';
import {
  downloadFile,
  prepareNavigationLinksForWizards,
  selectedPublicationFile,
} from '@report-publication/actions';
import { Router } from '@angular/router';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { loadReportPublicationHistory } from '@report-publication/actions';

@Component({
  selector: 'app-section-details-container',
  template: ` <app-feature-header-wrapper>
      <app-section-details-header
        [section]="sectionDetails$ | async"
        [title]="selectTitle"
        [showBackToList]="true"
      >
      </app-section-details-header> </app-feature-header-wrapper
    ><app-section-details
      [section]="sectionDetails$ | async"
      [publicationHistory]="publicationHistory$ | async"
      [sortParameters]="sortParameters"
      (emitter)="onUnpublish($event)"
      (sort)="onSort($event)"
      (download)="onDownload($event)"
    ></app-section-details>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectionDetailsContainerComponent implements OnInit {
  sectionDetails$: Observable<Section>;
  publicationHistory$: Observable<PublicationHistory[]>;
  sortParameters: SortParameters = {
    sortField: 'applicableForYear',
    sortDirection: 'DESC',
  };
  potentialErrors: Map<any, ErrorDetail>;

  constructor(
    private readonly store: Store,
    private router: Router,
    private readonly activatedRoute: ActivatedRoute
  ) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));

    this.sectionDetails$ = this.store.select(selectSection);
    this.publicationHistory$ = this.store.select(
      selectReportPublicationHistory
    );

    this.store.dispatch(
      prepareNavigationLinksForWizards({
        routeSnapshotUrl: getUrlIdentifier(
          this.activatedRoute.snapshot['_routerState'].url
        ),
      })
    );
  }

  onUnpublish(file: PublicationHistory) {
    this.store.dispatch(
      selectedPublicationFile({
        id: file.id,
        fileName: file.fileName,
        fileYear: file.applicableForYear,
      })
    );
  }

  onDownload(id: number) {
    this.store.dispatch(downloadFile({ id }));
  }

  get selectTitle() {
    if (
      getUrlIdentifier(
        this.activatedRoute.snapshot['_routerState'].url
      ).includes('kp-report-publication')
    ) {
      return 'KP Report publication';
    }
    return 'ETS Report publication';
  }

  onSort(sortParameters: SortParameters) {
    this.sortParameters = sortParameters;
    this.store.dispatch(clearErrors());
    const payload = {
      sortParameters: sortParameters,
      potentialErrors: this.potentialErrors,
    };
    this.store.dispatch(loadReportPublicationHistory(payload));
  }
}
