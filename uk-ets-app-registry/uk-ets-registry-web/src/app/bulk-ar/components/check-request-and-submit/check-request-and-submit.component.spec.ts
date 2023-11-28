import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { CheckRequestAndSubmitComponent } from '@registry-web/bulk-ar/components';
import { RouterTestingModule } from '@angular/router/testing';

describe('CheckRequestAndSignComponent', () => {
  let component: CheckRequestAndSubmitComponent;
  let fixture: ComponentFixture<CheckRequestAndSubmitComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [CheckRequestAndSubmitComponent],
        imports: [RouterTestingModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckRequestAndSubmitComponent);
    component = fixture.componentInstance;
    component.fileHeader = {
      id: 1,
      fileName: 'bulkArName',
      fileSize: '2000',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
