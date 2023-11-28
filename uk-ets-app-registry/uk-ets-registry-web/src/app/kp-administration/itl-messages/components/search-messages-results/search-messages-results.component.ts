import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { MessageSearchResult } from '@kp-administration/itl-messages/model';

@Component({
  selector: 'app-search-messages-results',
  templateUrl: './search-messages-results.component.html',
})
export class SearchMessagesResultsComponent {
  @Input() results: MessageSearchResult[];
  @Input() sortParameters: SortParameters;
  @Output() readonly sort = new EventEmitter<SortParameters>();
}
