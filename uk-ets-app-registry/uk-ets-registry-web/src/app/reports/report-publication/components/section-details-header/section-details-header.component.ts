import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Section } from '@report-publication/model';

@Component({
  selector: 'app-section-details-header',
  templateUrl: './section-details-header.component.html',
  styleUrls: ['../../../../shared/sub-headers/styles/sub-header.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectionDetailsHeaderComponent {
  @Input() title: string;
  @Input() section: Section;
  @Input() showBackToList: boolean;
}
