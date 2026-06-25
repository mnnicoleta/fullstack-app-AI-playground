import { EnvironmentProviders, makeEnvironmentProviders } from '@angular/core';
import { createValidationMessages } from '../config/constants/validation.constants';
import { I18nService } from '../services/i18n.service';
import { ValidationMessages } from '../types/providers/validation-messages';

/**
 * Provides validation messages using the i18n service for translations.
 * The factory function creates messages dynamically based on the current language.
 */
export const provideValidationMessages = (): EnvironmentProviders =>
    makeEnvironmentProviders([
        {
            provide: ValidationMessages,
            useFactory: (i18n: I18nService) => createValidationMessages(i18n),
            deps: [I18nService]
        }
    ]);
