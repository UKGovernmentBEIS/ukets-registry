import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ArUpdateTypeComponent } from './ar-update-type.component';
import { AuthorisedRepresentativeUpdateTypePipe } from '@shared/pipes';
import { RouterTestingModule } from '@angular/router/testing';

describe('ArUpdateTypeComponent', () => {
  let component: ArUpdateTypeComponent;
  let fixture: ComponentFixture<ArUpdateTypeComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          ArUpdateTypeComponent,
          AuthorisedRepresentativeUpdateTypePipe,
        ],
        imports: [RouterTestingModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ArUpdateTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
