package de.naju.adebar.web.validation.core;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import de.naju.adebar.model.Address;
import de.naju.adebar.web.validation.ValidatingEntityFormConverter;

/**
 * Simple service to convert {@link Address} instances to their corresponding {@link AddressForm}
 *
 * @author Rico Bergmann
 */
@Service
public class AddressFormConverter implements ValidatingEntityFormConverter<Address, AddressForm> {

  /*
   * (non-Javadoc)
   *
   * @see org.springframework.validation.Validator#supports(java.lang.Class)
   */
  @Override
  public boolean supports(Class<?> clazz) {
    return AddressForm.class.equals(clazz);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.springframework.validation.Validator#validate(java.lang.Object,
   * org.springframework.validation.Errors)
   */
  @Override
  public void validate(Object target, Errors errors) {

    // Just the default validation
    ValidationUtils.rejectIfEmpty(errors, "street", "field.required");
    ValidationUtils.rejectIfEmpty(errors, "zip", "field.required");
    ValidationUtils.rejectIfEmpty(errors, "city", "field.required");
  }

  /*
   * (non-Javadoc)
   *
   * @see de.naju.adebar.web.validation.EntityFormConverter#toEntity(java.lang.Object)
   */
  @Override
  public Address toEntity(AddressForm form) {
    return new Address(form.getStreet(), form.getZip(), form.getCity());
  }

  /*
   * (non-Javadoc)
   *
   * @see de.naju.adebar.web.validation.EntityFormConverter#toForm(java.lang.Object)
   */
  @Override
  public AddressForm toForm(Address entity) {
    return new AddressForm(entity.getStreet(), entity.getZip(), entity.getCity());
  }

}
