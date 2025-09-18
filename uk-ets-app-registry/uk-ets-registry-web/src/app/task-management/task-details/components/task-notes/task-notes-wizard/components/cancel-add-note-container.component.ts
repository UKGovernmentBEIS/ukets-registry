import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import { SharedModule } from '@registry-web/shared/shared.module';
import { cancelAddNote } from '../../store/task-notes.actions';

@Component({
  standalone: true,
  selector: 'app-cancel-add-note-container',
  imports: [SharedModule],
  template: `
    <app-cancel-update-request
      notification="Are you sure you want to cancel the addition of the note?"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelAddNoteContainerComponent implements OnInit {
  goBackRoute: string;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.goBackRoute = params.goBackRoute;
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.goBackRoute,
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel() {
    this.store.dispatch(cancelAddNote());
  }
}
