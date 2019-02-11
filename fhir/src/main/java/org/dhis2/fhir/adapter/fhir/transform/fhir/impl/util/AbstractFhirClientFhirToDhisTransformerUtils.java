package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util;

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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArgUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.repository.FhirClientUtils;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * FHIR to DHIS2 transformer utility methods for retrieving data from a server FHIR service.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "FhirClientUtils", transformType = ScriptTransformType.IMP, var = AbstractFhirClientFhirToDhisTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities for retrieving data from a FHIR server." )
public abstract class AbstractFhirClientFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "fhirClientUtils";

    private final FhirContext fhirContext;

    private final FhirClientResourceRepository fhirClientResourceRepository;

    private final SystemCodeRepository systemCodeRepository;

    protected AbstractFhirClientFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull FhirContext fhirContext,
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository, @Nonnull SystemCodeRepository systemCodeRepository )
    {
        super( scriptExecutionContext );
        this.fhirContext = fhirContext;
        this.fhirClientResourceRepository = fhirClientResourceRepository;
        this.systemCodeRepository = systemCodeRepository;
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nonnull
    protected abstract Class<? extends IBaseBundle> getBundleClass();

    protected abstract boolean hasNextLink( @Nonnull IBaseBundle bundle );

    @Nullable
    protected abstract IBaseResource getFirstRep( @Nonnull IBaseBundle bundle );

    @Nonnull
    protected abstract List<? extends IBaseResource> getEntries( @Nonnull IBaseBundle bundle );

    @Nullable
    @ScriptMethod( description = "Returns the latest FHIR resource (based on last updated timestamp of resource) that match the specified criteria.",
        args = {
            @ScriptMethodArg( value = "resourceName", description = "The name of the FHIR resource that should be searched for (e.g. Observation)." ),
            @ScriptMethodArg( value = "referencedResourceParameter", description = "The FHIR resource search parameter that contains the referenced resource (e.g. subject)." ),
            @ScriptMethodArg( value = "referencedResourceType", description = "The FHIR resource type of the referenced resource (e.g. Patient)." ),
            @ScriptMethodArg( value = "referencedResourceId", description = "The FHIR resource ID (ID element) of the reference resource." ),
            @ScriptMethodArg( value = "filter", description = "Optional further filter argument pairs (variable arguments). The first value is the filtered parameter and the second is the value." ),
        },
        returnDescription = "The latest FHIR resource that matches the specified criteria." )
    public final IBaseResource queryLatest( @Nonnull String resourceName,
        @Nonnull String referencedResourceParameter, @Nonnull String referencedResourceType, @Nonnull IIdType referencedResourceId,
        String... filter )
    {
        final Map<String, List<String>> filterMap = createFilter( referencedResourceParameter, referencedResourceType, referencedResourceId );
        final IBaseBundle bundle = createFhirClient().search().forResource( resourceName )
            .cacheControl( new CacheControlDirective().setNoCache( true ).setMaxResults( 1 ) ).count( 1 )
            .whereMap( filterMap ).sort().descending( "_lastUpdated" ).returnBundle( getBundleClass() ).execute();
        return getFirstRep( bundle );
    }

    @Nullable
    protected IBaseResource queryLatestPrioritizedByMappedCodes( @Nonnull String resourceName,
        @Nonnull String referencedResourceParameter, @Nonnull String referencedResourceType, @Nonnull IIdType referencedResourceId,
        @Nonnull String codeParameter, @Nullable Object mappedCodes, @Nonnull Function<IBaseResource, Collection<SystemCodeValue>> systemCodeValuesMapper,
        @Nullable Integer maxCount, String... filter )
    {
        final List<String> convertedMappedCodes = ScriptArgUtils.extractStringArray( mappedCodes );
        if ( (convertedMappedCodes == null) || convertedMappedCodes.isEmpty() )
        {
            return null;
        }
        final Collection<SystemCode> systemCodes = systemCodeRepository.findAllByCodes( convertedMappedCodes );
        final Map<SystemCodeValue, Integer> systemCodeValueIndexes = systemCodes.stream().collect(
            Collectors.toMap( SystemCode::getCalculatedSystemCodeValue, sc -> convertedMappedCodes.indexOf( sc.getCode().getCode() ) ) );

        final int resultingMaxCount = (maxCount == null) ? systemCodes.size() : maxCount;
        final Map<String, List<String>> filterMap = createFilter( referencedResourceParameter, referencedResourceType, referencedResourceId );
        filterMap.put( codeParameter, Collections.singletonList(
            systemCodes.stream().map( sc -> sc.getCalculatedSystemCodeValue().toString() ).collect( Collectors.joining( "," ) ) ) );

        int processedResources = 0;
        int foundMinIndex = Integer.MAX_VALUE;
        IBaseResource foundResource = null;
        final IGenericClient client = createFhirClient();
        IBaseBundle bundle = client.search().forResource( resourceName )
            .cacheControl( new CacheControlDirective().setNoCache( true ).setNoStore( true ).setMaxResults( resultingMaxCount ) )
            .count( resultingMaxCount ).whereMap( filterMap ).sort().descending( "_lastUpdated" ).returnBundle( getBundleClass() ).execute();
        do
        {
            final List<? extends IBaseResource> resources = getEntries( bundle );
            for ( final IBaseResource resource : resources )
            {
                for ( final SystemCodeValue systemCodeValue : systemCodeValuesMapper.apply( resource ) )
                {
                    final Integer index = systemCodeValueIndexes.get( systemCodeValue );
                    if ( index != null )
                    {
                        if ( index < foundMinIndex )
                        {
                            foundMinIndex = index;
                            foundResource = resource;
                            if ( foundMinIndex == 0 )
                            {
                                return resource;
                            }
                        }
                    }
                }
                processedResources++;
            }
            if ( processedResources >= resultingMaxCount )
            {
                bundle = null;
            }
            else
            {
                bundle = hasNextLink( bundle ) ? client.loadPage().next( bundle ).execute() : null;
            }
        }
        while ( bundle != null );
        return foundResource;
    }

    @Nonnull
    protected Map<String, List<String>> createFilter( @Nonnull String referencedResourceParameter, @Nonnull String referencedResourceType, @Nonnull IIdType referencedResourceId, String... filter )
    {
        final Map<String, List<String>> filterMap = new HashMap<>();
        if ( filter.length % 2 != 0 )
        {
            throw new ScriptExecutionException( "There must be an even number of filter arguments (name/value pairs)." );
        }
        for ( int i = 0; i < filter.length; i += 2 )
        {
            filterMap.computeIfAbsent( filter[i], k -> new ArrayList<>() ).add( filter[i + 1] );
        }

        if ( referencedResourceId.hasResourceType() && !referencedResourceId.getResourceType().equals( NameUtils.toClassName( referencedResourceType ) ) )
        {
            throw new TransformerMappingException( "The referenced resource ID contains resource type " + referencedResourceId.getResourceType()
                + ", but requested resource type is " + referencedResourceType );
        }
        if ( !referencedResourceId.hasIdPart() )
        {
            throw new TransformerMappingException( "The referenced resource ID does not contain an ID part." );
        }
        filterMap.put( referencedResourceParameter, Collections.singletonList( referencedResourceType + "/" + referencedResourceId.getIdPart() ) );

        return filterMap;
    }

    @Nonnull
    protected IGenericClient createFhirClient()
    {
        final FhirToDhisTransformerContext context = getScriptVariable( ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );
        final UUID resourceId = context.getFhirRequest().getFhirClientResourceId();
        if ( resourceId == null )
        {
            throw new TransformerMappingException( "FHIR client cannot be created without having a incoming FHIR client request." );
        }
        final FhirClientResource fhirClientResource = fhirClientResourceRepository.findOneByIdCached( resourceId )
            .orElseThrow( () -> new TransformerMappingException( "Could not find FHIR client resource with ID " + resourceId ) );
        return FhirClientUtils.createClient( fhirContext, fhirClientResource.getFhirClient().getFhirEndpoint() );
    }
}
