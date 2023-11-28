import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnInit
} from '@angular/core';
import { GovukTagColor } from '@shared/govuk-components/govuk-tag/govuk-tag-color.type';

@Component({
  selector: 'app-govuk-tag',
  template: `
    <strong class="govuk-tag" [ngClass]="color ? 'govuk-tag--' + color : null">
      <ng-content></ng-content>
    </strong>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GovukTagComponent {
  @Input() color: GovukTagColor;
}
