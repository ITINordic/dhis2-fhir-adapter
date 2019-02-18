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
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Nonnull;

/**
 * Spring Data REST validator for {@link System}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveSystemValidator implements Validator
{
    @Override
    public boolean supports( @Nonnull Class<?> clazz )
    {
        return System.class.isAssignableFrom( clazz );
    }

    @Override
    public void validate( Object target, @Nonnull Errors errors )
    {
        final System system = (System) target;

        if ( StringUtils.isBlank( system.getName() ) )
        {
            errors.rejectValue( "name", "System.name.blank", "Name must not be blank." );
        }
        if ( StringUtils.length( system.getName() ) > System.MAX_NAME_LENGTH )
        {
            errors.rejectValue( "name", "System.name.length", new Object[]{ System.MAX_NAME_LENGTH }, "Name must not be longer than {0} characters." );
        }
        if ( StringUtils.isBlank( system.getCode() ) )
        {
            errors.rejectValue( "code", "System.code.blank", "Code must not be blank." );
        }
        else if ( system.getCode().startsWith( System.DHIS2_FHIR_ADAPTER_CODE_PREFIX ) )
        {
            errors.rejectValue( "code", "System.code.reserved", new Object[]{ System.DHIS2_FHIR_ADAPTER_CODE_PREFIX }, "Adapter defined systems (prefix {1}) cannot be created or updated." );
        }
        if ( StringUtils.length( system.getCode() ) > System.MAX_CODE_LENGTH )
        {
            errors.rejectValue( "code", "System.code.length", new Object[]{ System.MAX_CODE_LENGTH }, "Code must not be longer than {0} characters." );
        }
        if ( StringUtils.isBlank( system.getSystemUri() ) )
        {
            errors.rejectValue( "systemUri", "System.systemUri.blank", "System URI must not be blank." );
        }
        if ( StringUtils.length( system.getSystemUri() ) > System.MAX_SYSTEM_URI_LENGTH )
        {
            errors.rejectValue( "systemUri", "System.systemUri.length", new Object[]{ System.MAX_SYSTEM_URI_LENGTH }, "System URI must not be longer than {0} characters." );
        }
        if ( StringUtils.length( system.getFhirDisplayName() ) > System.MAX_FHIR_DISPLAY_NAME_LENGTH )
        {
            errors.rejectValue( "fhirDisplayName", "System.fhirDisplayName.length", new Object[]{ System.MAX_FHIR_DISPLAY_NAME_LENGTH }, "FHIR display name must not be longer than {0} characters." );
        }
    }
}
