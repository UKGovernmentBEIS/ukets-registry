import { ComponentFixture } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

export function getText<T>(theFixture: ComponentFixture<T>, query: string) {
  const compiled = theFixture.debugElement.nativeElement;
  return compiled.querySelector(query).textContent;
}

export function setText<T>(
  theFixture: ComponentFixture<T>,
  query: string,
  theValue: string
) {
  const compiled = theFixture.debugElement.nativeElement;
  compiled.querySelector(query).value = theValue;
}

export function click<T>(theFixture: ComponentFixture<T>, selector: string) {
  const nativeElement = theFixture.debugElement.nativeElement.querySelector(
    selector
  );
  nativeElement.click();
}

export function getElement<T>(
  theFixture: ComponentFixture<T>,
  selector: string
): any {
  return theFixture.debugElement.nativeElement.querySelector(selector);
}
