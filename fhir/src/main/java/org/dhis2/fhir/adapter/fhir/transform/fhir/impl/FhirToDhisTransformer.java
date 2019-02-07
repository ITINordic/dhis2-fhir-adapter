package org.dhis2.fhir.adapter.fhir.transform.fhir.impl;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.model.FhirVersionRestricted;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Transforms a FHIR resource to a DHIS 2 resource.
 *
 * @param <R> the concrete type of the DHIS 2 resource that is processed by this transformer.
 * @param <U> the concrete type of the transformer rule that is processed by this transformer.
 * @author volsch
 */
public interface FhirToDhisTransformer<R extends DhisResource, U extends AbstractRule> extends FhirVersionRestricted
{
    @Nonnull
    DhisResourceType getDhisResourceType();

    @Nonnull
    Class<R> getDhisResourceClass();

    @Nonnull
    Class<U> getRuleClass();

    @Nullable
    FhirToDhisTransformOutcome<R> transform( @Nonnull FhirClientResource fhirClientResource, @Nonnull FhirToDhisTransformerContext context, @Nonnull IBaseResource input,
        @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    @Nullable
    FhirToDhisTransformOutcome<R> transformCasted( @Nonnull FhirClientResource fhirClientResource, @Nonnull FhirToDhisTransformerContext context, @Nonnull IBaseResource input,
        @Nonnull RuleInfo<? extends AbstractRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;
}
