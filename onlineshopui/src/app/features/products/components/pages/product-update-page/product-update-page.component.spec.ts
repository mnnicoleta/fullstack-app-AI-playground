import { TestBed, ComponentFixture } from '@angular/core/testing';
import { EnvironmentConfig } from '../../../../../core/types/providers/environment-config';
import { MOCK_ENVIRONMENT_CONFIG } from '../../../../../core/mocks/data/environment.mock';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of, throwError } from 'rxjs';
import { signal } from '@angular/core';
import { ProductUpdatePageComponent } from './product-update-page.component';
import { ProductService } from '../../../services/product.service';
import { NotificationsService } from '../../../../../core/services/notifications.service';
import { MOCK_CATEGORIES, MOCK_PRODUCTS } from '../../../../../core/mocks/data/products.mock';
import { AppNavRoutes } from '../../../../../core/config/constants/navigation.constants';
import { ValidationMessages } from '../../../../../core/types/providers/validation-messages';
import { createValidationMessages } from '../../../../../core/config/constants/validation.constants';
import { I18nService } from '../../../../../core/services/i18n.service';

describe('ProductUpdatePageComponent', () => {
    let component: ProductUpdatePageComponent;
    let fixture: ComponentFixture<ProductUpdatePageComponent>;
    let productServiceMock: {
        selectedProduct: ReturnType<typeof signal>;
        categories: ReturnType<typeof signal>;
        suppliers: ReturnType<typeof signal>;
        loading: ReturnType<typeof signal>;
        error: ReturnType<typeof signal>;
        loadById: jasmine.Spy;
        loadCategories: jasmine.Spy;
        loadSuppliers: jasmine.Spy;
        update: jasmine.Spy;
    };
    let routerMock: {
        navigate: jasmine.Spy;
    };
    let activatedRouteMock: {
        snapshot: {
            paramMap: ReturnType<typeof convertToParamMap>;
        };
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
            selectedProduct: signal(MOCK_PRODUCTS[0]),
            categories: signal([...MOCK_CATEGORIES]),
            suppliers: signal([]),
            loading: signal(false),
            error: signal(null),
            loadById: jasmine.createSpy().and.returnValue(of(MOCK_PRODUCTS[0])),
            loadCategories: jasmine.createSpy().and.returnValue(of(MOCK_CATEGORIES)),
            loadSuppliers: jasmine.createSpy().and.returnValue(of([])),
            update: jasmine.createSpy().and.returnValue(of(MOCK_PRODUCTS[0]))
        };

        routerMock = {
            navigate: jasmine.createSpy()
        };

        activatedRouteMock = {
            snapshot: {
                paramMap: convertToParamMap({ id: 'prod-1' })
            }
        };

        notificationsServiceMock = {
            notifySuccess: jasmine.createSpy(),
            notifyError: jasmine.createSpy()
        };

        TestBed.configureTestingModule({
            imports: [ProductUpdatePageComponent],
            providers: [
                { provide: EnvironmentConfig, useValue: MOCK_ENVIRONMENT_CONFIG },
                { provide: ProductService, useValue: productServiceMock },
                { provide: Router, useValue: routerMock },
                { provide: ActivatedRoute, useValue: activatedRouteMock },
                { provide: NotificationsService, useValue: notificationsServiceMock },
                I18nService,
                { provide: ValidationMessages, useFactory: createValidationMessages, deps: [I18nService] }
            ]
        });

        fixture = TestBed.createComponent(ProductUpdatePageComponent);
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

        it('should load product and categories on init', () => {
            // Prepare
            // (component created in beforeEach)

            // Action
            component.ngOnInit();

            // Verify
            expect(productServiceMock.loadById).toHaveBeenCalledWith('prod-1');
            expect(productServiceMock.loadCategories).toHaveBeenCalled();
        });

        it('should navigate to products overview when no id provided', () => {
            // Prepare
            activatedRouteMock.snapshot.paramMap = convertToParamMap({});

            // Action
            component.ngOnInit();

            // Verify
            expect(routerMock.navigate).toHaveBeenCalledWith([
                `/${AppNavRoutes.Products.root}/${AppNavRoutes.Products.features.overview}`
            ]);
        });

        it('should populate form with product data', () => {
            // Prepare
            fixture.detectChanges();

            // Action
            // (form auto-populated via effect)

            // Verify
            expect(component.form.value).toEqual({
                name: MOCK_PRODUCTS[0].name,
                description: MOCK_PRODUCTS[0].description,
                price: MOCK_PRODUCTS[0].price,
                weight: MOCK_PRODUCTS[0].weight,
                imageUrl: MOCK_PRODUCTS[0].imageUrl,
                categoryId: MOCK_PRODUCTS[0].category.id,
                supplierId: MOCK_PRODUCTS[0].supplier.id
            });
        });
    });

    describe('onSubmit()', () => {
        beforeEach(() => {
            component.ngOnInit();
            fixture.detectChanges();
        });

        it('should not submit when form is invalid', () => {
            // Prepare
            component.form.patchValue({ name: '' }); // Make form invalid

            // Action
            component.onSubmit();

            // Verify
            expect(productServiceMock.update).not.toHaveBeenCalled();
            expect(component.form.touched).toBe(true);
        });

        it('should update product and navigate on success', () => {
            // Prepare
            component.form.patchValue({
                name: 'Updated Product',
                price: 199.99
            });

            // Action
            component.onSubmit();

            // Verify
            expect(productServiceMock.update).toHaveBeenCalledWith('prod-1', {
                name: 'Updated Product',
                description: MOCK_PRODUCTS[0].description,
                price: 199.99,
                weight: MOCK_PRODUCTS[0].weight,
                imageUrl: MOCK_PRODUCTS[0].imageUrl,
                categoryId: MOCK_PRODUCTS[0].category.id,
                supplierId: MOCK_PRODUCTS[0].supplier.id
            });
            expect(notificationsServiceMock.notifySuccess).toHaveBeenCalledWith({
                title: 'Product updated',
                message: 'Changes have been saved.'
            });
            expect(routerMock.navigate).toHaveBeenCalledWith([
                `/${AppNavRoutes.Products.root}/${AppNavRoutes.Products.features.overview}`
            ]);
        });

        it('should not submit when no product id', () => {
            // Prepare
            activatedRouteMock.snapshot.paramMap = convertToParamMap({});

            const newFixture = TestBed.createComponent(ProductUpdatePageComponent);
            const newComponent = newFixture.componentInstance;
            newComponent.ngOnInit();
            productServiceMock.update.calls.reset();

            // Action
            newComponent.onSubmit();

            // Verify
            expect(productServiceMock.update).not.toHaveBeenCalled();
        });

        it('should handle update failure', () => {
            // Prepare
            productServiceMock.update.and.returnValue(throwError(() => new Error('Failed')));

            // Action
            component.onSubmit();

            // Verify
            expect(notificationsServiceMock.notifyError).toHaveBeenCalledWith({
                title: 'Update failed',
                message: 'Unable to save changes.'
            });
            expect(routerMock.navigate).not.toHaveBeenCalled();
        });

        it('should disable form while submitting', () => {
            // Prepare
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

    describe('retry()', () => {
        it('should reload product and categories', () => {
            // Prepare
            component.ngOnInit();
            productServiceMock.loadById.calls.reset();
            productServiceMock.loadCategories.calls.reset();

            // Action
            component.retry();

            // Verify
            expect(productServiceMock.loadById).toHaveBeenCalledWith('prod-1');
            expect(productServiceMock.loadCategories).toHaveBeenCalled();
        });

        it('should not reload when no product id', () => {
            // Prepare
            activatedRouteMock.snapshot.paramMap = convertToParamMap({});
            component.ngOnInit();
            productServiceMock.loadById.calls.reset();
            productServiceMock.loadCategories.calls.reset();

            // Action
            component.retry();

            // Verify
            expect(productServiceMock.loadById).not.toHaveBeenCalled();
            expect(productServiceMock.loadCategories).not.toHaveBeenCalled();
        });
    });
});
