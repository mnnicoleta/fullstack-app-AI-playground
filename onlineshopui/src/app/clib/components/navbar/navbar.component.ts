import {
    ChangeDetectionStrategy,
    Component,
    computed,
    inject,
    OnInit,
    signal
} from '@angular/core';
import { UpperCasePipe } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { take } from 'rxjs';
import { AuthService } from '../../../features/auth/services/auth.service';
import { ThemeService } from '../../services/theme.service';
import { I18nService } from '../../../core/services/i18n.service';
import { NotificationsService } from '../../../core/services/notifications.service';
import { AppNavRoutes } from '../../../core/config/constants/navigation.constants';
import { IconComponent } from '../icon/icon.component';
import { LANGUAGE_METADATA, type SupportedLanguage } from '../../../core/types/i18n.types';
import { TranslatePipe } from '../../../core/pipes/translate.pipe';

@Component({
    selector: 'app-navbar',
    imports: [RouterLink, RouterLinkActive, IconComponent, TranslatePipe, UpperCasePipe],
    templateUrl: './navbar.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent implements OnInit {
    readonly authService = inject(AuthService);
    readonly themeService = inject(ThemeService);
    readonly i18nService = inject(I18nService);
    private readonly notifications = inject(NotificationsService);

    readonly showMobileMenu = signal(false);
    readonly showLanguageMenu = signal(false);

    readonly userEmail = computed(() => {
        const user = this.authService.getUser();
        return user()?.email ?? null;
    });

    readonly currentLanguageCode = this.i18nService.currentLanguage;
    readonly currentLanguageFlag = computed(() => {
        const lang = this.i18nService.currentLanguage();
        return LANGUAGE_METADATA[lang].flag;
    });

    readonly availableLanguages = Object.values(LANGUAGE_METADATA);

    readonly productsRootLink = ['/', AppNavRoutes.Products.root];
    readonly ordersLink = ['/', AppNavRoutes.Orders.root, AppNavRoutes.Orders.features.overview];
    readonly cartLink = ['/', AppNavRoutes.Cart.root, AppNavRoutes.Cart.features.overview];
    readonly loginLink = ['/', AppNavRoutes.Auth.root, AppNavRoutes.Auth.features.login];
    readonly registerLink = ['/', AppNavRoutes.Auth.root, AppNavRoutes.Auth.features.register];

    ngOnInit(): void {
        this.authService.loadProfileIfNeeded().pipe(take(1)).subscribe();
    }

    toggleTheme(): void {
        this.themeService.toggle();
    }

    toggleMobileMenu(): void {
        this.showMobileMenu.update(v => !v);
    }

    closeMobileMenu(): void {
        this.showMobileMenu.set(false);
    }

    toggleLanguageMenu(): void {
        this.showLanguageMenu.update(v => !v);
    }

    selectLanguage(lang: SupportedLanguage): void {
        this.i18nService.setLanguage(lang);
        this.showLanguageMenu.set(false);
        this.closeMobileMenu();
    }

    logout(): void {
        this.authService.logout();
        this.notifications.notifySuccess({
            title: this.i18nService.translate('notifications.logoutSuccess'),
            message: ''
        });
        this.closeMobileMenu();
    }
}
