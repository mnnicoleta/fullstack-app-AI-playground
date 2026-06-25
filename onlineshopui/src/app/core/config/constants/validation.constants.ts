import { I18nService } from '../../services/i18n.service';
import { ValidationMessagesMap } from '../../types/providers/validation-messages';

/**
 * Factory function that creates validation messages using the i18n service
 * @param i18n - I18nService instance for translations
 * @returns ValidationMessagesMap with translated messages
 */
export function createValidationMessages(i18n: I18nService): ValidationMessagesMap {
    return {
        required: () => i18n.translate('validation.required'),
        min: errors => i18n.translate('validation.min', { min: errors['min'] }),
        max: errors => i18n.translate('validation.max', { max: errors['max'] }),
        minlength: errors => i18n.translate('validation.minLength', { length: errors['requiredLength'] }),
        maxlength: errors => i18n.translate('validation.maxLength', { length: errors['requiredLength'] }),
        email: () => i18n.translate('validation.email'),
        pattern: () => i18n.translate('validation.pattern'),
        url: () => i18n.translate('validation.url')
    };
}
