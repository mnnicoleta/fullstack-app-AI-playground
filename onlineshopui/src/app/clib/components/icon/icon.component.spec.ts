import { ComponentFixture, TestBed } from '@angular/core/testing';
import { IconComponent } from './icon.component';
import { LucideAngularModule, icons } from 'lucide-angular';

describe('IconComponent', () => {
  let component: IconComponent;
  let fixture: ComponentFixture<IconComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IconComponent, LucideAngularModule.pick(icons)],
    }).compileComponents();

    fixture = TestBed.createComponent(IconComponent);
    component = fixture.componentInstance;

    // Set required input
    fixture.componentRef.setInput('name', 'sun');
    fixture.detectChanges();
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should have default size of sm', () => {
      expect(component.size()).toBe('sm');
    });

    it('should have empty className by default', () => {
      expect(component.className()).toBe('');
    });

    it('should have null ariaLabel by default', () => {
      expect(component.ariaLabel()).toBeNull();
    });

    it('should have default strokeWidth of 1.5', () => {
      expect(component.strokeWidth()).toBeCloseTo(1.5);
    });
  });

  describe('Name Input', () => {
    it('should accept sun icon name', () => {
      fixture.componentRef.setInput('name', 'sun');
      fixture.detectChanges();

      expect(component.name()).toBe('sun');
      expect(component.icon()).toBeDefined();
    });

    it('should accept moon icon name', () => {
      fixture.componentRef.setInput('name', 'moon');
      fixture.detectChanges();

      expect(component.name()).toBe('moon');
      expect(component.icon()).toBeDefined();
    });

    it('should accept menu icon name', () => {
      fixture.componentRef.setInput('name', 'menu');
      fixture.detectChanges();

      expect(component.name()).toBe('menu');
      expect(component.icon()).toBeDefined();
    });

    it('should update icon when name changes', () => {
      fixture.componentRef.setInput('name', 'sun');
      fixture.detectChanges();
      const icon1 = component.icon();

      fixture.componentRef.setInput('name', 'moon');
      fixture.detectChanges();
      const icon2 = component.icon();

      expect(icon1).not.toBe(icon2);
    });
  });

  describe('Size Input', () => {
    it('should accept xs size', () => {
      fixture.componentRef.setInput('size', 'xs');
      fixture.detectChanges();

      expect(component.size()).toBe('xs');
      expect(component.sizePx()).toBeCloseTo(16);
    });

    it('should accept sm size', () => {
      fixture.componentRef.setInput('size', 'sm');
      fixture.detectChanges();

      expect(component.size()).toBe('sm');
      expect(component.sizePx()).toBeCloseTo(20);
    });

    it('should accept md size', () => {
      fixture.componentRef.setInput('size', 'md');
      fixture.detectChanges();

      expect(component.size()).toBe('md');
      expect(component.sizePx()).toBeCloseTo(24);
    });

    it('should accept lg size', () => {
      fixture.componentRef.setInput('size', 'lg');
      fixture.detectChanges();

      expect(component.size()).toBe('lg');
      expect(component.sizePx()).toBeCloseTo(28);
    });

    it('should default to sm size with 20px', () => {
      expect(component.sizePx()).toBeCloseTo(20);
    });
  });

  describe('SizePx Computed Signal', () => {
    it('should compute correct pixel value for xs', () => {
      fixture.componentRef.setInput('size', 'xs');
      fixture.detectChanges();

      expect(component.sizePx()).toBeCloseTo(16);
    });

    it('should compute correct pixel value for sm', () => {
      fixture.componentRef.setInput('size', 'sm');
      fixture.detectChanges();

      expect(component.sizePx()).toBeCloseTo(20);
    });

    it('should compute correct pixel value for md', () => {
      fixture.componentRef.setInput('size', 'md');
      fixture.detectChanges();

      expect(component.sizePx()).toBeCloseTo(24);
    });

    it('should compute correct pixel value for lg', () => {
      fixture.componentRef.setInput('size', 'lg');
      fixture.detectChanges();

      expect(component.sizePx()).toBeCloseTo(28);
    });

    it('should update sizePx when size changes', () => {
      fixture.componentRef.setInput('size', 'xs');
      fixture.detectChanges();
      expect(component.sizePx()).toBeCloseTo(16);

      fixture.componentRef.setInput('size', 'lg');
      fixture.detectChanges();
      expect(component.sizePx()).toBeCloseTo(28);
    });
  });

  describe('ClassName Input', () => {
    it('should accept custom className', () => {
      fixture.componentRef.setInput('className', 'text-red-500');
      fixture.detectChanges();

      expect(component.className()).toBe('text-red-500');
    });

    it('should accept multiple classes', () => {
      fixture.componentRef.setInput('className', 'text-red-500 hover:text-red-700');
      fixture.detectChanges();

      expect(component.className()).toBe('text-red-500 hover:text-red-700');
    });

    it('should handle empty className', () => {
      fixture.componentRef.setInput('className', '');
      fixture.detectChanges();

      expect(component.className()).toBe('');
    });
  });

  describe('AriaLabel Input', () => {
    it('should accept ariaLabel string', () => {
      fixture.componentRef.setInput('ariaLabel', 'Toggle theme');
      fixture.detectChanges();

      expect(component.ariaLabel()).toBe('Toggle theme');
    });

    it('should accept null ariaLabel', () => {
      fixture.componentRef.setInput('ariaLabel', null);
      fixture.detectChanges();

      expect(component.ariaLabel()).toBeNull();
    });

    it('should update ariaLabel', () => {
      fixture.componentRef.setInput('ariaLabel', 'Label 1');
      fixture.detectChanges();
      expect(component.ariaLabel()).toBe('Label 1');

      fixture.componentRef.setInput('ariaLabel', 'Label 2');
      fixture.detectChanges();
      expect(component.ariaLabel()).toBe('Label 2');
    });
  });

  describe('StrokeWidth Input', () => {
    it('should accept custom strokeWidth', () => {
      fixture.componentRef.setInput('strokeWidth', 2);
      fixture.detectChanges();

      expect(component.strokeWidth()).toBeCloseTo(2);
    });

    it('should accept decimal strokeWidth', () => {
      fixture.componentRef.setInput('strokeWidth', 1.75);
      fixture.detectChanges();

      expect(component.strokeWidth()).toBeCloseTo(1.75);
    });

    it('should accept zero strokeWidth', () => {
      fixture.componentRef.setInput('strokeWidth', 0);
      fixture.detectChanges();

      expect(component.strokeWidth()).toBeCloseTo(0);
    });

    it('should default to 1.5', () => {
      expect(component.strokeWidth()).toBeCloseTo(1.5);
    });
  });

  describe('Multiple Inputs Together', () => {
    it('should handle all inputs simultaneously', () => {
      fixture.componentRef.setInput('name', 'moon');
      fixture.componentRef.setInput('size', 'lg');
      fixture.componentRef.setInput('className', 'text-blue-500');
      fixture.componentRef.setInput('ariaLabel', 'Moon icon');
      fixture.componentRef.setInput('strokeWidth', 2);
      fixture.detectChanges();

      expect(component.name()).toBe('moon');
      expect(component.size()).toBe('lg');
      expect(component.sizePx()).toBeCloseTo(28);
      expect(component.className()).toBe('text-blue-500');
      expect(component.ariaLabel()).toBe('Moon icon');
      expect(component.strokeWidth()).toBeCloseTo(2);
      expect(component.icon()).toBeDefined();
    });
  });

  describe('Icon Computed Signal', () => {
    it('should return correct icon for sun', () => {
      fixture.componentRef.setInput('name', 'sun');
      fixture.detectChanges();

      expect(component.icon()).toBeDefined();
    });

    it('should return correct icon for chevron-left', () => {
      fixture.componentRef.setInput('name', 'chevron-left');
      fixture.detectChanges();

      expect(component.icon()).toBeDefined();
    });

    it('should return correct icon for plus', () => {
      fixture.componentRef.setInput('name', 'plus');
      fixture.detectChanges();

      expect(component.icon()).toBeDefined();
    });
  });
});
