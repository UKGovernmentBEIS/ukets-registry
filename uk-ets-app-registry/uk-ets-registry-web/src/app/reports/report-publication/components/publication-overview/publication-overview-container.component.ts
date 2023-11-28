import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  loadReportPublicationSections,
  setSelectedId,
} from '@report-publication/actions';
import { selectReportPublicationSections } from '@report-publication/selectors';
import { Observable } from 'rxjs';
import { Section, SectionType } from '@report-publication/model';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import { errors } from '@registry-web/shared/shared.action';
import { Router } from '@angular/router';

@Component({
  selector: 'app-publication-overview-container',
  template: `<app-publication-overview
    [publicationOverviewHeader]="publicationOverviewHeader"
    [sections]="sections$ | async"
    (selectSection)="onSelectSection($event)"
    (errorDetails)="onError($event)"
  ></app-publication-overview>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PublicationOverviewContainerComponent implements OnInit {
  sections$: Observable<Section[]>;
  sections: Section[];
  publicationOverviewHeader: string;
  potentialErrors: Map<any, ErrorDetail>;
  sectionType: SectionType;
  constructor(private store: Store, private router: Router) {}

  ngOnInit() {
    if (this.router.url == '/reports/ets-report-publication') {
      this.publicationOverviewHeader = 'ETS Report Publication';
      this.sectionType = SectionType.ETS;
    } else {
      this.publicationOverviewHeader = 'KP Report Publication';
      this.sectionType = SectionType.KP;
    }
    this.store.dispatch(
      loadReportPublicationSections({ sectionType: this.sectionType })
    );
    this.sections$ = this.store.select(selectReportPublicationSections);
  }

  onSelectSection(id) {
    this.store.dispatch(
      setSelectedId({
        selectedId: id,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
