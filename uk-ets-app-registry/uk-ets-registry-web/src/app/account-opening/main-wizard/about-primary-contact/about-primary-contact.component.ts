import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { selectAccountHolderWizardCompleted } from '@account-opening/account-holder/account-holder.selector';

@Component({
  selector: 'app-about-primary-contact',
  templateUrl: './about-primary-contact.component.html'
})
export class AboutPrimaryContactComponent implements OnInit {
  accountHolderCompleted$: Observable<boolean>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.accountHolderCompleted$ = this.store.select(
      selectAccountHolderWizardCompleted
    );
  }
}
