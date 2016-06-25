import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { RouteParams } from '@angular/router-deprecated';
import { Hero }        from './hero';
import { HeroService } from './hero.service';
@Component({
  selector: 'my-hero-detail',
  templateUrl: 'app/hero-detail.component.html',
  styleUrls: ['app/hero-detail.component.css']
})
export class HeroDetailComponent implements OnInit {
  @Input() hero: Hero;
  @Output() close = new EventEmitter();
  
  error: any;
  errorMessage: string;
  navigated = false; // true if navigated here
  constructor(
    private heroService: HeroService,
    private routeParams: RouteParams) {
  }
  ngOnInit() {
    if (this.routeParams.get('id') !== null) {
      let id = +this.routeParams.get('id');
      this.navigated = true;
      this.heroService.getHeroes()
      .subscribe(
    		  heroes => heroes.filter(hero => hero.id === id)[0],
              error =>  this.errorMessage = <any>error);
    } else {
      this.navigated = false;
      this.hero = new Hero();
    }
  }
  save() {
    this.heroService
        .save(this.hero)
        .subscribe(hero => {
          this.hero = hero; // saved hero, w/ id if new
          this.goBack(hero);
        },
        error =>  this.errorMessage = <any>error);
  }
  goBack(savedHero: Hero = null) {
    this.close.emit(savedHero);
    if (this.navigated) { window.history.back(); }
  }
}