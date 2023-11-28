import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ArUpdateAccessRightsComponent } from './ar-update-access-rights.component';
import { AccessRightsPipe } from '@shared/pipes';
import { RouterTestingModule } from '@angular/router/testing';

describe('ArUpdateAccessRightsComponent', () => {
  let component: ArUpdateAccessRightsComponent;
  let fixture: ComponentFixture<ArUpdateAccessRightsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ArUpdateAccessRightsComponent, AccessRightsPipe],
        imports: [RouterTestingModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ArUpdateAccessRightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
