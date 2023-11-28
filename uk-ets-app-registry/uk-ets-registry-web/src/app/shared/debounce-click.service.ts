
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Action {
  status: string;
  uuid: string;
}

@Injectable({ providedIn: 'root' })

export class DebounceClickService {

  private restApiStatusSubject = new BehaviorSubject<Action>(null);
  public restApiStatus$: Observable<Action> = this.restApiStatusSubject.asObservable();

  public updateStatus(action: Action) {
    this.restApiStatusSubject.next(action);
  }
  
}
