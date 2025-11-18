import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DeleteNoteComponent } from './delete-note.component';
import { UpdateWarningPageComponent } from '@notes/components/update-warning-page/update-warning-page.component';
import { Store } from '@ngrx/store';
import { RouterModule } from '@angular/router';

describe('UpdateWarningPageComponent', () => {
  let component: UpdateWarningPageComponent;
  let fixture: ComponentFixture<UpdateWarningPageComponent>;
  let storeMock: Partial<Store>;

  beforeEach(async () => {
    storeMock = {
      select: jest.fn(),
      dispatch: jest.fn(),
    } as unknown as jest.Mocked<Store>;
    await TestBed.configureTestingModule({
      imports: [UpdateWarningPageComponent, RouterModule.forRoot([])],
      providers: [{ provide: Store, useValue: storeMock }],
    }).compileComponents();

    fixture = TestBed.createComponent(UpdateWarningPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
