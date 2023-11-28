/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

import {
  Directive,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnDestroy,
  OnInit,
  Output,
  Renderer2,
} from '@angular/core';
import { pipe, Subject, Subscription } from 'rxjs';
import { debounceTime, filter, first, map, mergeMap, throttleTime } from 'rxjs/operators';
import { v4 as uuidv4 } from 'uuid';
import { DebounceClickService } from './debounce-click.service';

@Directive({ selector: '[appDebounceClick]' })
export class DebounceClickDirective implements OnInit, OnDestroy {
  @Input()
  debounceTime = 500;

  @Input()
  singleClick = false;

  @Input()
  throttle = false;

  @Output() readonly debounceClick = new EventEmitter();

  private clicks = new Subject();
  private subscription: Subscription;

  constructor(private el: ElementRef, private renderer: Renderer2, 
    private debounceService: DebounceClickService){}

  ngOnInit() {
    let operators;
    if (this.singleClick) {
      operators = pipe(first());
    } else {
      if (this.throttle) {
        operators = pipe(throttleTime(this.debounceTime));
      } else {
        operators = pipe(debounceTime(this.debounceTime));
      }
    }
    this.subscription = this.clicks
      .pipe(operators,
        map((e: MouseEvent) => {
          const element = e.target as HTMLElement;
          const elementType = element.nodeName;
          
          //Disable the button
          if(elementType === 'BUTTON') {
            this.renderer.setAttribute(this.el.nativeElement, 'disabled', 'disabled');
            const uuid = uuidv4();
            sessionStorage.setItem('submitUuid', uuid);
             
            // Emit the event
            this.debounceClick.emit(e);
            return {elementType, uuid};
          } else {
            // Emit the event
            this.debounceClick.emit(e);
            return {elementType};
          }
        }),
        filter((d) => d['elementType'] && d['elementType'] === 'BUTTON'),
        mergeMap((d) => {
          const submitUuid = d['uuid'];
          return this.debounceService.restApiStatus$.pipe(
            map((status) => {
              if(submitUuid === status?.uuid) {
                this.renderer.removeAttribute(this.el.nativeElement, 'disabled');
              }
            })
          )
        }))
      .subscribe();
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  @HostListener('click', ['$event'])
  clickEvent(event: MouseEvent) {
    event.preventDefault();
    event.stopPropagation();    
    this.clicks.next(event);
  }
}
