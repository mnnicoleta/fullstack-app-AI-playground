import { TestBed, ComponentFixture } from '@angular/core/testing';
import { EnvironmentConfig } from '../../../../../core/types/providers/environment-config';
import { MOCK_ENVIRONMENT_CONFIG } from '../../../../../core/mocks/data/environment.mock';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { signal } from '@angular/core';
import { ProductCreatePageComponent } from './product-create-page.component';
import { ProductService } from '../../../services/product.service';
import { NotificationsService } from '../../../../../core/services/notifications.service';
import { MOCK_CATEGORIES, MOCK_PRODUCTS } from '../../../../../core/mocks/data/products.mock';
import { AppNavRoutes } from '../../../../../core/config/constants/navigation.constants';
import { ValidationMessages } from '../../../../../core/types/providers/validation-messages';
import { createValidationMessages } from '../../../../../core/config/constants/validation.constants';
import { I18nService } from '../../../../../core/services/i18n.service';

describe('ProductCreatePageComponent', () => {
    let component: ProductCreatePageComponent;
    let fixture: ComponentFixture<ProductCreatePageComponent>;
    let productServiceMock: {
        categories: ReturnType<typeof signal>;
        suppliers: ReturnType<typeof signal>;
        loading: ReturnType<typeof signal>;
        loadCategories: jasmine.Spy;
        loadSuppliers: jasmine.Spy;
        create: jasmine.Spy;
    };
    let routerMock: {
        navigate: jasmine.Spy;
    };
    let notificationsServiceMock: {
        notifySuccess: jasmine.Spy;
        notifyError: jasmine.Spy;
    };
    let consoleErrorSpy: ReturnType<typeof spyOn>;

    beforeEach(() => {
        // Mock console.error to suppress expected error logs during error handling tests
        consoleErrorSpy = spyOn(console, 'error').and.callFake(() => {});
        productServiceMock = {
            categories: signal([...MOCK_CATEGORIES]),
            suppliers: signal([]),
            loading: signal(false),
            loadCategories: jasmine.createSpy().and.returnValue(of(MOCK_CATEGORIES)),
            loadSuppliers: jasmine.createSpy().and.returnValue(of([])),
            create: jasmine.createSpy().and.returnValue(of(MOCK_PRODUCTS[0]))
        };

        routerMock = {
            navigate: jasmine.createSpy()
        };

        notificationsServiceMock = {
            notifySuccess: jasmine.createSpy(),
            notifyError: jasmine.createSpy()
        };

        TestBed.configureTestingModule({
            imports: [ProductCreatePageComponent],
            providers: [
                { provide: EnvironmentConfig, useValue: MOCK_ENVIRONMENT_CONFIG },
                { provide: ProductService, useValue: productServiceMock },
                { provide: Router, useValue: routerMock },
                { provide: NotificationsService, useValue: notificationsServiceMock },
                I18nService,
                { provide: ValidationMessages, useFactory: createValidationMessages, deps: [I18nService] }
            ]
        });

        fixture = TestBed.createComponent(ProductCreatePageComponent);
        component = fixture.componentInstance;
    });

    afterEach(() => {
        consoleErrorSpy.and.stub();
    });

    describe('Initialization', () => {
        it('should create', () => {
            // Prepare
            // (component created in beforeEach)

            // Action
            // (no action needed)

            // Verify
            expect(component).toBeTruthy();
        });

        it('should load categories on init', () => {
            // Prepare
            // (component created in beforeEach)

            // Action
            component.ngOnInit();

            // Verify
            expect(productServiceMock.loadCategories).toHaveBeenCalled();
        });

        it('should initialize with empty form', () => {
            // Prepare
            // (component created in beforeEach)

            // Action
            fixture.detectChanges();

            // Verify
            expect(component.form.value).toEqual({
                name: '',
                description: '',
                price: 0,
                weight: 0,
                imageUrl: '',
                categoryId: '',
                supplierId: ''
            });
        });
    });

    describe('onSubmit()', () => {
        it('should not submit when form is invalid', () => {
            // Prepare
            fixture.detectChanges();
            expect(component.form.invalid).toBe(true);

            // Action
            component.onSubmit();

            // Verify
            expect(productServiceMock.create).not.toHaveBeenCalled();
            expect(component.form.touched).toBe(true);
        });

        it('should create product and navigate on success', () => {
            // Prepare
            fixture.detectChanges();
            component.form.patchValue({
                name: 'Test Product',
                description: 'Test Description',
                price: 99.99,
                weight: 1.5,
                imageUrl: 'http://test.com/image.jpg',
                categoryId: 'cat-1',
                supplierId: 'sup-1'
            });

            // Action
            component.onSubmit();

            // Verify
            expect(productServiceMock.create).toHaveBeenCalled();
            expect(notificationsServiceMock.notifySuccess).toHaveBeenCalledWith({
                title: 'Product created',
                message: 'Your new product is now available.'
            });
            expect(routerMock.navigate).toHaveBeenCalledWith([
                `/${AppNavRoutes.Products.root}/${AppNavRoutes.Products.features.overview}`
            ]);
        });

        it('should handle create failure', () => {
            // Prepare
            fixture.detectChanges();
            component.form.patchValue({
                name: 'Test Product',
                description: 'Test Description',
                price: 99.99,
                weight: 1.5,
                imageUrl: 'http://test.com/image.jpg',
                categoryId: 'cat-1',
                supplierId: 'sup-1'
            });
            productServiceMock.create.and.returnValue(throwError(() => new Error('Failed')));

            // Action
            component.onSubmit();

            // Verify
            expect(notificationsServiceMock.notifyError).toHaveBeenCalledWith({
                title: 'Create failed',
                message: 'Unable to create the product.'
            });
            expect(routerMock.navigate).not.toHaveBeenCalled();
        });

        it('should disable form while submitting', () => {
            // Prepare
            fixture.detectChanges();
            component.form.patchValue({
                name: 'Test Product',
                description: 'Test Description',
                price: 99.99,
                weight: 1.5,
                imageUrl: 'http://test.com/image.jpg',
                categoryId: 'cat-1',
                supplierId: 'sup-1'
            });
            expect(component.form.enabled).toBe(true);

            // Action
            component.isSubmitting.set(true);
            fixture.detectChanges();

            // Verify
            expect(component.form.disabled).toBe(true);
        });
    });

    describe('onCancel()', () => {
        it('should navigate to products overview', () => {
            // Prepare
            // (component created in beforeEach)

            // Action
            component.onCancel();

            // Verify
            expect(routerMock.navigate).toHaveBeenCalledWith([
                `/${AppNavRoutes.Products.root}/${AppNavRoutes.Products.features.overview}`
            ]);
        });
    });
});
