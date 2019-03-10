package org.dhis2.fhir.adapter.fhir.metadata.repository.cache;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Key generator for {@link org.dhis2.fhir.adapter.fhir.metadata.repository.CustomRuleRepository#findAllImpByInputData(FhirResourceType, Collection)}
 * and {@link org.dhis2.fhir.adapter.fhir.metadata.repository.CustomRuleRepository#findAllExpByInputData(FhirResourceType, Collection)}.
 * Since cache may be serialized to external storage, cache is automatically a string representation.
 *
 * @author volsch
 */
@Component
public class RuleFindAllByInputDataKeyGenerator implements KeyGenerator
{
    @Override
    @Nonnull
    public Object generate( @Nonnull Object target, @Nonnull Method method, @Nonnull Object... params )
    {
        @SuppressWarnings( "unchecked" ) final Collection<SystemCodeValue> systemCodeValues = (Collection<SystemCodeValue>) params[1];
        final SortedSet<String> systemCodes;
        if ( systemCodeValues == null )
        {
            systemCodes = Collections.emptySortedSet();
        }
        else
        {
            systemCodes = systemCodeValues.stream().map( SystemCodeValue::toString )
                .collect( Collectors.toCollection( TreeSet::new ) );
        }
        final StringBuilder sb = new StringBuilder( method.getName() );
        sb.append( ',' );
        sb.append( params[0] );
        // codes must have same order every time
        for ( final String systemCode : systemCodes )
        {
            sb.append( ',' ).append( systemCode );
        }
        return sb.toString();
    }
}
