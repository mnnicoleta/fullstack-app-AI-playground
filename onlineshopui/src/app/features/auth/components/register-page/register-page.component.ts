import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { take } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { AppNavRoutes } from '../../../../core/config/constants/navigation.constants';
import { createRegisterForm } from '../../utils/register-form.utils';
import { ErrorMessageComponent } from '../../../../clib/components/error-message/error-message.component';
import { TranslatePipe } from '../../../../core/pipes/translate.pipe';
import { NotificationsService } from '../../../../core/services/notifications.service';
import { I18nService } from '../../../../core/services/i18n.service';

@Component({
    selector: 'app-register-page',
    imports: [ReactiveFormsModule, RouterLink, ErrorMessageComponent, TranslatePipe],
    templateUrl: './register-page.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RegisterPageComponent {
    private readonly authService = inject(AuthService);
    private readonly notifications = inject(NotificationsService);
    private readonly i18n = inject(I18nService);
    readonly isSubmitting = signal(false);
    readonly errorMessage = signal<string | null>(null);

    readonly form = createRegisterForm();

    readonly loginLink = ['/', AppNavRoutes.Auth.root, AppNavRoutes.Auth.features.login];

    onSubmit() {
        this.errorMessage.set(null);

        if (this.form.invalid || this.isSubmitting()) {
            this.form.markAllAsTouched();
            return;
        }

        this.isSubmitting.set(true);

        const payload = this.form.getRawValue();

        this.authService
            .register(payload)
            .pipe(take(1))
            .subscribe({
                next: () => {
                    this.isSubmitting.set(false);
                    this.notifications.notifySuccess({
                        title: this.i18n.translate('notifications.registrationSuccess'),
                        message: ''
                    });
                },
                error: () => {
                    this.isSubmitting.set(false);
                    this.errorMessage.set('Unable to create account. Please try again.');
                    this.notifications.notifyError({
                        title: this.i18n.translate('notifications.genericError'),
                        message: 'Unable to create account. Please try again.'
                    });
                }
            });
    }
}
