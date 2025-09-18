import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack, errors } from '@shared/shared.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { DeclarationComponent } from './declaration.component';
import { confirmDeclaration } from '../registration.actions';
import { selectDeclarationConfirmed } from '../registration.selector';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  imports: [CommonModule, DeclarationComponent],
  selector: 'app-declaration-container',
  template: `
    <app-declaration
      [confirmed]="declarationConfirmated$ | async"
      (confirm)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-declaration>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeclarationContainerComponent implements OnInit {
  readonly previousRoute = 'registration/choose-password';

  declarationConfirmated$ = this.store.select(selectDeclarationConfirmed);

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: this.previousRoute }));
  }

  onContinue(isConfirmed: boolean): void {
    this.store.dispatch(confirmDeclaration({ confirmed: isConfirmed }));
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
