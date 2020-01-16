import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-thought-section',
  templateUrl: './thought-section.component.html',
  styleUrls: ['./thought-section.component.scss']
})
export class ThoughtSectionComponent implements OnInit {

  @Input("title")
  sectionTitle: string

  constructor() { }

  ngOnInit() {
  }

}
