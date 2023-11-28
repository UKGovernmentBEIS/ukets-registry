import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ToggleButtonComponent } from '@shared/search/toggle-button/toggle-button.component';
import { SortableTableDirective } from '@shared/search/sort/sortable-table.directive';
import { SortableColumnDirective } from '@shared/search/sort/sortable-column.directive';
import { PaginatorComponent } from '@shared/search/paginator';
import { RouterLinkDirectiveStub } from '@shared/test/router-link-directive-stub';
import { GdsDateTimeShortPipe } from '@shared/pipes';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SortService } from '@shared/search/sort/sort.service';
import {
  MessageListContainerComponent,
  SearchMessagesFormComponent,
  SearchMessagesResultsComponent,
} from '@kp-administration/itl-messages/components';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-text/uk-proto-form-text.component';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import { UkProtoFormDatePickerComponent } from '@shared/form-controls/uk-proto-form-controls';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DatePipe } from '@angular/common';

describe('MessageListContainerComponent', () => {
  let component: MessageListContainerComponent;
  let fixture: ComponentFixture<MessageListContainerComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          FormsModule,
          ReactiveFormsModule,
          NgbModule,
        ],
        declarations: [
          SortableColumnDirective,
          SortableTableDirective,
          SearchMessagesFormComponent,
          SearchMessagesResultsComponent,
          MessageListContainerComponent,
          PaginatorComponent,
          ToggleButtonComponent,
          UkProtoFormDatePickerComponent,
          UkProtoFormTextComponent,
          RouterLinkDirectiveStub,
          GdsDateTimeShortPipe,
          DisableControlDirective,
        ],
        providers: [DatePipe, SortService, provideMockStore()],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageListContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
