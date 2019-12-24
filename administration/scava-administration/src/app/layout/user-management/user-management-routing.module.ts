import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UserManagementComponent } from './user-management.component';
import { UserManagementUpdateComponent } from './user-management-update/user-management-update.component';
import { RoleGuard } from '../../shared/guard/role.guard';
import { JwtTokenGuard } from '../../shared';

const routes: Routes = [
  {
    path: '',
    canActivate: [RoleGuard, JwtTokenGuard],
    component: UserManagementComponent,
    data: {
      authorities: ['ROLE_ADMIN']
    }
  },
  {
    path: ':login/edit',
    component: UserManagementUpdateComponent,
    canActivate: [RoleGuard],
    data: {
      authorities: ['ROLE_ADMIN'],
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserManagementRoutingModule { }
