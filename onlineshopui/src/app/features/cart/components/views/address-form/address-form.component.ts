import { Component, input, output, ChangeDetectionStrategy } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { AddressFormGroup } from '../../../utils/address-form.utils';
import { ErrorMessageComponent } from '../../../../../clib/components/error-message/error-message.component';

@Component({
    selector: 'app-address-form',
    imports: [ReactiveFormsModule, ErrorMessageComponent],
    templateUrl: './address-form.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AddressFormComponent {
    form = input.required<AddressFormGroup>();
    isSubmitting = input<boolean>(false);
    showCancelButton = input<boolean>(false);

    formSubmit = output<void>();
    cancelled = output<void>();

    onSubmitClick(): void {
        if (this.form().valid) {
            this.formSubmit.emit();
        }
    }

    onCancelClick(): void {
        this.cancelled.emit();
    }
}
