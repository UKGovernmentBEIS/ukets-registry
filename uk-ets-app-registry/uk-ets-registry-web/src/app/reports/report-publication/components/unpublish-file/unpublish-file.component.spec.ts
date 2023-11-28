import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UnpublishFileComponent } from './unpublish-file.component';
import { Store } from '@ngrx/store';
import { MockStore, provideMockStore } from '@ngrx/store/testing';

describe('UnpublishFileComponent', () => {
  let component: UnpublishFileComponent;
  let fixture: ComponentFixture<UnpublishFileComponent>;
  let store: Store;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UnpublishFileComponent],
      providers: [provideMockStore()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UnpublishFileComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(Store) as MockStore<any>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
