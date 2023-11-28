import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProtectPipe } from '@shared/pipes';
import { EmptySearchResultsComponent } from './empty-search-results.component';
import { By } from '@angular/platform-browser';

describe('EmptySearchResultsComponent', () => {
  let component: EmptySearchResultsComponent;
  let fixture: ComponentFixture<EmptySearchResultsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmptySearchResultsComponent, ProtectPipe],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmptySearchResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should display the appropriate message', () => {
    const NO_RESULTS_FOUND_MSG = 'There are no matching results.';

    const value = fixture.debugElement.query(By.css('p')).nativeElement;
    expect(value.innerHTML.trim()).toEqual(NO_RESULTS_FOUND_MSG);
  });
});
