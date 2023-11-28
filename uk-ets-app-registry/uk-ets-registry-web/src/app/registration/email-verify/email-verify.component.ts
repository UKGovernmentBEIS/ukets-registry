import { Component, OnInit } from '@angular/core';
import { RegistrationService } from '../registration.service';
import { ActivatedRoute } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import {selectRegistrationConfigurationProperty} from "@shared/shared.selector";
import {Observable} from "rxjs";

@Component({
  selector: 'app-email-verify',
  templateUrl: './email-verify.component.html'
})
export class EmailVerifyComponent implements OnInit {
  submittedEmail: string;
  registered: boolean;

  serviceDeskWeb$: Observable<string>;

  constructor(
    private store: Store,
    private route: ActivatedRoute,
    public registrationService: RegistrationService
  ) {
    this.submittedEmail = route.snapshot.paramMap.get('submittedEmail');
    this.registrationService
      .registerUser(this.submittedEmail)
      .subscribe((result: boolean) => (this.registered = result));
  }

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.serviceDeskWeb$ = this.store.select(
      selectRegistrationConfigurationProperty,
      {
        property: 'registry.service.desk.web',
      }
    );
  }
}
