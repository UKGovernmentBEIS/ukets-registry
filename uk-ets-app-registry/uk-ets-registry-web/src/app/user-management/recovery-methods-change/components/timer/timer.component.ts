import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter,
  signal,
  inject,
  DestroyRef,
} from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, interval, Subscription, timer } from 'rxjs';
import {
  map,
  takeWhile,
  startWith,
  switchMap,
  finalize,
  filter,
  takeUntil,
} from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-timer',
  template: `
    <div>
      <div *ngIf="timeRemaining() > 0">
        <p class="govuk-body govuk-!-font-weight-bold">
          Code expires in: {{ timeRemaining() | date: 'mm:ss' }}
        </p>
      </div>
    </div>
  `,
})
export class TimerComponent implements OnInit {
  @Input() expiresAt$: Observable<number>;
  @Output() expired = new EventEmitter<boolean>();
  timer$: Observable<number>;

  timeRemaining = signal(0);
  private destroyRef = inject(DestroyRef);

  ngOnInit() {
    this.triggerTimer();
  }

  triggerTimer() {
    this.expiresAt$
      .pipe(
        switchMap((expiresAt) => {
          const expiresAtDate = new Date().getTime() + expiresAt;

          return interval(1000).pipe(
            startWith(0),
            map(() => expiresAtDate - Date.now()),
            takeWhile((remainingTime) => remainingTime > 0, true)
          );
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (remainingTime) => {
          this.timeRemaining.set(remainingTime);
          if (remainingTime <= 0) {
            this.expired.emit(false);
          }
        },
        error: (err) => {
          console.error('Error in timer:', err);
        },
      });
  }

  isDateAfterNow = (end: number) => new Date() < new Date(end);
}
