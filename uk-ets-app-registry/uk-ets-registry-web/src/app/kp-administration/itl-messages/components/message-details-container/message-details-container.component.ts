import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { MessageDetails } from '@kp-administration/itl-messages/model';
import { selectMessage } from '@kp-administration/store';

@Component({
  selector: 'app-message-details-container',
  template: `
    <app-feature-header-wrapper>
      <app-message-header
        [messageDetails]="messageDetails$ | async"
      ></app-message-header>
    </app-feature-header-wrapper>
    <h1 class="govuk-heading-xl">
      ITL message details
    </h1>
    <p class="govuk-body" *ngIf="messageDetails$ | async as messageDetails">
      {{ messageDetails.content }}
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MessageDetailsContainerComponent implements OnInit {
  messageDetails$: Observable<MessageDetails>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.messageDetails$ = this.store.select(selectMessage);
  }
}
