package org.dhis2.fhir.adapter.fhir.server;

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

import org.dhis2.fhir.adapter.AbstractAppTest;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Basic tests of the end point that receives REST hook requests.
 *
 * @author volsch
 */
public class FhirServerRestHookControllerAppTest extends AbstractAppTest
{
    @Nonnull
    @Override
    protected FhirVersion getFhirVersion()
    {
        return FhirVersion.DSTU3;
    }

    @Test
    public void authWithoutPayload() throws Exception
    {
        mockMvc.perform( post( "/remote-fhir-rest-hook/{subscriptionId}/{fhirServerResourceId}",
            testConfiguration.getFhirServerId( getFhirVersion() ), testConfiguration.getFhirServerResourceId( getFhirVersion(), FhirResourceType.PATIENT ) ) )
            .andExpect( status().isUnauthorized() );
    }

    @Test
    public void notFoundSubscriptionWithoutPayload() throws Exception
    {
        mockMvc.perform( post( "/remote-fhir-rest-hook/{subscriptionId}/{fhirServerResourceId}",
            UUID.randomUUID().toString(), testConfiguration.getFhirServerResourceId( getFhirVersion(), FhirResourceType.PATIENT ) ) )
            .andExpect( status().isNotFound() );
    }

    @Test
    public void notFoundSubscriptionResourceWithoutPayload() throws Exception
    {
        mockMvc.perform( post( "/remote-fhir-rest-hook/{subscriptionId}/{fhirServerResourceId}",
            testConfiguration.getFhirServerId( getFhirVersion() ), UUID.randomUUID().toString() ) )
            .andExpect( status().isNotFound() );
    }

    @Test
    public void authWithPayload() throws Exception
    {
        mockMvc.perform( put( "/remote-fhir-rest-hook/{subscriptionId}/{fhirServerResourceId}/Patient/1",
            testConfiguration.getFhirServerId( getFhirVersion() ), testConfiguration.getFhirServerResourceId( getFhirVersion(), FhirResourceType.PATIENT ) )
            .contentType( FHIR_JSON_MEDIA_TYPE ).content( "{}" ) )
            .andExpect( status().isUnauthorized() );
    }
}
