import { TestBed } from '@angular/core/testing';
import { TranslatePipe } from './translate.pipe';
import { I18nService } from '../services/i18n.service';

describe('TranslatePipe', () => {
  let pipe: TranslatePipe;
  let i18nService: jasmine.SpyObj<I18nService>;

  beforeEach(() => {
    const i18nServiceSpy = jasmine.createSpyObj('I18nService', ['translate']);

    TestBed.configureTestingModule({
      providers: [
        TranslatePipe,
        { provide: I18nService, useValue: i18nServiceSpy }
      ]
    });

    pipe = TestBed.inject(TranslatePipe);
    i18nService = TestBed.inject(I18nService) as jasmine.SpyObj<I18nService>;
  });

  it('should create pipe', () => {
    expect(pipe).toBeTruthy();
  });

  it('should translate simple key', () => {
    i18nService.translate.and.returnValue('Products');

    const result = pipe.transform('nav.products');

    expect(result).toBe('Products');
    expect(i18nService.translate).toHaveBeenCalledWith('nav.products', undefined);
  });

  it('should translate with parameters', () => {
    i18nService.translate.and.returnValue('5 items');

    const result = pipe.transform('cart.itemCount', { count: 5 });

    expect(result).toBe('5 items');
    expect(i18nService.translate).toHaveBeenCalledWith('cart.itemCount', { count: 5 });
  });

  it('should pass through all parameters to service', () => {
    i18nService.translate.and.returnValue('Minimum length is 8 characters');

    const params = { length: 8, min: 1, max: 100 };
    pipe.transform('validation.minLength', params);

    expect(i18nService.translate).toHaveBeenCalledWith('validation.minLength', params);
  });

  it('should handle missing translation gracefully', () => {
    i18nService.translate.and.returnValue('missing.key');

    const result = pipe.transform('missing.key' as any);

    expect(result).toBe('missing.key');
  });
});
