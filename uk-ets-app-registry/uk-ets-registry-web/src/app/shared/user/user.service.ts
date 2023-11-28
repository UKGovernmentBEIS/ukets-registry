import {Injectable} from '@angular/core';
import {User} from './user';
import {SessionStorageService} from 'angular-web-storage';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  KEY = 'user.details';

  constructor(private session: SessionStorageService) {}

  save(user: User) {
    this.session.set(this.KEY, JSON.stringify(user.toJSON()));
  }

  clear() {
    this.session.remove(this.KEY);
  }

  get(): User {
    return User.fromJSON(this.session.get(this.KEY));
  }
}
