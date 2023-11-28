import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBackInWizards, unpublishFile } from '@report-publication/actions';
import { Store } from '@ngrx/store';
import { selectPublicationFile } from '@report-publication/selectors';
import { Observable } from 'rxjs';
import { PublicationHistory } from '@report-publication/model';

@Component({
  selector: 'app-unpublish-file-container',
  template: `<app-unpublish-file
    [selectedPublicationFile]="selectedPublicationFile$ | async"
    (emitter)="onSubmit()"
  >
  </app-unpublish-file>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnpublishFileContainerComponent implements OnInit {
  constructor(private readonly store: Store) {}

  selectedPublicationFile$: Observable<PublicationHistory>;

  ngOnInit(): void {
    this.store.dispatch(canGoBackInWizards({}));
    this.selectedPublicationFile$ = this.store.select(selectPublicationFile);
  }

  onSubmit() {
    this.store.dispatch(unpublishFile());
  }
}
