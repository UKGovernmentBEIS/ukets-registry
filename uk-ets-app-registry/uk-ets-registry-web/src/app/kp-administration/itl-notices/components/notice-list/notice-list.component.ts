import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { SortService } from '@shared/search/sort/sort.service';
import {
  noticeStatusMap,
  noticeTypeMap,
  Notice,
} from '@kp-administration/itl-notices/model/notice';
import { SortParameters } from '@shared/search/sort/SortParameters';

@Component({
  selector: 'app-notice-list',
  templateUrl: './notice-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [SortService],
})
export class NoticeListComponent {
  @Input() notices: Notice[];
  @Input() sortParameters: SortParameters;
  @Output() readonly sort = new EventEmitter<SortParameters>();

  noticeStatusMap = noticeStatusMap;
  noticeTypeMap = noticeTypeMap;
}
