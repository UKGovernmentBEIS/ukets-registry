import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { DisplayTypeMap, PublicationFrequencyMap, Section } from "@report-publication/model";

@Component({
  selector: 'app-publication-overview',
  templateUrl: './publication-overview.component.html',
})
export class PublicationOverviewComponent implements OnInit {
  @Input() publicationOverviewHeader: string;
  @Input() sections: Section[];
  @Output() readonly selectSection = new EventEmitter<number>();
  readonly publicationFrequencyMap = PublicationFrequencyMap;
  readonly displayTypeMap = DisplayTypeMap;

  ngOnInit(): void {}

  onClick(id: number) {
    this.selectSection.emit(id);
  }
}
