import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
} from '@angular/core';
import { merge, Observable, of, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { HttpParams } from '@angular/common/http';
import { ResultTemplateContext } from '@ng-bootstrap/ng-bootstrap/typeahead/typeahead-window';
import { NgbTypeaheadSelectItemEvent } from '@ng-bootstrap/ng-bootstrap';
import { UkFormControlComponent } from '../uk-form-control.component';
import { TypeAheadService } from '@shared/services/type-ahead.service';

@Component({
  selector: 'app-type-ahead',
  templateUrl: './type-ahead.component.html',
  styleUrls: ['./type-ahead.component.scss'],
})
export class TypeAheadComponent extends UkFormControlComponent {
  @Input()
  url?: string;
  @Input()
  params?: HttpParams;
  @Input()
  searchKey?: string;
  @Input()
  resultTemplate: TemplateRef<ResultTemplateContext>;
  @Input()
  placeHolder: string;
  @Input()
  inputFormatter: (item: any) => string;

  @Input() options?: any[] = [];

  @Output() readonly resultItem = new EventEmitter<any>();

  @Input() IsSelect: boolean;

  toggle$ = new Subject<boolean>();

  constructor(private typeAheadService: TypeAheadService) {
    super();
  }

  search = (text$: Observable<string | boolean>) => {
    const debouncedText$ = text$.pipe(
      debounceTime(200),
      distinctUntilChanged()
    );
    const inputToggle$ = this.toggle$;

    return merge(debouncedText$, inputToggle$).pipe(
      switchMap((term: string) => {
        if (this.params) {
          if (term.length < 3) {
            return [];
          } else {
            this.params = this.params.set(this.searchKey, term);
            const typeAheadService$ = this.typeAheadService.search(
              this.url,
              this.params
            );
            return typeAheadService$;
          }
        } else {
          if (this.options?.length > 0) {
            if (typeof term === 'boolean') {
              return of(this.options);
            }
            if (term.length < 3) {
              return [];
            } else {
              const options = this.options
                .filter(
                  (v) => v.label.toLowerCase().indexOf(term.toLowerCase()) > -1
                )
                .slice(0, 10);
              return of(options);
            }
          }
        }
      })
    );
  };

  onSelect(event: NgbTypeaheadSelectItemEvent) {
    this.resultItem.emit(event.item);
  }

  get labelId(): string {
    const labelId = `${this.id}-${this.label?.toLowerCase()}`;
    return labelId;
  }
}
