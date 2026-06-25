import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CartItemRowComponent } from './cart-item-row.component';
import { CartItem } from '../../../types/cart-item.type';
import { ProductDto } from '../../../../../core/types/dtos/product.dto';

describe('CartItemRowComponent', () => {
  let component: CartItemRowComponent;
  let fixture: ComponentFixture<CartItemRowComponent>;

  const mockCartItem: CartItem = {
    productId: 'product-123',
    quantity: 2,
  };

  const mockProduct: ProductDto = {
    id: 'product-123',
    name: 'Test Product',
    description: 'Test Description',
    price: 50.00,
    weight: 1.0,
    category: {
      id: 'cat-123',
      name: 'Electronics',
      description: 'Electronic items',
    },
    supplier: {
      id: 'sup-123',
      name: 'Test Supplier',
      address: 'Test Address',
      description: 'Test Description',
      contactEmail: 'test@supplier.com',
      contactPhone: '123-456-7890',
    },
    imageUrl: '/test-image.jpg',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CartItemRowComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CartItemRowComponent);
    component = fixture.componentInstance;

    fixture.componentRef.setInput('cartItem', mockCartItem);
    fixture.componentRef.setInput('product', mockProduct);
    fixture.detectChanges();
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display product name', () => {
      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain('Test Product');
    });

    it('should display product price', () => {
      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain('$50.00');
    });

    it('should display quantity', () => {
      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain('2');
    });
  });

  describe('Line Total Calculation', () => {
    it('should calculate line total correctly', () => {
      expect(component.lineTotal()).toBeCloseTo(100.00);
    });

    it('should update line total when quantity changes', () => {
      const newCartItem: CartItem = { ...mockCartItem, quantity: 3 };
      fixture.componentRef.setInput('cartItem', newCartItem);
      fixture.detectChanges();

      expect(component.lineTotal()).toBeCloseTo(150.00);
    });

    it('should update line total when price changes', () => {
      const newProduct: ProductDto = { ...mockProduct, price: 75.00 };
      fixture.componentRef.setInput('product', newProduct);
      fixture.detectChanges();

      expect(component.lineTotal()).toBeCloseTo(150.00);
    });

    it('should handle quantity of 1', () => {
      const singleItem: CartItem = { ...mockCartItem, quantity: 1 };
      fixture.componentRef.setInput('cartItem', singleItem);
      fixture.detectChanges();

      expect(component.lineTotal()).toBeCloseTo(50.00);
    });

    it('should handle large quantities', () => {
      const largeQuantity: CartItem = { ...mockCartItem, quantity: 100 };
      fixture.componentRef.setInput('cartItem', largeQuantity);
      fixture.detectChanges();

      expect(component.lineTotal()).toBeCloseTo(5000.00);
    });
  });

  describe('Increment Action', () => {
    it('should emit quantityChange event with incremented value', () => {
      const quantityChangeSpy = jasmine.createSpy();
      component.quantityChange.subscribe(quantityChangeSpy);

      component.increment();

      expect(quantityChangeSpy).toHaveBeenCalledWith(3);
      expect(quantityChangeSpy).toHaveBeenCalledTimes(1);
    });

    it('should emit correct value when quantity is 1', () => {
      const singleItem: CartItem = { ...mockCartItem, quantity: 1 };
      fixture.componentRef.setInput('cartItem', singleItem);
      fixture.detectChanges();

      const quantityChangeSpy = jasmine.createSpy();
      component.quantityChange.subscribe(quantityChangeSpy);

      component.increment();

      expect(quantityChangeSpy).toHaveBeenCalledWith(2);
    });
  });

  describe('Decrement Action', () => {
    it('should emit quantityChange event with decremented value', () => {
      const quantityChangeSpy = jasmine.createSpy();
      component.quantityChange.subscribe(quantityChangeSpy);

      component.decrement();

      expect(quantityChangeSpy).toHaveBeenCalledWith(1);
      expect(quantityChangeSpy).toHaveBeenCalledTimes(1);
    });

    it('should emit 0 when quantity is 1', () => {
      const singleItem: CartItem = { ...mockCartItem, quantity: 1 };
      fixture.componentRef.setInput('cartItem', singleItem);
      fixture.detectChanges();

      const quantityChangeSpy = jasmine.createSpy();
      component.quantityChange.subscribe(quantityChangeSpy);

      component.decrement();

      expect(quantityChangeSpy).toHaveBeenCalledWith(0);
    });

    it('should emit negative number when quantity is 0', () => {
      const zeroItem: CartItem = { ...mockCartItem, quantity: 0 };
      fixture.componentRef.setInput('cartItem', zeroItem);
      fixture.detectChanges();

      const quantityChangeSpy = jasmine.createSpy();
      component.quantityChange.subscribe(quantityChangeSpy);

      component.decrement();

      expect(quantityChangeSpy).toHaveBeenCalledWith(-1);
    });
  });

  describe('Remove Action', () => {
    it('should emit remove event', () => {
      const removeSpy = jasmine.createSpy();
      component.remove.subscribe(removeSpy);

      component.onRemove();

      expect(removeSpy).toHaveBeenCalledTimes(1);
      expect(removeSpy).toHaveBeenCalled();
    });
  });

  describe('isUpdating Input', () => {
    it('should default to false', () => {
      expect(component.isUpdating()).toBe(false);
    });

    it('should accept true value', () => {
      fixture.componentRef.setInput('isUpdating', true);
      fixture.detectChanges();

      expect(component.isUpdating()).toBe(true);
    });

    it('should accept false value explicitly', () => {
      fixture.componentRef.setInput('isUpdating', false);
      fixture.detectChanges();

      expect(component.isUpdating()).toBe(false);
    });
  });

  describe('Edge Cases', () => {
    it('should handle product with zero price', () => {
      const freeProduct: ProductDto = { ...mockProduct, price: 0 };
      fixture.componentRef.setInput('product', freeProduct);
      fixture.detectChanges();

      expect(component.lineTotal()).toBeCloseTo(0);
    });

    it('should handle product with decimal price', () => {
      const decimalProduct: ProductDto = { ...mockProduct, price: 19.99 };
      fixture.componentRef.setInput('product', decimalProduct);
      fixture.detectChanges();

      expect(component.lineTotal()).toBeCloseTo(39.98);
    });

    it('should handle very long product name', () => {
      const longNameProduct: ProductDto = {
        ...mockProduct,
        name: 'This is a very long product name that should be handled properly without breaking the layout',
      };
      fixture.componentRef.setInput('product', longNameProduct);
      fixture.detectChanges();

      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain(longNameProduct.name);
    });
  });
});
