import { TestBed } from '@angular/core/testing';
import { ThemeService } from './theme.service';

describe('ThemeService', () => {
  let service: ThemeService;
  let localStorageGetItemSpy: jasmine.Spy;
  let localStorageSetItemSpy: jasmine.Spy;

  beforeEach(() => {
    // Mock localStorage BEFORE configuring TestBed
    localStorageGetItemSpy = spyOn(Storage.prototype, 'getItem').and.returnValue(null);
    localStorageSetItemSpy = spyOn(Storage.prototype, 'setItem');

    // Clear document classes
    document.documentElement.classList.remove('dark', 'theme-dark');

    TestBed.configureTestingModule({
      providers: [ThemeService],
    });
  });

  afterEach(() => {
    // Jasmine automatically restores spies after each test
  });

  describe('Initialization', () => {
    it('should be created', () => {
      service = TestBed.inject(ThemeService);
      expect(service).toBeTruthy();
    });

    it('should initialize with light mode when no theme in localStorage', () => {
      localStorageGetItemSpy.and.returnValue(null);

      service = TestBed.inject(ThemeService);

      expect(service.isDarkMode()).toBe(false);
    });

    it('should initialize with dark mode when dark theme in localStorage', () => {
      localStorageGetItemSpy.and.returnValue('dark');

      service = TestBed.inject(ThemeService);

      expect(service.isDarkMode()).toBe(true);
    });

    it('should initialize with light mode when light theme in localStorage', () => {
      localStorageGetItemSpy.and.returnValue('light');

      service = TestBed.inject(ThemeService);

      expect(service.isDarkMode()).toBe(false);
    });
  });

  describe('toggle()', () => {
    it('should toggle from light to dark mode', () => {
      localStorageGetItemSpy.and.returnValue('light');
      service = TestBed.inject(ThemeService);

      expect(service.isDarkMode()).toBe(false);

      service.toggle();

      expect(service.isDarkMode()).toBe(true);
    });

    it('should toggle from dark to light mode', () => {
      localStorageGetItemSpy.and.returnValue('dark');
      service = TestBed.inject(ThemeService);

      expect(service.isDarkMode()).toBe(true);

      service.toggle();

      expect(service.isDarkMode()).toBe(false);
    });

    it('should add dark class to document element when toggling to dark', () => {
      localStorageGetItemSpy.and.returnValue('light');
      service = TestBed.inject(ThemeService);

      service.toggle();

      expect(document.documentElement.classList.contains('dark')).toBe(true);
      expect(document.documentElement.classList.contains('theme-dark')).toBe(true);
    });

    it('should remove dark class from document element when toggling to light', () => {
      localStorageGetItemSpy.and.returnValue('dark');
      service = TestBed.inject(ThemeService);

      service.toggle();

      expect(document.documentElement.classList.contains('dark')).toBe(false);
      expect(document.documentElement.classList.contains('theme-dark')).toBe(false);
    });

    it('should persist theme to localStorage when toggling', () => {
      localStorageGetItemSpy.and.returnValue('light');
      service = TestBed.inject(ThemeService);

      service.toggle();

      expect(localStorageSetItemSpy).toHaveBeenCalledWith('theme', 'dark');
    });
  });

  describe('enableDarkMode()', () => {
    it('should enable dark mode', () => {
      localStorageGetItemSpy.and.returnValue('light');
      service = TestBed.inject(ThemeService);

      expect(service.isDarkMode()).toBe(false);

      service.enableDarkMode();

      expect(service.isDarkMode()).toBe(true);
    });

    it('should add dark classes to document element', () => {
      localStorageGetItemSpy.and.returnValue('light');
      service = TestBed.inject(ThemeService);

      service.enableDarkMode();

      expect(document.documentElement.classList.contains('dark')).toBe(true);
      expect(document.documentElement.classList.contains('theme-dark')).toBe(true);
    });

    it('should persist dark theme to localStorage', () => {
      localStorageGetItemSpy.and.returnValue('light');
      service = TestBed.inject(ThemeService);

      service.enableDarkMode();

      expect(localStorageSetItemSpy).toHaveBeenCalledWith('theme', 'dark');
    });

    it('should handle enabling dark mode when already dark', () => {
      localStorageGetItemSpy.and.returnValue('dark');
      service = TestBed.inject(ThemeService);

      expect(service.isDarkMode()).toBe(true);

      service.enableDarkMode();

      expect(service.isDarkMode()).toBe(true);
    });
  });

  describe('disableDarkMode()', () => {
    it('should disable dark mode', () => {
      localStorageGetItemSpy.and.returnValue('dark');
      service = TestBed.inject(ThemeService);

      expect(service.isDarkMode()).toBe(true);

      service.disableDarkMode();

      expect(service.isDarkMode()).toBe(false);
    });

    it('should remove dark classes from document element', () => {
      localStorageGetItemSpy.and.returnValue('dark');
      service = TestBed.inject(ThemeService);

      service.disableDarkMode();

      expect(document.documentElement.classList.contains('dark')).toBe(false);
      expect(document.documentElement.classList.contains('theme-dark')).toBe(false);
    });

    it('should persist light theme to localStorage', () => {
      localStorageGetItemSpy.and.returnValue('dark');
      service = TestBed.inject(ThemeService);

      service.disableDarkMode();

      expect(localStorageSetItemSpy).toHaveBeenCalledWith('theme', 'light');
    });

    it('should handle disabling dark mode when already light', () => {
      localStorageGetItemSpy.and.returnValue('light');
      service = TestBed.inject(ThemeService);

      expect(service.isDarkMode()).toBe(false);

      service.disableDarkMode();

      expect(service.isDarkMode()).toBe(false);
    });
  });

  describe('Edge cases', () => {
    it('should handle localStorage errors gracefully when reading', () => {
      // ThemeService catches localStorage errors in getInitialTheme()
      // It returns false (light mode) as the default when localStorage throws
      localStorageGetItemSpy.and.throwError('localStorage not available');

      // Service creation should not throw
      service = TestBed.inject(ThemeService);

      // Should default to light mode when localStorage fails
      expect(service.isDarkMode()).toBe(false);
    });

    it('should handle localStorage errors gracefully when writing', () => {
      localStorageGetItemSpy.and.returnValue('light');
      service = TestBed.inject(ThemeService);

      // Make setItem throw an error
      localStorageSetItemSpy.and.throwError('localStorage not available');

      // toggle() should not throw even if localStorage.setItem fails
      expect(() => service.toggle()).not.toThrow();

      // The signal should still update even if persistence fails
      expect(service.isDarkMode()).toBe(true);
    });

    it('should handle invalid theme value in localStorage', () => {
      localStorageGetItemSpy.and.returnValue('invalid-theme');

      service = TestBed.inject(ThemeService);

      expect(service.isDarkMode()).toBe(false);
    });
  });
});
