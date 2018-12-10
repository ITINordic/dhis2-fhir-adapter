package org.dhis2.fhir.adapter.fhir.transform.dhis.impl;

/*
 * Copyright (c) 2004-2018, University of Oslo
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

import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.model.FhirVersionedValue;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerRequest;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerService;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.DhisRequest;
import org.dhis2.fhir.adapter.fhir.transform.dhis.util.DhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;
import org.dhis2.fhir.adapter.lock.LockContext;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of {@link DhisToFhirTransformerService}.
 *
 * @author volsch
 */
@Service
public class DhisToFhirTransformerServiceImpl implements DhisToFhirTransformerService
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final LockManager lockManager;

    private final Map<DhisResourceType, DhisToFhirRuleResolver> ruleResolvers = new HashMap<>();

    private final Map<FhirVersionedValue<DhisResourceType>, DhisToFhirTransformer<?, ?>> transformers = new HashMap<>();

    private final Map<FhirVersion, Map<String, DhisToFhirTransformerUtils>> transformerUtils = new HashMap<>();

    private final ScriptExecutor scriptExecutor;

    public DhisToFhirTransformerServiceImpl( @Nonnull LockManager lockManager,
        @Nonnull ObjectProvider<List<DhisToFhirRuleResolver>> ruleResolvers,
        @Nonnull ObjectProvider<List<DhisToFhirTransformer<?, ?>>> transformersProvider,
        @Nonnull ObjectProvider<List<DhisToFhirTransformerUtils>> transformUtilsProvider,
        @Nonnull ScriptExecutor scriptExecutor )
    {
        this.lockManager = lockManager;
        this.scriptExecutor = scriptExecutor;

        ruleResolvers.ifAvailable( resolvers ->
            resolvers.forEach( r -> this.ruleResolvers.put( r.getDhisResourceType(), r ) ) );
        transformersProvider.ifAvailable( transformers ->
        {
            for ( final DhisToFhirTransformer<?, ?> transformer : transformers )
            {
                for ( final FhirVersion fhirVersion : transformer.getFhirVersions() )
                {
                    this.transformers.put( new FhirVersionedValue<>( fhirVersion, transformer.getDhisResourceType() ), transformer );
                }
            }
        } );
        transformUtilsProvider.ifAvailable( dhisToFhirTransformerUtils ->
        {
            for ( final DhisToFhirTransformerUtils tu : dhisToFhirTransformerUtils )
            {
                for ( final FhirVersion fhirVersion : tu.getFhirVersions() )
                {
                    this.transformerUtils.computeIfAbsent( fhirVersion, key -> new HashMap<>() ).put( tu.getScriptAttrName(), tu );
                }
            }
        } );
    }

    @Nullable
    @Override
    public DhisToFhirTransformerRequest createTransformerRequest( @Nonnull DhisRequest dhisRequest, @Nonnull DhisResource originalInput )
    {
        final DhisResource input = Objects.requireNonNull( DhisBeanTransformerUtils.clone( originalInput ) );

        final DhisToFhirRuleResolver ruleResolver = ruleResolvers.get( dhisRequest.getResourceType() );
        if ( ruleResolver == null )
        {
            throw new TransformerMappingException( "No rule resolver can be found for DHIS resource type " + dhisRequest.getResourceType() );
        }
        final List<? extends AbstractRule> rules = ruleResolver.resolveRule( input );
        if ( rules.isEmpty() )
        {
            logger.info( "Could not find any rule to process DHIS resource." );
            return null;
        }

        final RemoteSubscription remoteSubscription = ruleResolver.resolveRemoteSubscription( input, rules );
        if ( remoteSubscription == null )
        {
            logger.info( "Could not determine remote subscription to process DHIS resource." );
            return null;
        }

        final Map<String, DhisToFhirTransformerUtils> transformerUtils = this.transformerUtils.get( remoteSubscription.getFhirVersion() );
        if ( transformerUtils == null )
        {
            throw new TransformerMappingException( "No transformer utils can be found for FHIR version " + remoteSubscription.getFhirVersion() );
        }

        return new DhisToFhirTransformerRequestImpl( new DhisToFhirTransformerContextImpl( dhisRequest, remoteSubscription ),
            ruleResolver.convert( input ), transformerUtils, rules );
    }

    @Nullable
    @Override
    public DhisToFhirTransformOutcome<? extends IBaseResource> transform( @Nonnull DhisToFhirTransformerRequest transformerRequest ) throws TransformerException
    {
        final DhisToFhirTransformerRequestImpl transformerRequestImpl = (DhisToFhirTransformerRequestImpl) transformerRequest;

        final boolean firstRule = transformerRequestImpl.isFirstRule();
        AbstractRule rule;
        while ( (rule = transformerRequestImpl.nextRule()) != null )
        {
            final DhisToFhirTransformer<?, ?> transformer = this.transformers.get(
                new FhirVersionedValue<>( transformerRequestImpl.getContext().getVersion(), rule.getDhisResourceType() ) );
            if ( transformer == null )
            {
                throw new TransformerMappingException( "No transformer can be found for FHIR version " +
                    transformerRequestImpl.getContext().getVersion() +
                    " mapping of DHIS resource type " + rule.getDhisResourceType() );
            }

            final Map<String, Object> scriptVariables = new HashMap<>( transformerRequestImpl.getTransformerUtils() );
            scriptVariables.put( ScriptVariable.CONTEXT.getVariableName(), transformerRequestImpl.getContext() );
            scriptVariables.put( ScriptVariable.INPUT.getVariableName(), transformerRequestImpl.getInput() );
            if ( isApplicable( transformerRequestImpl.getContext(), transformerRequestImpl.getInput(), rule, scriptVariables ) )
            {
                final DhisToFhirTransformOutcome<? extends IBaseResource> outcome = transformer.transformCasted(
                    transformerRequestImpl.getContext(), transformerRequestImpl.getInput(), rule, scriptVariables );
                if ( outcome != null )
                {
                    logger.info( "Rule {} used successfully for transformation of {}.",
                        rule, transformerRequestImpl.getInput().getResourceId() );
                    return new DhisToFhirTransformOutcome<>( outcome, transformerRequestImpl.isLastRule() ? null : transformerRequestImpl );
                }
                // if the previous transformation caused a lock of any resource this must be released since the transformation has been rolled back
                lockManager.getCurrentLockContext().ifPresent( LockContext::unlockAll );
            }
        }
        if ( firstRule )
        {
            logger.info( "No matching rule for {}.", transformerRequestImpl.getInput().getResourceId() );
        }
        return null;
    }

    private boolean isApplicable( @Nonnull DhisToFhirTransformerContext context, @Nonnull ScriptedDhisResource input,
        @Nonnull AbstractRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( rule.getApplicableOutScript() == null )
        {
            return true;
        }
        return Boolean.TRUE.equals( scriptExecutor.execute( rule.getApplicableOutScript(), context.getVersion(), scriptVariables, Boolean.class ) );
    }
}
