import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { CheckDocumentsRequestComponent } from './check-documents-request.component';
import { RouterTestingModule } from '@angular/router/testing';
import { SummaryListComponent } from '@shared/summary-list';

describe('CheckDocumentsRequestComponent', () => {
  let component: CheckDocumentsRequestComponent;
  let fixture: ComponentFixture<CheckDocumentsRequestComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [CheckDocumentsRequestComponent, SummaryListComponent],
        imports: [RouterTestingModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckDocumentsRequestComponent);
    component = fixture.componentInstance;
    component.documentNames = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
