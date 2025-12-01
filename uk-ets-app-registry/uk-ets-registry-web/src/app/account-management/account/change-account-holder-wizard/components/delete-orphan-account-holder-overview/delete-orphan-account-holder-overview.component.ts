import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-delete-orphan-account-holder-overview',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './delete-orphan-account-holder-overview.component.html',
})
export class DeleteOrphanAccountHolderOverviewComponent {
  readonly deleteAccountHolder = input.required<boolean>();
  readonly showChangeLink = input<boolean>(false);
  readonly goToChangeForm = output<void>();

  navigateToChangeForm() {
    this.goToChangeForm.emit();
  }
}
