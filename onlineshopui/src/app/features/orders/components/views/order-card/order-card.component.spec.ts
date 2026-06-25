import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OrderCardComponent } from './order-card.component';
import { OrderSummary } from '../../../types/order-summary.type';

describe('OrderCardComponent', () => {
  let component: OrderCardComponent;
  let fixture: ComponentFixture<OrderCardComponent>;

  const mockOrderSummary: any = {
    id: '123e4567-e89b-12d3-a456-426614174000',
    createdAt: '2024-01-15 10:30:00',
    totalItems: 3,
    totalAmount: 299.97,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(OrderCardComponent);
    component = fixture.componentInstance;

    fixture.componentRef.setInput('order', mockOrderSummary);
    fixture.detectChanges();
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display order ID', () => {
      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain(mockOrderSummary.id);
    });

    it('should display created date', () => {
      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain(mockOrderSummary.createdAt);
    });

    it('should display total items', () => {
      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain('3');
    });

    it('should display total amount formatted', () => {
      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain('$299.97');
    });
  });

  describe('View Details Action', () => {
    it('should emit viewDetails event with order ID when button clicked', () => {
      const viewDetailsSpy = jasmine.createSpy();
      component.viewDetails.subscribe(viewDetailsSpy);

      component.onViewDetails();

      expect(viewDetailsSpy).toHaveBeenCalledWith(mockOrderSummary.id);
      expect(viewDetailsSpy).toHaveBeenCalledTimes(1);
    });
  });

  describe('Order Signal', () => {
    it('should return order from signal', () => {
      expect(component.order()).toEqual(mockOrderSummary);
    });

    it('should update when input changes', () => {
      const newOrder: OrderSummary = {
        ...mockOrderSummary,
        id: 'new-order-id',
        totalAmount: 500.0,
      };

      fixture.componentRef.setInput('order', newOrder);
      fixture.detectChanges();

      expect(component.order().id).toBe('new-order-id');
      expect(component.order().totalAmount).toBeCloseTo(500.0);
    });
  });

  describe('Edge Cases', () => {
    it('should handle zero total amount', () => {
      const freeOrder: OrderSummary = {
        ...mockOrderSummary,
        totalAmount: 0,
      };

      fixture.componentRef.setInput('order', freeOrder);
      fixture.detectChanges();

      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain('$0.00');
    });

    it('should handle single item', () => {
      const singleItemOrder: OrderSummary = {
        ...mockOrderSummary,
        totalItems: 1,
      };

      fixture.componentRef.setInput('order', singleItemOrder);
      fixture.detectChanges();

      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain('1');
    });

    it('should handle large order amounts', () => {
      const largeOrder: OrderSummary = {
        ...mockOrderSummary,
        totalAmount: 9999.99,
        totalItems: 50,
      };

      fixture.componentRef.setInput('order', largeOrder);
      fixture.detectChanges();

      const compiled = fixture.debugElement.nativeElement;
      expect(compiled.textContent).toContain('$9999.99');
      expect(compiled.textContent).toContain('50');
    });
  });
});
