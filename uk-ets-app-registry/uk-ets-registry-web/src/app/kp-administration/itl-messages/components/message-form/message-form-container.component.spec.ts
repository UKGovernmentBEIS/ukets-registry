import { APP_BASE_HREF } from '@angular/common';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { provideMockStore } from '@ngrx/store/testing';

import {
  MessageFormComponent,
  MessageFormContainerComponent,
} from '@kp-administration/itl-messages/components';
import { UkProtoFormCommentAreaComponent } from '@shared/form-controls/uk-proto-form-controls';

describe('MessageFormContainerComponent', () => {
  let component: MessageFormContainerComponent;
  let fixture: ComponentFixture<MessageFormContainerComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          MessageFormContainerComponent,
          MessageFormComponent,
          UkProtoFormCommentAreaComponent,
        ],
        imports: [ReactiveFormsModule, RouterModule.forRoot([])],
        providers: [
          provideMockStore(),
          { provide: APP_BASE_HREF, useValue: '/' },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageFormContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
