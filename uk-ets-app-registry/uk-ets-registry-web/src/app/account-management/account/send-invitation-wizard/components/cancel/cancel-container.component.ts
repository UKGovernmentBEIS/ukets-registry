import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { take } from 'rxjs';
import { SharedModule } from '@registry-web/shared/shared.module';
import { SendInvitationActions } from '@send-invitation-wizard/store';

@Component({
  selector: 'app-send-invitation-container',
  template: `
    <app-cancel-update-request
      notification="Are you sure you want to cancel the send invitation request and return back to the account page?"
      (cancelRequest)="onCancel()"
    />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule],
  standalone: true,
})
export class CancelContainerComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.route.queryParams.pipe(take(1)).subscribe((params) => {
      this.store.dispatch(
        canGoBack({
          goBackRoute: params.goBackRoute,
          extras: { skipLocationChange: true },
        })
      );
    });
  }

  onCancel(): void {
    this.store.dispatch(SendInvitationActions.CANCEL_REQUEST());
  }
}
