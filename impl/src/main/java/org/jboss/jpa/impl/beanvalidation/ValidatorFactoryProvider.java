package org.jboss.jpa.impl.beanvalidation;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 * Return the appropriate ValidatorFactory
 * The current implementation create a new one each time: FIX IT ;)
 *
 * @author Emmanuel Bernard
 */
public class ValidatorFactoryProvider
{
   public ValidatorFactory getValidatorFactory() {
      //FIXME get it from JNDI or the deployer
      return Validation.buildDefaultValidatorFactory();
   }
}
