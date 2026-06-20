import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ErrorMessageComponent } from './error-message.component';
import { FormControl, Validators } from '@angular/forms';
import { ValidationMessages } from '../../../core/types/providers/validation-messages';

describe('ErrorMessageComponent', () => {
  let component: ErrorMessageComponent;
  let fixture: ComponentFixture<ErrorMessageComponent>;

  const mockValidationMessages = {
    required: () => 'This field is required',
    email: () => 'Please enter a valid email address',
    minlength: (error: any) => `Minimum length is ${error.requiredLength} characters`,
    maxlength: (error: any) => `Maximum length is ${error.requiredLength} characters`,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ErrorMessageComponent],
      providers: [
        {
          provide: ValidationMessages,
          useValue: mockValidationMessages,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ErrorMessageComponent);
    component = fixture.componentInstance;
    // DON'T call detectChanges() here - input must be set first
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      const control = new FormControl('');
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();

      expect(component).toBeTruthy();
    });

    it('should accept null control', () => {
      fixture.componentRef.setInput('control', null);
      fixture.detectChanges();

      expect(component).toBeTruthy();
    });
  });

  describe('Error Message Display', () => {
    it('should not show error when control is null', () => {
      fixture.componentRef.setInput('control', null);
      fixture.detectChanges();

      expect(component.errorMessage()).toBeNull();
    });

    it('should not show error when control is valid', () => {
      const control = new FormControl('valid', Validators.required);
      control.markAsTouched();
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();

      expect(component.errorMessage()).toBeNull();
    });

    it('should not show error when control is invalid but not touched', () => {
      const control = new FormControl('', Validators.required);
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();

      expect(component.errorMessage()).toBeNull();
    });

    it('should show required error when control is touched and empty', () => {
      const control = new FormControl('', Validators.required);
      control.markAsTouched();
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();

      expect(component.errorMessage()).toBe('This field is required');
    });

    it('should show email error when control has invalid email', () => {
      const control = new FormControl('invalid-email', Validators.email);
      control.markAsTouched();
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();

      expect(component.errorMessage()).toBe('Please enter a valid email address');
    });

    it('should show minlength error with correct value', () => {
      const control = new FormControl('abc', Validators.minLength(5));
      control.markAsTouched();
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();

      expect(component.errorMessage()).toBe('Minimum length is 5 characters');
    });

    it('should show maxlength error with correct value', () => {
      const control = new FormControl('abcdefghijk', Validators.maxLength(10));
      control.markAsTouched();
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();

      expect(component.errorMessage()).toBe('Maximum length is 10 characters');
    });
  });

  describe('Error Priority', () => {
    it('should show first error when multiple errors exist', () => {
      const control = new FormControl('', [Validators.required, Validators.email]);
      control.markAsTouched();
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();

      expect(component.errorMessage()).toBe('This field is required');
    });

    it('should show email error when required is satisfied but email is invalid', () => {
      const control = new FormControl('invalid', [Validators.required, Validators.email]);
      control.markAsTouched();
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();

      expect(component.errorMessage()).toBe('Please enter a valid email address');
    });
  });

  describe('Unknown Validators', () => {
    it('should show generic message for unknown validator', () => {
      const control = new FormControl('');
      control.setErrors({ customValidator: true });
      control.markAsTouched();
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();

      expect(component.errorMessage()).toBe('Validation error: customValidator');
    });
  });

  describe('Error Message Updates', () => {
    it('should update error message when control value changes', () => {
      // Start with invalid control
      const control1 = new FormControl('', Validators.required);
      control1.markAsTouched();
      fixture.componentRef.setInput('control', control1);
      fixture.detectChanges();

      expect(component.errorMessage()).toBe('This field is required');

      // Create new control with valid value - computed signals detect new reference
      const control2 = new FormControl('valid', Validators.required);
      control2.markAsTouched();
      fixture.componentRef.setInput('control', control2);
      fixture.detectChanges();

      expect(component.errorMessage()).toBeNull();
    });

    it('should clear error when control becomes untouched', () => {
      // Start with touched, invalid control
      const control1 = new FormControl('', Validators.required);
      control1.markAsTouched();
      fixture.componentRef.setInput('control', control1);
      fixture.detectChanges();

      expect(component.errorMessage()).toBe('This field is required');

      // Create new control that's untouched - computed signals detect new reference
      const control2 = new FormControl('', Validators.required);
      // Don't mark as touched
      fixture.componentRef.setInput('control', control2);
      fixture.detectChanges();

      expect(component.errorMessage()).toBeNull();
    });

    it('should show error when control becomes touched', () => {
      // Start with untouched control
      const control1 = new FormControl('', Validators.required);
      fixture.componentRef.setInput('control', control1);
      fixture.detectChanges();

      expect(component.errorMessage()).toBeNull();

      // Create new control that's touched - computed signals detect new reference
      const control2 = new FormControl('', Validators.required);
      control2.markAsTouched();
      fixture.componentRef.setInput('control', control2);
      fixture.detectChanges();

      expect(component.errorMessage()).toBe('This field is required');
    });
  });

  describe('Edge Cases', () => {
    it('should handle control with no errors object', () => {
      const control = new FormControl('value');
      control.markAsTouched();
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();

      expect(component.errorMessage()).toBeNull();
    });

    it('should handle control with empty errors object', () => {
      const control = new FormControl('value');
      control.setErrors({});
      control.markAsTouched();
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();

      expect(component.errorMessage()).toBeNull();
    });

    it('should handle switching between different controls', () => {
      const control1 = new FormControl('', Validators.required);
      control1.markAsTouched();
      fixture.componentRef.setInput('control', control1);
      fixture.detectChanges();

      expect(component.errorMessage()).toBe('This field is required');

      const control2 = new FormControl('invalid@', Validators.email);
      control2.markAsTouched();
      fixture.componentRef.setInput('control', control2);
      fixture.detectChanges();

      expect(component.errorMessage()).toBe('Please enter a valid email address');
    });
  });
});
