import { Component, Input } from '@angular/core';
import { BannerType } from './banner-type.enum';

@Component({
  selector: 'app-banner',
  templateUrl: './banner.component.html',
  styleUrls: ['./banner.component.scss'],
})
export class BannerComponent {
  @Input()
  type: BannerType = BannerType.INFORMATION;
  @Input()
  title: string;
  @Input()
  text: string;
  @Input()
  contentHtml: string;
  @Input()
  link: string;
  @Input()
  inline = true;

  bannerType = BannerType;
}
