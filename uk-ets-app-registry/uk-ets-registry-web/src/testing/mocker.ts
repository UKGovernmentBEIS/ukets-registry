import { Type } from '@angular/core';
import { TestBed } from '@angular/core/testing';

export type MockType<T> = Partial<jest.Mocked<T>>;

export function mockClass<T>(someClass: Type<T>): MockType<T> {
  return Object.getOwnPropertyNames(someClass.prototype)
    .map(property => [property, jest.fn()] as const)
    .reduce((obj, entry) => ({ ...obj, [entry[0]]: entry[1] }), {});
}

export function injectMock<T>(someClass: Type<T>): MockType<T> {
  return TestBed.inject(someClass) as MockType<T>;
}
