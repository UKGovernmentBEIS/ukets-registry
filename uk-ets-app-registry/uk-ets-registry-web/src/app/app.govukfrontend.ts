import { Injectable } from '@angular/core';

declare let GOVUKFrontend: any;

@Injectable({
  providedIn: 'root',
})
export class GOVUKFrontendService {
  initAll() {
    GOVUKFrontend.initAll();
  }
}
