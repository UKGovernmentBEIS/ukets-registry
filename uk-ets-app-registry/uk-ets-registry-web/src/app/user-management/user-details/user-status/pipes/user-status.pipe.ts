import { Pipe, PipeTransform } from '@angular/core';
import { UserStatus, userStatusMap } from '@shared/user';

@Pipe({
  name: 'userStatus'
})
export class UserStatusPipe implements PipeTransform {
  transform(value: UserStatus, ...args: any[]): string {
    return value ? userStatusMap[value].label : '';
  }
}
