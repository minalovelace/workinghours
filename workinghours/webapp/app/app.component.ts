import { Component } from '@angular/core';
import { RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS } from '@angular/router-deprecated';
import { HTTP_PROVIDERS } from '@angular/http';

//Add the RxJS Observable operators we need in this app.
import './rxjs-operators';

import { HeroListComponent }        from './hero-list.component';
import { WikiComponent }            from './wiki/wiki.component';
import { WikipediaService }            from './wiki/wikipedia.service';

import { DashboardComponent }  from './dashboard.component';
import { HeroesComponent }     from './heroes.component';
import { HeroDetailComponent } from './hero-detail.component';
import { HeroService }         from './hero.service';
@Component({
  selector: 'my-app',
  template: `
    <h1>{{title}}</h1>
    <nav>
      <a [routerLink]="['Dashboard']">Dashboard</a>
      <a [routerLink]="['Heroes']">Heroes</a>
    </nav>
    <router-outlet></router-outlet>
    <hero-list></hero-list>
    <my-wiki></my-wiki>
  `,
  styleUrls: ['app/app.component.css'],
  directives: [ROUTER_DIRECTIVES, WikiComponent, HeroListComponent],
  providers: [
    HTTP_PROVIDERS,
    ROUTER_PROVIDERS,
    HeroService,
    WikipediaService
  ]
})
@RouteConfig([
  { path: '/dashboard',  name: 'Dashboard',  component: DashboardComponent, useAsDefault: true },
  { path: '/detail/:id', name: 'HeroDetail', component: HeroDetailComponent },
  { path: '/heroes',     name: 'Heroes',     component: HeroesComponent }
])
export class AppComponent {
  title = 'Tour of Heroes';
}