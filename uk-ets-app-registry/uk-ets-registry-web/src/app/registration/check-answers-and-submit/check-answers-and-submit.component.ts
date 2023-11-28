import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors } from 'src/app/shared/shared.action';
import { User } from '@shared/user';
import { submitRegistration } from '../registration.actions';
import { selectUser } from '../registration.selector';
import {
  selectBirthCountry,
  selectPhoneNumberFull,
  selectResidenceCountry,
  selectWorkCountry
} from './check-answers-and-submit.selectors';

@Component({
  selector: 'app-check-answers-and-submit',
  templateUrl: './check-answers-and-submit.component.html'
})
export class CheckAnswersAndSubmitComponent implements OnInit {
  user$ = this.store.select(selectUser);
  phoneNumberFull$ = this.store.select(selectPhoneNumberFull);
  residenceCountry$ = this.store.select(selectResidenceCountry);
  workCountry$ = this.store.select(selectWorkCountry);
  birthCountry$ = this.store.select(selectBirthCountry);

  readonly nextRoute = '/registration/registered';
  readonly previousRoute = '/registration/choose-password';

  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: this.previousRoute }));
    this.store.dispatch(clearErrors());
  }

  onSubmit(user: User) {
    this.store.dispatch(submitRegistration({ user }));
  }
}
