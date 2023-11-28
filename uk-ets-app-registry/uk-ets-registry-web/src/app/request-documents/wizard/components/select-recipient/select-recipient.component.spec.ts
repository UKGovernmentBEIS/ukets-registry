import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { SelectRecipientComponent } from './select-recipient.component';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { User } from '@shared/user';
import {
  UkProtoFormCommentAreaComponent,
  UkProtoFormSelectComponent,
} from '@shared/form-controls/uk-proto-form-controls';

const formBuilder = new FormBuilder();

describe('SelectRecipientComponent', () => {
  let component: SelectRecipientComponent;
  let fixture: ComponentFixture<SelectRecipientComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule],
        declarations: [
          SelectRecipientComponent,
          UkProtoFormCommentAreaComponent,
          UkProtoFormSelectComponent,
        ],
        providers: [{ provide: FormBuilder, useValue: formBuilder }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectRecipientComponent);
    component = fixture.componentInstance;
    component.candidateRecipients = [
      aTestUser('Michael', 'Jordan'),
      aTestUser('Scottie', 'Pippen'),
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  function aTestUser(firstName: string, lastName: string): User {
    const user = new User();
    user.firstName = firstName;
    user.lastName = lastName;
    return user;
  }
});
