import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { BannerType } from '@shared/banner/banner-type.enum';
import { SharedModule } from '@shared/shared.module';
import { selectBulkActionSuccess } from '@shared/task-and-regulator-notice-management/bulk-actions/store';

@Component({
  selector: 'app-bulk-action-success',
  templateUrl: './bulk-action-success.component.html',
  standalone: true,
  imports: [SharedModule, AsyncPipe],
})
export class BulkActionSuccessComponent {
  private readonly store = inject(Store);
  readonly bulkActionSuccess$ = this.store.select(selectBulkActionSuccess);

  readonly BannerType = BannerType;
}
