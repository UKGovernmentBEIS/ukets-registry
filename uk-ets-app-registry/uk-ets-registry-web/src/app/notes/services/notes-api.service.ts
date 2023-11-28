import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { CreateNoteDTO } from '@registry-web/shared/model/note';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class NotesApiService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {}

  notesGetUrl = `${this.ukEtsRegistryApiBaseUrl}/notes.get`;
  notesAddUrl = `${this.ukEtsRegistryApiBaseUrl}/notes.add`;
  notesDeleteUrl = `${this.ukEtsRegistryApiBaseUrl}/notes.delete`;

  fetchAccountNotes(accountIdentifier: string): Observable<any> {
    const params = {
      params: new HttpParams().set('accountIdentifier', accountIdentifier),
    };
    return this.http.get<any>(this.notesGetUrl, params);
  }

  createNote(noteDTO: CreateNoteDTO) {
    return this.http.post<any>(this.notesAddUrl, noteDTO);
  }

  deleteNote(noteId: string) {
    return this.http.delete<any>(`${this.notesDeleteUrl}/${noteId}`);
  }
}
