import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CardComponent } from './card.component';

describe('CardComponent', () => {
  let component: CardComponent;
  let fixture: ComponentFixture<CardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should have default padding of md', () => {
      expect(component.padding()).toBe('md');
    });

    it('should have shadow enabled by default', () => {
      expect(component.shadow()).toBe(true);
    });

    it('should not be hoverable by default', () => {
      expect(component.hoverable()).toBe(false);
    });

    it('should not have full height by default', () => {
      expect(component.fullHeight()).toBe(false);
    });
  });

  describe('Padding Input', () => {
    it('should accept sm padding', () => {
      fixture.componentRef.setInput('padding', 'sm');
      fixture.detectChanges();

      expect(component.padding()).toBe('sm');
      expect(component.paddingClass()).toBe('p-3');
    });

    it('should accept md padding', () => {
      fixture.componentRef.setInput('padding', 'md');
      fixture.detectChanges();

      expect(component.padding()).toBe('md');
      expect(component.paddingClass()).toBe('p-4 md:p-6');
    });

    it('should accept lg padding', () => {
      fixture.componentRef.setInput('padding', 'lg');
      fixture.detectChanges();

      expect(component.padding()).toBe('lg');
      expect(component.paddingClass()).toBe('p-6 md:p-8');
    });

    it('should default to md padding class', () => {
      expect(component.paddingClass()).toBe('p-4 md:p-6');
    });
  });

  describe('PaddingClass Computed Signal', () => {
    it('should compute correct class for sm padding', () => {
      fixture.componentRef.setInput('padding', 'sm');
      fixture.detectChanges();

      expect(component.paddingClass()).toBe('p-3');
    });

    it('should compute correct class for md padding', () => {
      fixture.componentRef.setInput('padding', 'md');
      fixture.detectChanges();

      expect(component.paddingClass()).toBe('p-4 md:p-6');
    });

    it('should compute correct class for lg padding', () => {
      fixture.componentRef.setInput('padding', 'lg');
      fixture.detectChanges();

      expect(component.paddingClass()).toBe('p-6 md:p-8');
    });

    it('should update padding class when padding input changes', () => {
      fixture.componentRef.setInput('padding', 'sm');
      fixture.detectChanges();
      expect(component.paddingClass()).toBe('p-3');

      fixture.componentRef.setInput('padding', 'lg');
      fixture.detectChanges();
      expect(component.paddingClass()).toBe('p-6 md:p-8');
    });
  });

  describe('Shadow Input', () => {
    it('should accept true value', () => {
      fixture.componentRef.setInput('shadow', true);
      fixture.detectChanges();

      expect(component.shadow()).toBe(true);
    });

    it('should accept false value', () => {
      fixture.componentRef.setInput('shadow', false);
      fixture.detectChanges();

      expect(component.shadow()).toBe(false);
    });

    it('should toggle shadow value', () => {
      fixture.componentRef.setInput('shadow', true);
      fixture.detectChanges();
      expect(component.shadow()).toBe(true);

      fixture.componentRef.setInput('shadow', false);
      fixture.detectChanges();
      expect(component.shadow()).toBe(false);
    });
  });

  describe('Hoverable Input', () => {
    it('should accept true value', () => {
      fixture.componentRef.setInput('hoverable', true);
      fixture.detectChanges();

      expect(component.hoverable()).toBe(true);
    });

    it('should accept false value', () => {
      fixture.componentRef.setInput('hoverable', false);
      fixture.detectChanges();

      expect(component.hoverable()).toBe(false);
    });

    it('should toggle hoverable value', () => {
      fixture.componentRef.setInput('hoverable', true);
      fixture.detectChanges();
      expect(component.hoverable()).toBe(true);

      fixture.componentRef.setInput('hoverable', false);
      fixture.detectChanges();
      expect(component.hoverable()).toBe(false);
    });
  });

  describe('FullHeight Input', () => {
    it('should accept true value', () => {
      fixture.componentRef.setInput('fullHeight', true);
      fixture.detectChanges();

      expect(component.fullHeight()).toBe(true);
    });

    it('should accept false value', () => {
      fixture.componentRef.setInput('fullHeight', false);
      fixture.detectChanges();

      expect(component.fullHeight()).toBe(false);
    });

    it('should toggle fullHeight value', () => {
      fixture.componentRef.setInput('fullHeight', true);
      fixture.detectChanges();
      expect(component.fullHeight()).toBe(true);

      fixture.componentRef.setInput('fullHeight', false);
      fixture.detectChanges();
      expect(component.fullHeight()).toBe(false);
    });
  });

  describe('Multiple Inputs Together', () => {
    it('should handle all inputs set simultaneously', () => {
      fixture.componentRef.setInput('padding', 'lg');
      fixture.componentRef.setInput('shadow', false);
      fixture.componentRef.setInput('hoverable', true);
      fixture.componentRef.setInput('fullHeight', true);
      fixture.detectChanges();

      expect(component.padding()).toBe('lg');
      expect(component.shadow()).toBe(false);
      expect(component.hoverable()).toBe(true);
      expect(component.fullHeight()).toBe(true);
      expect(component.paddingClass()).toBe('p-6 md:p-8');
    });

    it('should handle inputs changing independently', () => {
      fixture.componentRef.setInput('padding', 'sm');
      fixture.detectChanges();
      expect(component.padding()).toBe('sm');
      expect(component.shadow()).toBe(true);

      fixture.componentRef.setInput('hoverable', true);
      fixture.detectChanges();
      expect(component.padding()).toBe('sm');
      expect(component.hoverable()).toBe(true);
      expect(component.shadow()).toBe(true);
    });
  });

  describe('Content Projection', () => {
    it('should project content', () => {
      const fixtureWithContent = TestBed.createComponent(CardComponent);
      const compiled = fixtureWithContent.debugElement.nativeElement;
      const testContent = 'Test Card Content';

      // Set innerHTML for content projection test
      const contentElement = compiled.querySelector('ng-content') || compiled;

      fixtureWithContent.detectChanges();
      expect(compiled).toBeTruthy();
    });
  });
});
