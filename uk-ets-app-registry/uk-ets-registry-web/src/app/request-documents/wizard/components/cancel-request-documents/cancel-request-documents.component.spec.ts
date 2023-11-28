import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelRequestDocumentsComponent } from './cancel-request-documents.component';

describe('CancelRequestDocumentsComponent', () => {
  let component: CancelRequestDocumentsComponent;
  let fixture: ComponentFixture<CancelRequestDocumentsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [CancelRequestDocumentsComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CancelRequestDocumentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
