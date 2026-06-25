import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SpinnerComponent } from './spinner.component';

describe('SpinnerComponent', () => {
  let component: SpinnerComponent;
  let fixture: ComponentFixture<SpinnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SpinnerComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SpinnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should have default size of md', () => {
      expect(component.size()).toBe('md');
    });

    it('should not have overlay by default', () => {
      expect(component.overlay()).toBe(false);
    });

    it('should have default size class of w-8 h-8', () => {
      expect(component.sizeClass()).toBe('w-8 h-8');
    });
  });

  describe('Size Input', () => {
    it('should accept sm size', () => {
      fixture.componentRef.setInput('size', 'sm');
      fixture.detectChanges();

      expect(component.size()).toBe('sm');
    });

    it('should accept md size', () => {
      fixture.componentRef.setInput('size', 'md');
      fixture.detectChanges();

      expect(component.size()).toBe('md');
    });

    it('should accept lg size', () => {
      fixture.componentRef.setInput('size', 'lg');
      fixture.detectChanges();

      expect(component.size()).toBe('lg');
    });
  });

  describe('SizeClass Computed Signal', () => {
    it('should compute w-4 h-4 for sm size', () => {
      fixture.componentRef.setInput('size', 'sm');
      fixture.detectChanges();

      expect(component.sizeClass()).toBe('w-4 h-4');
    });

    it('should compute w-8 h-8 for md size', () => {
      fixture.componentRef.setInput('size', 'md');
      fixture.detectChanges();

      expect(component.sizeClass()).toBe('w-8 h-8');
    });

    it('should compute w-12 h-12 for lg size', () => {
      fixture.componentRef.setInput('size', 'lg');
      fixture.detectChanges();

      expect(component.sizeClass()).toBe('w-12 h-12');
    });

    it('should update sizeClass when size changes', () => {
      fixture.componentRef.setInput('size', 'sm');
      fixture.detectChanges();
      expect(component.sizeClass()).toBe('w-4 h-4');

      fixture.componentRef.setInput('size', 'lg');
      fixture.detectChanges();
      expect(component.sizeClass()).toBe('w-12 h-12');
    });
  });

  describe('Overlay Input', () => {
    it('should accept true value', () => {
      fixture.componentRef.setInput('overlay', true);
      fixture.detectChanges();

      expect(component.overlay()).toBe(true);
    });

    it('should accept false value', () => {
      fixture.componentRef.setInput('overlay', false);
      fixture.detectChanges();

      expect(component.overlay()).toBe(false);
    });

    it('should toggle overlay value', () => {
      fixture.componentRef.setInput('overlay', true);
      fixture.detectChanges();
      expect(component.overlay()).toBe(true);

      fixture.componentRef.setInput('overlay', false);
      fixture.detectChanges();
      expect(component.overlay()).toBe(false);
    });
  });

  describe('Multiple Inputs Together', () => {
    it('should handle size and overlay together', () => {
      fixture.componentRef.setInput('size', 'lg');
      fixture.componentRef.setInput('overlay', true);
      fixture.detectChanges();

      expect(component.size()).toBe('lg');
      expect(component.overlay()).toBe(true);
      expect(component.sizeClass()).toBe('w-12 h-12');
    });

    it('should handle different combinations', () => {
      fixture.componentRef.setInput('size', 'sm');
      fixture.componentRef.setInput('overlay', false);
      fixture.detectChanges();

      expect(component.size()).toBe('sm');
      expect(component.overlay()).toBe(false);
      expect(component.sizeClass()).toBe('w-4 h-4');
    });
  });
});
