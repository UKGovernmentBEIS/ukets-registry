import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NotesListComponent } from './notes-list.component';
import { SharedModule } from '@registry-web/shared/shared.module';
import { NoteType } from '@registry-web/shared/model';
import { ActivatedRoute } from '@angular/router';

describe('NotesListComponent', () => {
  let component: NotesListComponent;
  let fixture: ComponentFixture<NotesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [NotesListComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get(): string {
                  return '123';
                },
              },
            },
          },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotesListComponent);
    component = fixture.componentInstance;
    component.isSeniorAdmin = true;
    component.accountNotes = [
      {
        id: 1,
        domainId: '1',
        domainType: NoteType.ACCOUNT,
        description: 'test note',
        userIdentifier: '1',
        userName: 'Haris',
        creationDate: new Date(),
      },
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it.skip('should emit deleteNote when delete link is clicked', () => {
    spyOn(component.deleteNote, 'emit');
    const noteId = 'someNoteId';

    const deleteLink = fixture.nativeElement.querySelector('.govuk-link');
    deleteLink.click();

    expect(component.deleteNote.emit).toHaveBeenCalledWith(noteId);
  });
});
