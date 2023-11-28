import { Component, Input } from '@angular/core';
import {
  Notice,
  noticeStatusMap,
  noticeTypeMap,
} from '@kp-administration/itl-notices/model';

@Component({
  selector: 'app-notice-details-header',
  templateUrl: './notice-details-header.component.html',
  styleUrls: ['../../../../shared/sub-headers/styles/sub-header.scss'],
})
export class NoticeDetailsHeaderComponent {
  @Input() notice: Notice;
  noticeStatusMap = noticeStatusMap;
  noticeTypeMap = noticeTypeMap;
}
