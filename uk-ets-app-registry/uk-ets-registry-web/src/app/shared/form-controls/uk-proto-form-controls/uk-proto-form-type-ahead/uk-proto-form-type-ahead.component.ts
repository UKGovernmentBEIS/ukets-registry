import {
  Component,
  EventEmitter,
  Inject,
  Input,
  OnDestroy,
  OnInit,
  Output,
  TemplateRef,
} from '@angular/core';
import {
  ControlContainer,
  UntypedFormControl,
  FormGroupDirective,
} from '@angular/forms';
import { HttpParams } from '@angular/common/http';

import {
  debounceTime,
  distinctUntilChanged,
  merge,
  Observable,
  Subject,
  switchMap,
  takeUntil,
} from 'rxjs';

import { NgbTypeaheadSelectItemEvent } from '@ng-bootstrap/ng-bootstrap';
import { ResultTemplateContext } from '@ng-bootstrap/ng-bootstrap/typeahead/typeahead-window';
import { TypeAheadService } from '@registry-web/shared/services';

import { UkProtoFormComponent } from '../../uk-proto-form.component';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
@Component({
  selector: 'app-form-control-type-ahead',
  templateUrl: './uk-proto-form-type-ahead.component.html',
  styleUrls: ['./uk-proto-form-type-ahead.component.scss'],
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkProtoFormTypeAheadComponent
  extends UkProtoFormComponent
  implements OnInit, OnDestroy
{
  @Input() maxlength = 40;
  @Input() showLabel = true;
  @Input() disabled = false;

  @Input()
  url?: string;
  @Input()
  params?: HttpParams;
  @Input()
  searchKey?: string;
  @Input()
  resultTemplate: TemplateRef<ResultTemplateContext>;
  @Input()
  placeHolder = '';
  @Input()
  inputFormatter: (item: any) => string;

  @Input() options?: any[] = [];

  @Output() readonly resultItem = new EventEmitter<any>();

  @Input() IsSelect: boolean;

  @Input()
  isNumber?: boolean = false;

  private readonly unsubscribe$: Subject<void> = new Subject();

  constructor(
    private parentf: FormGroupDirective,
    private typeAheadService: TypeAheadService,
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string
  ) {
    super(parentf);
  }

  search = (text$: Observable<string>) => {
    const debouncedText$ = text$.pipe(
      debounceTime(200),
      distinctUntilChanged()
    );

    return merge(debouncedText$).pipe(
      switchMap((term: string) => {
        if (this.isNumber) {
          if (isNaN(+term) || term.includes('.')) {
            this.formControl.setValue(null, { emitEvent: false });
            return '';
          }
        }
        if (this.params) {
          if (term.length < 3) {
            return [];
          } else {
            this.params = this.params.set(this.searchKey, term);
            const typeAheadService$ = this.typeAheadService.search(
              this.ukEtsRegistryApiBaseUrl + this.url,
              this.params
            );
            return typeAheadService$;
          }
        }
      })
    );
  };

  onSelect(event: NgbTypeaheadSelectItemEvent) {
    this.resultItem.emit(event.item);
  }

  ngOnInit(): void {
    super.ngOnInit();
    (this.parentf.form.get(this.id) as UntypedFormControl).valueChanges
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe((val) => {
        if (typeof val === 'object' && val !== null) {
          (this.parentf.form.get(this.id) as UntypedFormControl).setValue(
            val[this.id],
            { emitEvent: false }
          );
        }
      });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }
}
