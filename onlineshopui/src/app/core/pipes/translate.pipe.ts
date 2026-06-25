import { Pipe, PipeTransform, inject } from '@angular/core';
import { I18nService } from '../services/i18n.service';
import type { TranslationKey } from '../types/i18n.types';

/**
 * Translate pipe for use in templates
 * Usage: {{ 'nav.products' | translate }}
 * With params: {{ 'cart.itemCount' | translate:{ count: 5 } }}
 */
@Pipe({
  name: 'translate',
  standalone: true,
  pure: false // Required for signal reactivity - re-evaluates when language changes
})
export class TranslatePipe implements PipeTransform {
  private readonly i18n = inject(I18nService);

  transform(key: TranslationKey, params?: Record<string, any>): string {
    return this.i18n.translate(key, params);
  }
}
