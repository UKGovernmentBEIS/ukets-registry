import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-about-trusted-account-list',
  templateUrl: './about-trusted-account-list.component.html',
})
export class AboutTrustedAccountListComponent {
  @Input() informativeText: boolean;
  @Input() isOHAOrAOHA: boolean;
}
