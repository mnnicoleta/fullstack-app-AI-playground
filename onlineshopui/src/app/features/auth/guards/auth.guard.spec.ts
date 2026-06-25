import { TestBed } from '@angular/core/testing';
import { Router, UrlTree, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';
import { AppNavRoutes } from '../../../core/config/constants/navigation.constants';

describe('authGuard', () => {
    let authServiceMock: {
        isAuthenticated: jasmine.Spy;
    };
    let routerSpy: {
        createUrlTree: jasmine.Spy;
    };

    beforeEach(() => {
        authServiceMock = {
            isAuthenticated: jasmine.createSpy()
        };

        routerSpy = {
            createUrlTree: jasmine.createSpy()
        };

        TestBed.configureTestingModule({
            providers: [
                { provide: AuthService, useValue: authServiceMock },
                { provide: Router, useValue: routerSpy }
            ]
        });
    });

    it('should allow activation when user is authenticated', () => {
        // Prepare
        authServiceMock.isAuthenticated.and.returnValue(true);

        // Action
        const result = TestBed.runInInjectionContext(() =>
            authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
        );

        // Verify
        expect(result).toBe(true);
        expect(authServiceMock.isAuthenticated).toHaveBeenCalled();
        expect(routerSpy.createUrlTree).not.toHaveBeenCalled();
    });

    it('should redirect to login when user is not authenticated', () => {
        // Prepare
        authServiceMock.isAuthenticated.and.returnValue(false);
        const mockUrlTree = {} as UrlTree;
        routerSpy.createUrlTree.and.returnValue(mockUrlTree);

        // Action
        const result = TestBed.runInInjectionContext(() =>
            authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
        );

        // Verify
        expect(result).toBe(mockUrlTree);
        expect(authServiceMock.isAuthenticated).toHaveBeenCalled();
        expect(routerSpy.createUrlTree).toHaveBeenCalledWith([
            '/',
            AppNavRoutes.Auth.root,
            AppNavRoutes.Auth.features.login
        ]);
    });

    it('should not inject router when user is authenticated', () => {
        // Prepare
        authServiceMock.isAuthenticated.and.returnValue(true);

        // Action
        const result = TestBed.runInInjectionContext(() =>
            authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
        );

        // Verify
        expect(result).toBe(true);
        expect(routerSpy.createUrlTree).not.toHaveBeenCalled();
    });
});
