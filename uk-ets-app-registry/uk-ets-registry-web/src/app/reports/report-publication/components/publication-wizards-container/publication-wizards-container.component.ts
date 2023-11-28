import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectSection } from '@report-publication/selectors';
import { Observable } from 'rxjs';
import { Section } from '@report-publication/model';

@Component({
  selector: 'app-publication-wizards-container',
  template: `
    <app-feature-header-wrapper
      ><app-section-details-header
        [section]="sectionDetails$ | async"
        [showBackToList]="false"
      ></app-section-details-header>
    </app-feature-header-wrapper>
    <router-outlet></router-outlet>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PublicationWizardsContainerComponent implements OnInit {
  constructor(private readonly store: Store) {}

  sectionDetails$: Observable<Section>;

  ngOnInit(): void {
    this.sectionDetails$ = this.store.select(selectSection);
  }
}
