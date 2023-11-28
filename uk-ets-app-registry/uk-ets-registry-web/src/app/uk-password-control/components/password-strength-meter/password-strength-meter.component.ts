import {
  Component,
  Input,
  OnChanges,
  SimpleChanges,
  Output,
  EventEmitter,
  OnDestroy,
} from '@angular/core';
import { Store } from '@ngrx/store';
import { selectPasswordStrengthScore } from '@uk-password-control/store/reducers';
import {
  clearPasswordStrength,
  loadPasswordScore,
} from '@uk-password-control/store/actions';
import { Subscription } from 'rxjs';

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'password-strength-meter',
  templateUrl: './password-strength-meter.component.html',
  styleUrls: ['./password-strength-meter.component.scss'],
})
export class PasswordStrengthMeterComponent implements OnChanges, OnDestroy {
  @Input() password: string;

  @Input() minPasswordLength = 8;

  @Input() enableFeedback = false;

  @Input() colors: string[] = [];

  @Output() readonly strengthChange = new EventEmitter<number>();

  passwordStrength: number = null;

  feedback: { suggestions: string[]; warning: string } = null;

  private prevPasswordStrength = null;

  private subscription: Subscription = null;

  private defaultColours = [
    'darkred',
    'orangered',
    'orange',
    'yellowgreen',
    'green',
  ];

  constructor(private store: Store) {
    this.subscription = this.store
      .select(selectPasswordStrengthScore)
      .subscribe((score) => {
        this.passwordStrength = score;
        // Only emit the passwordStrength if it changed
        if (this.prevPasswordStrength !== this.passwordStrength) {
          this.strengthChange.emit(this.passwordStrength);
          this.prevPasswordStrength = this.passwordStrength;
        }
      });
  }

  ngOnDestroy(): void {
    this.store.dispatch(clearPasswordStrength());
    this.subscription.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.password) {
      this.calculatePasswordStrength();
    }
  }

  private calculatePasswordStrength() {
    // TODO validation logic optimization
    if (!this.password) {
      this.passwordStrength = null;
      this.store.dispatch(clearPasswordStrength());
    } else if (this.password && this.password.length < this.minPasswordLength) {
      this.passwordStrength = 0;
      this.store.dispatch(clearPasswordStrength());
    } else {
      this.store.dispatch(loadPasswordScore({ password: this.password }));
    }
  }

  getMeterFillColor(strength: number): string {
    if (!strength || strength < 0 || strength > 5) {
      return this.colors[0] ? this.colors[0] : this.defaultColours[0];
    }

    return this.colors[strength]
      ? this.colors[strength]
      : this.defaultColours[strength];
  }
}
