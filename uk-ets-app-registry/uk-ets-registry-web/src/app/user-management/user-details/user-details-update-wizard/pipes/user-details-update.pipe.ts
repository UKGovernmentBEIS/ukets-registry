import { Pipe, PipeTransform } from '@angular/core';
import {
  UpdateUserDetailsRequestTypeMap,
  UserUpdateDetailsType,
} from '@user-update/model';

@Pipe({
  name: 'userDetailsUpdateType',
  pure: true,
})
export class UserDetailsUpdatePipe implements PipeTransform {
  transform(value: UserUpdateDetailsType): string {
    return UpdateUserDetailsRequestTypeMap[value].label;
  }
}
