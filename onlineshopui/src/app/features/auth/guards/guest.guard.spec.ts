import { TestBed } from '@angular/core/testing';
import { Router, UrlTree, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { guestGuard } from './guest.guard';
import { AuthService } from '../services/auth.service';
import { AppNavRoutes } from '../../../core/config/constants/navigation.constants';

describe('guestGuard', () => {
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

    it('should allow activation when user is not authenticated', () => {
        // Prepare
        authServiceMock.isAuthenticated.and.returnValue(false);

        // Action
        const result = TestBed.runInInjectionContext(() =>
            guestGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
        );

        // Verify
        expect(result).toBe(true);
        expect(authServiceMock.isAuthenticated).toHaveBeenCalled();
        expect(routerSpy.createUrlTree).not.toHaveBeenCalled();
    });

    it('should redirect to products when user is authenticated', () => {
        // Prepare
        authServiceMock.isAuthenticated.and.returnValue(true);
        const mockUrlTree = {} as UrlTree;
        routerSpy.createUrlTree.and.returnValue(mockUrlTree);

        // Action
        const result = TestBed.runInInjectionContext(() =>
            guestGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
        );

        // Verify
        expect(result).toBe(mockUrlTree);
        expect(authServiceMock.isAuthenticated).toHaveBeenCalled();
        expect(routerSpy.createUrlTree).toHaveBeenCalledWith([
            '/',
            AppNavRoutes.Products.root,
            AppNavRoutes.Products.features.overview
        ]);
    });

    it('should not inject router when user is not authenticated', () => {
        // Prepare
        authServiceMock.isAuthenticated.and.returnValue(false);

        // Action
        const result = TestBed.runInInjectionContext(() =>
            guestGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
        );

        // Verify
        expect(result).toBe(true);
        expect(routerSpy.createUrlTree).not.toHaveBeenCalled();
    });
});
