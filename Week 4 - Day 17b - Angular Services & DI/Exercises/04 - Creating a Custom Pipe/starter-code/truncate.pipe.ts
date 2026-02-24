import { Pipe, PipeTransform } from '@angular/core';

// TODO 1: Add the @Pipe decorator with name: 'truncate'
@Pipe({ name: 'truncate' })
export class TruncatePipe implements PipeTransform {
  // TODO 2: Implement transform(value: string, maxLength: number = 30): string
  //         If value.length <= maxLength, return value unchanged.
  //         Otherwise return value.slice(0, maxLength) + '…'
  transform(value: string, maxLength: number = 30): string {
    // TODO: implement truncation logic
    return value;
  }
}
