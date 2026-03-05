import { Component, Input } from '@angular/core';
import { BannerType } from './banner-type.enum';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-banner',
  templateUrl: './banner.component.html',
  styleUrls: ['./banner.component.scss'],
})
export class BannerComponent {
  @Input() readonly type: BannerType = BannerType.INFORMATION;
  @Input() readonly title: string;
  @Input() readonly text: string;
  @Input() readonly contentHtml: string;
  @Input() readonly link: string;
  @Input() readonly inline = true;

  readonly BannerType = BannerType;
}
