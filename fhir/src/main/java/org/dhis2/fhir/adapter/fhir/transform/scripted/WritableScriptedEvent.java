package org.dhis2.fhir.adapter.fhir.transform.scripted;

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
import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.WritableDataValue;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageDataElement;
import org.dhis2.fhir.adapter.fhir.transform.TransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program.FhirToDhisOptionSetUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.ScriptedDateTimeUtils;
import org.dhis2.fhir.adapter.geo.Location;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.NameUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Mutable event resource that can be used by scripts safely.
 *
 * @author volsch
 */
@Scriptable
public class WritableScriptedEvent implements ScriptedEvent, Serializable
{
    private static final long serialVersionUID = 3407593545422372222L;

    private final TransformerContext transformerContext;

    private final Program program;

    private final ProgramStage programStage;

    private final Event event;

    private final ScriptedTrackedEntityInstance trackedEntityInstance;

    private final ValueConverter valueConverter;

    public WritableScriptedEvent( @Nonnull Program program, @Nonnull ProgramStage programStage, @Nonnull Event event, @Nullable ScriptedTrackedEntityInstance trackedEntityInstance, @Nonnull ValueConverter valueConverter )
    {
        this( null, program, programStage, event, trackedEntityInstance, valueConverter );
    }

    public WritableScriptedEvent( @Nullable TransformerContext transformerContext, @Nonnull Program program, @Nonnull ProgramStage programStage, @Nonnull Event event, @Nullable ScriptedTrackedEntityInstance trackedEntityInstance,
        @Nonnull ValueConverter valueConverter )
    {
        this.transformerContext = transformerContext;
        this.program = program;
        this.programStage = programStage;
        this.event = event;
        this.trackedEntityInstance = trackedEntityInstance;
        this.valueConverter = valueConverter;
    }

    @Override
    public boolean isNewResource()
    {
        return event.isNewResource();
    }

    @Override
    public boolean isDeleted()
    {
        return event.isDeleted();
    }

    @Nullable
    @Override
    public String getId()
    {
        return event.getId();
    }

    @Nonnull
    @Override
    public DhisResourceType getResourceType()
    {
        return event.getResourceType();
    }

    @Nullable
    @Override
    public DhisResourceId getResourceId()
    {
        return event.getResourceId();
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the date and time when the resource has been updated the last time or null if this is a new resource." )
    public ZonedDateTime getLastUpdated()
    {
        return event.getLastUpdated();
    }

    @Nullable
    @Override
    public String getOrganizationUnitId()
    {
        return event.getOrgUnitId();
    }

    @Nullable
    @Override
    public String getEnrollmentId()
    {
        return event.getEnrollmentId();
    }

    @Nonnull
    @Override
    public Program getProgram()
    {
        return program;
    }

    @Nonnull
    @Override
    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    @Nullable
    @Override
    public ScriptedTrackedEntityInstance getTrackedEntityInstance()
    {
        return trackedEntityInstance;
    }

    @Nullable
    @Override
    public ZonedDateTime getEventDate()
    {
        return event.getEventDate();
    }

    public boolean setEventDate( @Nullable Object eventDate )
    {
        final ZonedDateTime zonedDateTime = ScriptedDateTimeUtils.toZonedDateTime( eventDate, valueConverter );
        if ( !Objects.equals( event.getEventDate(), zonedDateTime ) )
        {
            event.setModified( true );
        }
        event.setEventDate( zonedDateTime );
        return (eventDate != null);
    }

    @Nullable
    @Override
    public ZonedDateTime getDueDate()
    {
        return event.getDueDate();
    }

    public boolean setDueDate( @Nullable Object dueDate )
    {
        final ZonedDateTime zonedDateTime = ScriptedDateTimeUtils.toZonedDateTime( dueDate, valueConverter );
        if ( !Objects.equals( event.getDueDate(), zonedDateTime ) )
        {
            event.setModified( true );
        }
        event.setDueDate( zonedDateTime );
        return (dueDate != null);
    }

    @Nullable
    @Override
    public EventStatus getStatus()
    {
        return event.getStatus();
    }

    public boolean setStatus( @Nullable Object status )
    {
        final EventStatus convertedStatus;
        try
        {
            convertedStatus = NameUtils.toEnumValue( EventStatus.class, status );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerScriptException( "Event status has not been defined: " + status, e );
        }
        if ( !Objects.equals( event.getStatus(), convertedStatus ) )
        {
            event.setModified( true );
        }
        event.setStatus( convertedStatus );
        return true;
    }

    @Nullable
    @Override
    public Location getCoordinate()
    {
        return event.getCoordinate();
    }

    public boolean setCoordinate( @Nullable Object coordinate )
    {
        final Location convertedCoordinate = valueConverter.convert( coordinate, ValueType.COORDINATE, Location.class );
        if ( !Objects.equals( event.getCoordinate(), convertedCoordinate ) )
        {
            event.setModified( true );
        }
        event.setCoordinate( convertedCoordinate );
        return true;
    }

    @Override
    public boolean isProvidedElsewhere( @Nonnull Reference dataElementReference )
    {
        final ProgramStageDataElement dataElement = getProgramStageDataElement( dataElementReference );
        final WritableDataValue dataValue = getDataValue( dataElement );
        return dataValue.isProvidedElsewhere();
    }

    @Nullable
    @Override
    public Object getValue( @Nonnull Reference dataElementReference )
    {
        final ProgramStageDataElement dataElement = getProgramStageDataElement( dataElementReference );
        final WritableDataValue dataValue = getDataValue( dataElement );
        return dataValue.getValue();
    }

    @Nullable
    @Override
    public Boolean getBooleanValue( @Nonnull Reference dataElementReference )
    {
        return valueConverter.convert( getValue( dataElementReference ), Boolean.class );
    }

    @Nullable
    @Override
    public Integer getIntegerValue( @Nonnull Reference dataElementReference )
    {
        return valueConverter.convert( getValue( dataElementReference ), Integer.class );
    }

    @Nullable
    @Override
    public String getStringValue( @Nonnull Reference dataElementReference )
    {
        return valueConverter.convert( getValue( dataElementReference ), String.class );
    }

    public boolean setValue( @Nonnull Reference dataElementReference, @Nullable Object value ) throws TransformerException
    {
        return setValue( dataElementReference, value, null );
    }

    public boolean setValue( @Nonnull Reference dataElementReference, @Nullable Object value, @Nullable Boolean providedElsewhere ) throws TransformerException
    {
        return setValue( dataElementReference, value, providedElsewhere, null );
    }

    public boolean setValue( @Nonnull Reference dataElementReference, @Nullable Object value, @Nullable Boolean providedElsewhere, @Nullable Object lastUpdated ) throws TransformerException
    {
        return setValue( dataElementReference, value, providedElsewhere, true, lastUpdated );
    }

    public boolean setValue( @Nonnull Reference dataElementReference, @Nullable Object value, @Nullable Boolean providedElsewhere, boolean override, @Nullable Object lastUpdated ) throws TransformerException
    {
        final ProgramStageDataElement dataElement = getProgramStageDataElement( dataElementReference );
        return setValue( dataElement, value, providedElsewhere, override, ScriptedDateTimeUtils.toZonedDateTime( lastUpdated, valueConverter ) );
    }

    @Override
    @Nullable
    public Integer getIntegerOptionValue( @Nonnull Reference dataElementReference, int valueBase, @Nullable Pattern optionValuePattern )
    {
        final ProgramStageDataElement dataElement = getOptionDataElement( dataElementReference );
        final String selectedCode = valueConverter.convert( getDataValue( dataElement ).getValue(), String.class );
        if ( StringUtils.isBlank( selectedCode ) )
        {
            return null;
        }

        final List<String> codes = FhirToDhisOptionSetUtils.resolveIntegerOptionCodes( dataElement.getElement().getOptionSet(), optionValuePattern );
        final int index = codes.indexOf( selectedCode );
        if ( index < 0 )
        {
            return null;
        }

        return valueBase + index;
    }

    @Nonnull
    private ProgramStageDataElement getOptionDataElement( @Nonnull Reference dataElementReference )
    {
        final ProgramStageDataElement dataElement = getProgramStageDataElement( dataElementReference );
        if ( !dataElement.getElement().isOptionSetValue() || (dataElement.getElement().getOptionSet() == null) )
        {
            throw new TransformerMappingException( "Data element \"" + dataElementReference + "\" is not an option set." );
        }
        return dataElement;
    }

    public boolean setIntegerOptionValue( @Nonnull Reference dataElementReference, int value, int valueBase, boolean decrementAllowed, @Nullable Pattern optionValuePattern, @Nullable Boolean providedElsewhere )
    {
        final ProgramStageDataElement dataElement = getOptionDataElement( dataElementReference );
        if ( value < valueBase )
        {
            return false;
        }
        final int resultingValue = value - valueBase;

        final List<String> codes = FhirToDhisOptionSetUtils.resolveIntegerOptionCodes( dataElement.getElement().getOptionSet(), optionValuePattern );
        final int newIndex = Math.min( resultingValue, codes.size() - 1 );
        final String newCode = codes.get( newIndex );
        if ( !decrementAllowed && (newIndex > 0) )
        {
            final WritableDataValue dataValue = getDataValue( dataElement );
            if ( dataValue.getValue() != null )
            {
                final String currentCode = valueConverter.convert( dataValue.getValue(), dataElement.getElement().getValueType(), String.class );
                final int currentIndex = codes.indexOf( FhirToDhisOptionSetUtils.getIntegerOptionCode( currentCode, optionValuePattern ) );
                if ( currentIndex > newIndex )
                {
                    return false;
                }
            }
        }
        setValue( dataElement, newCode, providedElsewhere, true, null );
        return true;
    }

    @Nonnull
    private ProgramStageDataElement getProgramStageDataElement( @Nonnull Reference dataElementReference )
    {
        return programStage.getOptionalDataElement( dataElementReference ).orElseThrow( () ->
            new TransformerMappingException( "Program stage \"" + programStage.getName() +
                "\" does not include data element \"" + dataElementReference + "\"" ) );
    }

    protected boolean setValue( @Nonnull ProgramStageDataElement dataElement, Object value, Boolean providedElsewhere, boolean override, @Nullable ZonedDateTime lastUpdated )
    {
        final Object convertedValue;
        try
        {
            convertedValue = valueConverter.convert( value, dataElement.getElement().getValueType(), String.class );
        }
        catch ( ConversionException e )
        {
            throw new TransformerMappingException( "Value of data element \"" + dataElement.getElement().getName() +
                "\" could not be converted: " + e.getMessage(), e );
        }

        if ( (convertedValue != null) && dataElement.getElement().isOptionSetValue() &&
            dataElement.getElement().getOptionSet().getOptions().stream().noneMatch( o -> Objects.equals( convertedValue, o.getCode() ) ) )
        {
            throw new TransformerMappingException( "Code \"" + value + "\" is not a valid option of \"" +
                dataElement.getElement().getOptionSet().getName() + "\" for data element \"" + dataElement.getElement().getName() + "\"." );
        }

        final WritableDataValue dataValue = getDataValue( dataElement );
        if ( !override && (dataValue.getValue() != null) )
        {
            return false;
        }
        // if last update has been done on behalf of the adapter last update timestamp cannot be used since timestamps may be far behind of the timestamp of data processing
        if ( (transformerContext != null) && (lastUpdated != null) && (dataValue.getLastUpdated() != null) && dataValue.getLastUpdated().isAfter( lastUpdated ) &&
            !Objects.equals( dataValue.getStoredBy(), transformerContext.getDhisUsername() ) )
        {
            return false;
        }

        if ( !Objects.equals( convertedValue, dataValue.getValue() ) )
        {
            dataValue.setModified();
        }
        dataValue.setValue( convertedValue );

        if ( (providedElsewhere != null) && dataElement.isAllowProvidedElsewhere() )
        {
            if ( providedElsewhere != dataValue.isProvidedElsewhere() )
            {
                dataValue.setModified();
            }
            dataValue.setProvidedElsewhere( providedElsewhere );
        }
        return true;
    }

    public boolean isModified()
    {
        return event.isModified();
    }

    public boolean isAnyDataValueModified()
    {
        return event.isAnyDataValueModified();
    }

    @Nonnull
    protected WritableDataValue getDataValue( @Nonnull ProgramStageDataElement dataElement )
    {
        if ( event.isDeleted() )
        {
            return new WritableDataValue( dataElement.getElementId(), true );
        }
        return event.getDataValue( dataElement.getElementId() );
    }

    @Override
    public void validate() throws TransformerException
    {
        if ( event.getOrgUnitId() == null )
        {
            throw new TransformerMappingException( "Organization unit ID of event has not been specified." );
        }
        if ( event.getEventDate() == null )
        {
            throw new TransformerMappingException( "Event date of event has not been specified." );
        }
        if ( event.getDueDate() == null )
        {
            throw new TransformerMappingException( "Due date of event has not been specified." );
        }
    }
}
