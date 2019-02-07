package org.dhis2.fhir.adapter.fhir.metadata.repository.validator;

/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientSystem;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Nonnull;

/**
 * Spring Data REST validator for {@link FhirClientSystem}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveFhirClientSystemValidator implements Validator
{
    @Override
    public boolean supports( @Nonnull Class<?> clazz )
    {
        return FhirClientSystem.class.isAssignableFrom( clazz );
    }

    @Override
    public void validate( Object target, @Nonnull Errors errors )
    {
        final FhirClientSystem fhirClientSystem = (FhirClientSystem) target;

        if ( fhirClientSystem.getFhirClient() == null )
        {
            errors.rejectValue( "fhirClient", "FhirClientSystem.fhirClient.null", "FHIR client is mandatory." );
        }
        if ( fhirClientSystem.getFhirResourceType() == null )
        {
            errors.rejectValue( "fhirResourceType", "FhirClientSystem.fhirResourceType.null", "FHIR resource type is mandatory." );
        }
        if ( fhirClientSystem.getSystem() == null )
        {
            errors.rejectValue( "system", "FhirClientSystem.system.null", "System is mandatory." );
        }
        if ( StringUtils.length( fhirClientSystem.getCodePrefix() ) > FhirClientSystem.MAX_CODE_PREFIX_LENGTH )
        {
            errors.rejectValue( "codePrefix", "FhirClientSystem.codePrefix.length", new Object[]{ FhirClientSystem.MAX_CODE_PREFIX_LENGTH }, "Code prefix must not be longer than {0} characters." );
        }
        if ( StringUtils.length( fhirClientSystem.getDefaultValue() ) > FhirClientSystem.MAX_DEFAULT_VALUE_LENGTH )
        {
            errors.rejectValue( "defaultValue", "FhirClientSystem.defaultValue.length", new Object[]{ FhirClientSystem.MAX_DEFAULT_VALUE_LENGTH }, "Default value must not be longer than {0} characters." );
        }
    }
}
