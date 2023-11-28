import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { SubWizardTitleComponent } from './sub-wizard-title.component';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';

describe('SubWizardTitleComponent', () => {
  let component: SubWizardTitleComponent;
  let fixture: ComponentFixture<SubWizardTitleComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          SubWizardTitleComponent,
          ScreenReaderPageAnnounceDirective,
        ],
      }).compileComponents();
    })
  );

  it('should create', () => {
    fixture = TestBed.createComponent(SubWizardTitleComponent);
    component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });

  test('should be Primary Contact title', () => {
    fixture = TestBed.createComponent(SubWizardTitleComponent);
    component = fixture.componentInstance;
    component.isAHUpdateWizard = false;
    component.contactType = ContactType.PRIMARY;
    fixture.detectChanges();
    expect(component.title).toEqual('Add the Primary Contact');
  });

  it('should be alternative Primary Contact title', () => {
    fixture = TestBed.createComponent(SubWizardTitleComponent);
    component = fixture.componentInstance;
    component.isAHUpdateWizard = false;
    component.contactType = ContactType.ALTERNATIVE;
    fixture.detectChanges();
    expect(component.title).toEqual('Add an alternative Primary Contact');
  });

  it('should be request update title', () => {
    fixture = TestBed.createComponent(SubWizardTitleComponent);
    component = fixture.componentInstance;
    component.isAHUpdateWizard = true;
    fixture.detectChanges();
    expect(component.title).toEqual('Request to update the account holder');
  });
});
