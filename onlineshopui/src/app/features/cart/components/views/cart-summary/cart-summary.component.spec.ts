import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CartSummaryComponent } from './cart-summary.component';

describe('CartSummaryComponent', () => {
  let component: CartSummaryComponent;
  let fixture: ComponentFixture<CartSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CartSummaryComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CartSummaryComponent);
    component = fixture.componentInstance;

    fixture.componentRef.setInput('subtotal', '$150.00');
    fixture.componentRef.setInput('itemCount', 3);
    fixture.detectChanges();
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display subtotal', () => {
      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain('$150.00');
    });

    it('should display item count', () => {
      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain('3');
    });
  });

  describe('Subtotal Input', () => {
    it('should update when subtotal changes', () => {
      fixture.componentRef.setInput('subtotal', '$250.50');
      fixture.detectChanges();

      expect(component.subtotal()).toBe('$250.50');
      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain('$250.50');
    });

    it('should handle zero subtotal', () => {
      fixture.componentRef.setInput('subtotal', '$0.00');
      fixture.detectChanges();

      expect(component.subtotal()).toBe('$0.00');
    });

    it('should handle large subtotals', () => {
      fixture.componentRef.setInput('subtotal', '$9,999.99');
      fixture.detectChanges();

      expect(component.subtotal()).toBe('$9,999.99');
    });
  });

  describe('Item Count Input', () => {
    it('should update when item count changes', () => {
      fixture.componentRef.setInput('itemCount', 5);
      fixture.detectChanges();

      expect(component.itemCount()).toBeCloseTo(5);
    });

    it('should handle zero items', () => {
      fixture.componentRef.setInput('itemCount', 0);
      fixture.detectChanges();

      expect(component.itemCount()).toBeCloseTo(0);
    });

    it('should handle single item', () => {
      fixture.componentRef.setInput('itemCount', 1);
      fixture.detectChanges();

      expect(component.itemCount()).toBeCloseTo(1);
    });

    it('should handle large item counts', () => {
      fixture.componentRef.setInput('itemCount', 100);
      fixture.detectChanges();

      expect(component.itemCount()).toBeCloseTo(100);
    });
  });

  describe('Checkout Action', () => {
    it('should emit checkout event when checkout is called', () => {
      const checkoutSpy = jasmine.createSpy();
      component.checkout.subscribe(checkoutSpy);

      component.onCheckout();

      expect(checkoutSpy).toHaveBeenCalledTimes(1);
      expect(checkoutSpy).toHaveBeenCalled();
    });

    it('should emit checkout event multiple times', () => {
      const checkoutSpy = jasmine.createSpy();
      component.checkout.subscribe(checkoutSpy);

      component.onCheckout();
      component.onCheckout();
      component.onCheckout();

      expect(checkoutSpy).toHaveBeenCalledTimes(3);
    });
  });

  describe('Clear Action', () => {
    it('should emit clear event when clear is called', () => {
      const clearSpy = jasmine.createSpy();
      component.clear.subscribe(clearSpy);

      component.onClear();

      expect(clearSpy).toHaveBeenCalledTimes(1);
      expect(clearSpy).toHaveBeenCalled();
    });

    it('should emit clear event multiple times', () => {
      const clearSpy = jasmine.createSpy();
      component.clear.subscribe(clearSpy);

      component.onClear();
      component.onClear();

      expect(clearSpy).toHaveBeenCalledTimes(2);
    });
  });

  describe('isSubmitting Input', () => {
    it('should default to false', () => {
      expect(component.isSubmitting()).toBe(false);
    });

    it('should accept true value', () => {
      fixture.componentRef.setInput('isSubmitting', true);
      fixture.detectChanges();

      expect(component.isSubmitting()).toBe(true);
    });

    it('should accept false value explicitly', () => {
      fixture.componentRef.setInput('isSubmitting', false);
      fixture.detectChanges();

      expect(component.isSubmitting()).toBe(false);
    });

    it('should toggle between true and false', () => {
      fixture.componentRef.setInput('isSubmitting', true);
      fixture.detectChanges();
      expect(component.isSubmitting()).toBe(true);

      fixture.componentRef.setInput('isSubmitting', false);
      fixture.detectChanges();
      expect(component.isSubmitting()).toBe(false);
    });
  });

  describe('Event Interaction', () => {
    it('should not interfere with each other when both events are subscribed', () => {
      const checkoutSpy = jasmine.createSpy();
      const clearSpy = jasmine.createSpy();

      component.checkout.subscribe(checkoutSpy);
      component.clear.subscribe(clearSpy);

      component.onCheckout();
      expect(checkoutSpy).toHaveBeenCalledTimes(1);
      expect(clearSpy).not.toHaveBeenCalled();

      component.onClear();
      expect(checkoutSpy).toHaveBeenCalledTimes(1);
      expect(clearSpy).toHaveBeenCalledTimes(1);
    });
  });

  describe('Edge Cases', () => {
    it('should handle empty subtotal string', () => {
      fixture.componentRef.setInput('subtotal', '');
      fixture.detectChanges();

      expect(component.subtotal()).toBe('');
    });

    it('should handle negative item count', () => {
      fixture.componentRef.setInput('itemCount', -1);
      fixture.detectChanges();

      expect(component.itemCount()).toBe(-1);
    });

    it('should handle subtotal without currency symbol', () => {
      fixture.componentRef.setInput('subtotal', '150.00');
      fixture.detectChanges();

      expect(component.subtotal()).toBe('150.00');
    });
  });
});
