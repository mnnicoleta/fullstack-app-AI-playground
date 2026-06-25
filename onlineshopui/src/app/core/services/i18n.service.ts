import { Injectable, signal, computed } from '@angular/core';
import type { SupportedLanguage, TranslationsMap, TranslationKey } from '../types/i18n.types';
import { SUPPORTED_LANGUAGES } from '../types/i18n.types';
import { EN_TRANSLATIONS } from '../config/i18n/translations.en';
import { DE_TRANSLATIONS } from '../config/i18n/translations.de';

/**
 * Internationalization service
 * Provides signal-based reactive translations with localStorage persistence
 */
@Injectable({
  providedIn: 'root'
})
export class I18nService {
  private readonly STORAGE_KEY = 'app_language';
  private readonly DEFAULT_LANGUAGE: SupportedLanguage = 'en';

  // Private mutable signals
  private readonly _currentLanguage = signal<SupportedLanguage>(this.getInitialLanguage());
  private readonly _translations = signal<TranslationsMap>(this.loadTranslations(this.getInitialLanguage()));

  // Public readonly signals
  readonly currentLanguage = this._currentLanguage.asReadonly();
  readonly translations = this._translations.asReadonly();

  constructor() {
    // Apply initial language on service initialization
    this.applyLanguage(this._currentLanguage());
  }

  /**
   * Switch to a different language
   * Updates translations signal and persists choice to localStorage
   */
  setLanguage(lang: SupportedLanguage): void {
    if (!this.isSupportedLanguage(lang)) {
      console.warn(`Unsupported language: ${lang}. Falling back to ${this.DEFAULT_LANGUAGE}`);
      lang = this.DEFAULT_LANGUAGE;
    }

    this._currentLanguage.set(lang);
    this.applyLanguage(lang);
    this.persistLanguage(lang);
  }

  /**
   * Get translation for a key with optional parameter interpolation
   * @param key Translation key (e.g., 'nav.products', 'validation.minLength')
   * @param params Optional parameters for interpolation (e.g., { length: 5 })
   * @returns Translated string
   */
  translate(key: TranslationKey, params?: Record<string, any>): string {
    const translations = this._translations();
    const template = this.getNestedValue(translations, key);

    if (!template) {
      console.warn(`Translation missing: ${key} (language: ${this._currentLanguage()})`);

      // Fallback to English
      if (this._currentLanguage() !== 'en') {
        const fallback = this.getNestedValue(EN_TRANSLATIONS, key);
        if (fallback) {
          return this.interpolate(fallback, params);
        }
      }

      // Last resort: return the key itself
      return key;
    }

    return this.interpolate(template, params);
  }

  /**
   * Create a computed signal for reactive translations
   * Useful when translation needs to update automatically when language changes
   * @param key Translation key
   * @param params Optional parameters for interpolation
   * @returns Computed signal that updates when language changes
   */
  t(key: TranslationKey, params?: Record<string, any>) {
    return computed(() => {
      const translations = this._translations();
      const template = this.getNestedValue(translations, key);

      if (!template) {
        console.warn(`Translation missing: ${key} (language: ${this._currentLanguage()})`);

        // Fallback to English
        if (this._currentLanguage() !== 'en') {
          const fallback = this.getNestedValue(EN_TRANSLATIONS, key);
          if (fallback) {
            return this.interpolate(fallback, params);
          }
        }

        return key;
      }

      return this.interpolate(template, params);
    });
  }

  /**
   * Get initial language from localStorage or browser settings
   * SSR-safe with fallback to default language
   */
  private getInitialLanguage(): SupportedLanguage {
    // SSR guard
    if (typeof window === 'undefined') {
      return this.DEFAULT_LANGUAGE;
    }

    try {
      // Check localStorage first
      const stored = localStorage.getItem(this.STORAGE_KEY);
      if (stored && this.isSupportedLanguage(stored)) {
        return stored;
      }

      // Fallback to browser language detection
      const browserLang = navigator.language.split('-')[0];
      if (this.isSupportedLanguage(browserLang)) {
        return browserLang;
      }
    } catch (error) {
      console.warn('Failed to read language from localStorage:', error);
    }

    return this.DEFAULT_LANGUAGE;
  }

  /**
   * Load translation file for a given language
   */
  private loadTranslations(lang: SupportedLanguage): TranslationsMap {
    switch (lang) {
      case 'en':
        return EN_TRANSLATIONS;
      case 'de':
        return DE_TRANSLATIONS;
      default:
        console.warn(`Unknown language: ${lang}, falling back to English`);
        return EN_TRANSLATIONS;
    }
  }

  /**
   * Apply language by loading translations
   */
  private applyLanguage(lang: SupportedLanguage): void {
    const translations = this.loadTranslations(lang);
    this._translations.set(translations);
  }

  /**
   * Persist language choice to localStorage
   * SSR-safe with guard
   */
  private persistLanguage(lang: SupportedLanguage): void {
    if (typeof window === 'undefined') {
      return;
    }

    try {
      localStorage.setItem(this.STORAGE_KEY, lang);
    } catch (error) {
      console.warn('Failed to persist language to localStorage:', error);
    }
  }

  /**
   * Interpolate parameters into a translation template
   * Replaces {{param}} with values from params object
   * @param template Translation template (e.g., "Hello {{name}}")
   * @param params Parameters object (e.g., { name: "John" })
   */
  private interpolate(template: string, params?: Record<string, any>): string {
    if (!params) {
      return template;
    }

    return template.replace(/\{\{(\w+)\}\}/g, (match, key) => {
      const value = params[key];
      return value !== undefined && value !== null ? String(value) : match;
    });
  }

  /**
   * Get nested value from object using dot notation
   * @param obj Object to traverse
   * @param path Dot-separated path (e.g., "nav.products")
   */
  private getNestedValue(obj: any, path: string): string | undefined {
    return path.split('.').reduce((current, key) => current?.[key], obj);
  }

  /**
   * Type guard to check if a string is a supported language
   */
  private isSupportedLanguage(lang: string): lang is SupportedLanguage {
    return SUPPORTED_LANGUAGES.includes(lang as SupportedLanguage);
  }
}
