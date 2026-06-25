import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductCardComponent } from './product-card.component';
import { ProductDto } from '../../../../../core/types/dtos/product.dto';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { EnvironmentConfig } from '../../../../../core/types/providers/environment-config';
import { MOCK_ENVIRONMENT_CONFIG } from '../../../../../core/mocks/data/environment.mock';

describe('ProductCardComponent', () => {
  let component: ProductCardComponent;
  let fixture: ComponentFixture<ProductCardComponent>;
  let compiled: DebugElement;

  const mockProduct: ProductDto = {
    id: '123e4567-e89b-12d3-a456-426614174000',
    name: 'Test Product',
    description: 'Test Description',
    price: 99.99,
    weight: 1.5,
    category: {
      id: 'cat-123',
      name: 'Electronics',
      description: 'Electronic items',
    },
    supplier: {
        description: "Test Description",
        contactEmail: "test@test.com",
        contactPhone: "123-456-7890",
      id: 'sup-123',
      name: 'Test Supplier',
      address: 'Test Address',
    },
    imageUrl: '/test-image.jpg',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductCardComponent],
      providers: [
        { provide: EnvironmentConfig, useValue: MOCK_ENVIRONMENT_CONFIG }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductCardComponent);
    component = fixture.componentInstance;
    compiled = fixture.debugElement;

    // Set required input
    fixture.componentRef.setInput('product', mockProduct);
    fixture.detectChanges();
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display product name', () => {
      const nameElement = compiled.query(By.css('h3'));
      expect(nameElement.nativeElement.textContent).toContain('Test Product');
    });

    it('should display product price', () => {
      const priceElement = compiled.query(By.css('.text-2xl'));
      expect(priceElement.nativeElement.textContent).toContain('$99.99');
    });

    it('should display product weight', () => {
      const weightElement = compiled.query(By.css('.text-sm.text-text-muted.uppercase'));
      expect(weightElement.nativeElement.textContent).toContain('1.5g');
    });

    it('should display category name', () => {
      const categoryElement = compiled.query(By.css('.bg-primary\\/10'));
      expect(categoryElement.nativeElement.textContent).toContain('Electronics');
    });

    it('should display supplier name', () => {
      const supplierElement = compiled.query(By.css('.bg-accent\\/10'));
      expect(supplierElement.nativeElement.textContent).toContain('Test Supplier');
    });

    it('should display product description', () => {
      const descElement = compiled.query(By.css('p.line-clamp-2'));
      expect(descElement.nativeElement.textContent.trim()).toContain('Test Description');
    });
  });

  describe('Image URL Computed Signal', () => {
    it('should use product imageUrl when provided', () => {
      expect(component.imageUrl()).toBe('/test-image.jpg');
    });

    it('should use placeholder when imageUrl is empty', () => {
      const productWithoutImage = { ...mockProduct, imageUrl: '' };
      fixture.componentRef.setInput('product', productWithoutImage);
      fixture.detectChanges();

      expect(component.imageUrl()).toBe('/placeholder-product.svg');
    });

    it('should use placeholder when imageUrl is null', () => {
      const productWithoutImage = { ...mockProduct, imageUrl: null as any };
      fixture.componentRef.setInput('product', productWithoutImage);
      fixture.detectChanges();

      expect(component.imageUrl()).toBe('/placeholder-product.svg');
    });
  });

  describe('View Details Action', () => {
    it('should emit viewDetails event with product ID when View button clicked', () => {
      const viewDetailsSpy = jasmine.createSpy();
      component.viewDetails.subscribe(viewDetailsSpy);

      const viewButton = compiled.queryAll(By.css('button'))[0];
      viewButton.nativeElement.click();

      expect(viewDetailsSpy).toHaveBeenCalledWith(mockProduct.id);
      expect(viewDetailsSpy).toHaveBeenCalledTimes(1);
    });

    it('should call onViewDetails method', () => {
      const onViewDetailsSpy = spyOn(component, 'onViewDetails');

      component.onViewDetails();

      expect(onViewDetailsSpy).toHaveBeenCalled();
    });
  });

  describe('Add to Cart Action', () => {
    it('should emit addToCart event with product ID when button clicked', () => {
      const addToCartSpy = jasmine.createSpy();
      component.addToCart.subscribe(addToCartSpy);

      component.onAddToCart();

      expect(addToCartSpy).toHaveBeenCalledWith(mockProduct.id);
      expect(addToCartSpy).toHaveBeenCalledTimes(1);
    });
  });

  describe('Edit Action', () => {
    it('should emit edit event with product ID when Edit button clicked', () => {
      const editSpy = jasmine.createSpy();
      component.edit.subscribe(editSpy);

      component.onEdit();

      expect(editSpy).toHaveBeenCalledWith(mockProduct.id);
      expect(editSpy).toHaveBeenCalledTimes(1);
    });
  });

  describe('Delete Action', () => {
    it('should emit delete event with product ID when Delete button clicked', () => {
      const deleteSpy = jasmine.createSpy();
      component.delete.subscribe(deleteSpy);

      component.onDelete();

      expect(deleteSpy).toHaveBeenCalledWith(mockProduct.id);
      expect(deleteSpy).toHaveBeenCalledTimes(1);
    });
  });

  describe('Multiple Products', () => {
    it('should handle different product IDs', () => {
      const product1 = { ...mockProduct, id: 'id-1' };
      const product2 = { ...mockProduct, id: 'id-2' };

      fixture.componentRef.setInput('product', product1);
      fixture.detectChanges();

      const viewSpy1 = jasmine.createSpy();
      component.viewDetails.subscribe(viewSpy1);
      component.onViewDetails();
      expect(viewSpy1).toHaveBeenCalledWith('id-1');

      fixture.componentRef.setInput('product', product2);
      fixture.detectChanges();

      const viewSpy2 = jasmine.createSpy();
      component.viewDetails.subscribe(viewSpy2);
      component.onViewDetails();
      expect(viewSpy2).toHaveBeenCalledWith('id-2');
    });
  });

  describe('Edge Cases', () => {
    it('should handle product with very long name', () => {
      const longNameProduct = {
        ...mockProduct,
        name: 'This is a very long product name that should be truncated properly with line-clamp',
      };
      fixture.componentRef.setInput('product', longNameProduct);
      fixture.detectChanges();

      const nameElement = compiled.query(By.css('h3'));
      expect(nameElement.nativeElement.textContent).toContain(longNameProduct.name);
    });

    it('should handle product with zero price', () => {
      const freeProduct = { ...mockProduct, price: 0 };
      fixture.componentRef.setInput('product', freeProduct);
      fixture.detectChanges();

      const priceElement = compiled.query(By.css('.text-2xl'));
      expect(priceElement.nativeElement.textContent).toContain('$0.00');
    });

    it('should handle product with decimal weight', () => {
      const decimalWeightProduct = { ...mockProduct, weight: 0.05 };
      fixture.componentRef.setInput('product', decimalWeightProduct);
      fixture.detectChanges();

      const weightElement = compiled.query(By.css('.text-sm.text-text-muted.uppercase'));
      expect(weightElement.nativeElement.textContent).toContain('0.05g');
    });
  });
});
