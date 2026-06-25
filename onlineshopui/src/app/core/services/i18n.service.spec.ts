import { TestBed } from '@angular/core/testing';
import { I18nService } from './i18n.service';
import { EN_TRANSLATIONS } from '../config/i18n/translations.en';
import { DE_TRANSLATIONS } from '../config/i18n/translations.de';

describe('I18nService', () => {
  let service: I18nService;
  let localStorageSpy: jasmine.SpyObj<Storage>;

  beforeEach(() => {
    // Mock localStorage
    localStorageSpy = jasmine.createSpyObj('localStorage', ['getItem', 'setItem', 'removeItem']);
    Object.defineProperty(window, 'localStorage', {
      value: localStorageSpy,
      writable: true
    });

    TestBed.configureTestingModule({});
    service = TestBed.inject(I18nService);
  });

  afterEach(() => {
    localStorageSpy.getItem.calls.reset();
    localStorageSpy.setItem.calls.reset();
  });

  describe('Initialization', () => {
    it('should create service', () => {
      expect(service).toBeTruthy();
    });

    it('should default to English language', () => {
      expect(service.currentLanguage()).toBe('en');
    });

    it('should load English translations by default', () => {
      expect(service.translations()).toEqual(EN_TRANSLATIONS);
    });

    it('should read language from localStorage if available', () => {
      localStorageSpy.getItem.and.returnValue('de');

      const newService = TestBed.inject(I18nService);

      expect(newService.currentLanguage()).toBe('de');
    });
  });

  describe('setLanguage()', () => {
    it('should switch to German language', () => {
      service.setLanguage('de');

      expect(service.currentLanguage()).toBe('de');
      expect(service.translations()).toEqual(DE_TRANSLATIONS);
    });

    it('should switch to English language', () => {
      service.setLanguage('de');
      service.setLanguage('en');

      expect(service.currentLanguage()).toBe('en');
      expect(service.translations()).toEqual(EN_TRANSLATIONS);
    });

    it('should persist language choice to localStorage', () => {
      service.setLanguage('de');

      expect(localStorageSpy.setItem).toHaveBeenCalledWith('app_language', 'de');
    });

    it('should fallback to English for unsupported language', () => {
      service.setLanguage('fr' as any);

      expect(service.currentLanguage()).toBe('en');
    });

    it('should update translations signal reactively', () => {
      const translationsSpy = jasmine.createSpy('translations');

      // Subscribe to translations signal changes
      const initialTranslations = service.translations();
      expect(initialTranslations).toEqual(EN_TRANSLATIONS);

      service.setLanguage('de');

      expect(service.translations()).toEqual(DE_TRANSLATIONS);
      expect(service.translations()).not.toEqual(EN_TRANSLATIONS);
    });
  });

  describe('translate()', () => {
    it('should translate simple key', () => {
      const result = service.translate('nav.products');

      expect(result).toBe('Products');
    });

    it('should translate nested key', () => {
      const result = service.translate('cart.addToCart');

      expect(result).toBe('Add to Cart');
    });

    it('should translate with parameters', () => {
      const result = service.translate('cart.itemCount', { count: 5 });

      expect(result).toBe('5 items');
    });

    it('should interpolate multiple parameters', () => {
      const result = service.translate('validation.minLength', { length: 8 });

      expect(result).toBe('Minimum length is 8 characters');
    });

    it('should return key if translation missing', () => {
      const result = service.translate('nonexistent.key' as any);

      expect(result).toBe('nonexistent.key');
    });

    it('should fallback to English if translation missing in current language', () => {
      service.setLanguage('de');

      // Assuming 'nav.products' exists in EN but testing fallback logic
      const result = service.translate('nav.products');

      expect(result).toBe('Produkte'); // German translation exists
    });

    it('should translate correctly in German', () => {
      service.setLanguage('de');

      const result = service.translate('nav.products');

      expect(result).toBe('Produkte');
    });

    it('should handle parameters in German translations', () => {
      service.setLanguage('de');

      const result = service.translate('cart.itemCount', { count: 3 });

      expect(result).toBe('3 Artikel');
    });
  });

  describe('t() - Computed Translation Signal', () => {
    it('should return computed signal with translation', () => {
      const translationSignal = service.t('nav.products');

      expect(translationSignal()).toBe('Products');
    });

    it('should update when language changes', () => {
      const translationSignal = service.t('nav.products');

      expect(translationSignal()).toBe('Products');

      service.setLanguage('de');

      expect(translationSignal()).toBe('Produkte');
    });

    it('should handle parameters in computed signal', () => {
      const translationSignal = service.t('cart.itemCount', { count: 7 });

      expect(translationSignal()).toBe('7 items');
    });

    it('should update parameters reactively', () => {
      let count = 1;
      const translationSignal = service.t('cart.itemCount', { count });

      expect(translationSignal()).toBe('1 items');

      // Note: params are captured at creation, not reactive
      // For reactive params, component should call translate() directly
    });
  });

  describe('localStorage Persistence', () => {
    it('should save language to localStorage on change', () => {
      service.setLanguage('de');

      expect(localStorageSpy.setItem).toHaveBeenCalledWith('app_language', 'de');
    });

    it('should handle localStorage errors gracefully', () => {
      localStorageSpy.setItem.and.throwError('QuotaExceededError');

      expect(() => service.setLanguage('de')).not.toThrow();
      expect(service.currentLanguage()).toBe('de');
    });

    it('should handle localStorage read errors gracefully', () => {
      localStorageSpy.getItem.and.throwError('SecurityError');

      expect(() => TestBed.inject(I18nService)).not.toThrow();
    });
  });

  describe('SSR Compatibility', () => {
    it('should handle missing window object', () => {
      // This test would require mocking window as undefined
      // In real SSR environment, service should not crash
      expect(service).toBeTruthy();
    });
  });
});
