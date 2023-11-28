import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { selectMessageId } from '@kp-administration/store';

@Component({
  selector: 'app-send-message-confirmation',
  templateUrl: './send-message-confirmation.component.html'
})
export class SendMessageConfirmationComponent implements OnInit {
  messageId$: Observable<number>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.messageId$ = this.store.select(selectMessageId);
  }
}
