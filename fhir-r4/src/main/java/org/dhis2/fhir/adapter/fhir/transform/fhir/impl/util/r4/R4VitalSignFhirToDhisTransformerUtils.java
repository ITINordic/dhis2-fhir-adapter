package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.r4;

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

import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractVitalSignFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.model.HeightUnit;
import org.dhis2.fhir.adapter.model.WeightUnit;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.Quantity;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Set;

@Component
@Scriptable
public class R4VitalSignFhirToDhisTransformerUtils extends AbstractVitalSignFhirToDhisTransformerUtils
{
    public static final String UNIT_SYSTEM = "http://unitsofmeasure.org";

    public R4VitalSignFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Nullable
    @Override
    public Double getWeight( @Nullable ICompositeType value, @Nullable Object weightUnit, boolean round ) throws TransformerDataException
    {
        if ( value == null )
        {
            return null;
        }

        if ( weightUnit == null )
        {
            throw new TransformerMappingException( "Weight unit has not been specified." );
        }
        final WeightUnit resultingWeightUnit;
        try
        {
            resultingWeightUnit = NameUtils.toEnumValue( WeightUnit.class, weightUnit );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerMappingException( "Specified weight unit is invalid: " + weightUnit );
        }

        if ( !(value instanceof Quantity) )
        {
            throw new TransformerDataException( "Weight must be included as quantity, but element is " + value.getClass().getSimpleName() + "." );
        }
        final Quantity quantity = (Quantity) value;

        if ( !UNIT_SYSTEM.equals( quantity.getSystem() ) )
        {
            throw new TransformerDataException( UNIT_SYSTEM + " is expected as unit system: " + quantity.getSystem() );
        }
        final WeightUnit actualWeightUnit = WeightUnit.getByUcumCode( quantity.getCode() );
        if ( actualWeightUnit == null )
        {
            throw new TransformerDataException( "Unknown UCUM weight unit code: " + quantity.getCode() );
        }

        final BigDecimal actualValue = quantity.getValue();
        if ( actualValue == null )
        {
            return null;
        }
        final double convertedValue = actualWeightUnit.convertTo( actualValue.doubleValue(), resultingWeightUnit );
        return round ? Math.round( convertedValue ) : convertedValue;
    }

    @Nullable
    @Override
    public Double getHeight( @Nullable ICompositeType value, @Nullable Object heightUnit, boolean round ) throws TransformerDataException
    {
        if ( value == null )
        {
            return null;
        }

        if ( heightUnit == null )
        {
            throw new TransformerMappingException( "Height unit has not been specified." );
        }
        final HeightUnit resultingHeightUnit;
        try
        {
            resultingHeightUnit = NameUtils.toEnumValue( HeightUnit.class, heightUnit );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerMappingException( "Specified height unit is invalid: " + heightUnit );
        }

        if ( !(value instanceof Quantity) )
        {
            throw new TransformerDataException( "Height must be included as quantity, but element is " + value.getClass().getSimpleName() + "." );
        }
        final Quantity quantity = (Quantity) value;

        if ( !UNIT_SYSTEM.equals( quantity.getSystem() ) )
        {
            throw new TransformerDataException( UNIT_SYSTEM + " is expected as unit system: " + quantity.getSystem() );
        }
        final HeightUnit actualHeightUnit = HeightUnit.getByUcumCode( quantity.getCode() );
        if ( actualHeightUnit == null )
        {
            throw new TransformerDataException( "Unknown UCUM height unit code: " + quantity.getCode() );
        }

        final BigDecimal actualValue = quantity.getValue();
        if ( actualValue == null )
        {
            return null;
        }
        final double convertedValue = actualHeightUnit.convertTo( actualValue.doubleValue(), resultingHeightUnit );
        return round ? Math.round( convertedValue ) : convertedValue;
    }
}
