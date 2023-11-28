import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-token-change-link-expired-container',
  template: `
    <app-token-change-link-expired></app-token-change-link-expired>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TokenChangeLinkExpiredContainerComponent {}
