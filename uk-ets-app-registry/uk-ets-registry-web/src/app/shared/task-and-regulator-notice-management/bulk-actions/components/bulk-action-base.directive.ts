import { computed, Directive, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder } from '@angular/forms';
import { Data, ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorSummary } from '@registry-web/shared/error-summary';
import { errors } from '@registry-web/shared/shared.action';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { selectItemTypeLabel } from '../store';

@Directive()
export abstract class BulkActionBaseDirective {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly formBuilder = inject(UntypedFormBuilder);
  protected readonly store = inject(Store);
  readonly itemTypeLabel = toSignal(this.store.select(selectItemTypeLabel));

  protected errorDetails: ErrorDetail[] = [];

  readonly form = this.formBuilder.group({ comment: [] });
  readonly selectedTasks = signal<any[]>([]);

  readonly requestIds = computed<string[]>(() =>
    this.selectedTasks().map((task) => task.requestId)
  );

  protected potentialErrors: Map<any, ErrorDetail>;

  constructor() {
    this.activatedRoute.data
      .pipe(takeUntilDestroyed())
      .subscribe((data: Data) => this.initData(data));
  }

  protected validateSelectedRequestItems() {
    if (this.requestIds()?.length === 0) {
      this.errorDetails.push({
        componentId: '',
        errorMessage: `No ${this.itemTypeLabel()} has been selected from the list to be claimed.`,
      });
    }
  }

  submit() {
    this.errorDetails = [];

    if (this.validate()) {
      this.doSubmit();
    } else {
      this.store.dispatch(
        errors({ errorSummary: new ErrorSummary(this.errorDetails) })
      );
    }
  }

  private initData(data: Data) {
    this.selectedTasks.set(data.selectedTasks);
    this.potentialErrors = data.errorMap;
  }

  abstract doSubmit(): void;

  abstract validate(): boolean;
}
