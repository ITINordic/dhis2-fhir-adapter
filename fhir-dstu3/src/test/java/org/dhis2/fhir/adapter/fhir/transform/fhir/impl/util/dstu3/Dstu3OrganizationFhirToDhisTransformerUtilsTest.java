package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.dstu3;

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
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.fhir.metadata.model.ClientFhirEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.HierarchicallyFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecution;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Unit tests for {@link Dstu3OrganizationFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class Dstu3OrganizationFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private OrganizationUnitService organizationUnitService;

    @Mock
    private FhirClientResourceRepository fhirClientResourceRepository;

    @Mock
    private FhirResourceRepository fhirResourceRepository;

    @Mock
    private HierarchicallyFhirResourceRepository hierarchicallyFhirResourceRepository;

    @Mock
    private FhirToDhisTransformerContext context;

    @Mock
    private FhirRequest request;

    @Mock
    private ScriptExecution scriptExecution;

    @Mock
    private Map<String, Object> variables;

    @InjectMocks
    private Dstu3OrganizationFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void exists()
    {
        Mockito.doReturn( Optional.of( new OrganizationUnit() ) ).when( organizationUnitService ).findMetadataByReference( Mockito.eq( new Reference( "ABC_123", ReferenceType.CODE ) ) );
        Assert.assertTrue( utils.exists( "ABC_123" ) );
        Mockito.verify( organizationUnitService ).findMetadataByReference( Mockito.eq( new Reference( "ABC_123", ReferenceType.CODE ) ) );
    }

    @Test
    public void existsNot()
    {
        Mockito.doReturn( Optional.empty() ).when( organizationUnitService ).findMetadataByReference( Mockito.eq( new Reference( "ABC_123", ReferenceType.CODE ) ) );
        Assert.assertFalse( utils.exists( "ABC_123" ) );
        Mockito.verify( organizationUnitService ).findMetadataByReference( Mockito.eq( new Reference( "ABC_123", ReferenceType.CODE ) ) );
    }

    @Test
    public void existsWithPrefix()
    {
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "http://test.com", "OT_", null, null );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.ORGANIZATION );


        Mockito.doReturn( Optional.of( new OrganizationUnit() ) ).when( organizationUnitService ).findMetadataByReference( Mockito.eq( new Reference( "OT_ABC_123", ReferenceType.CODE ) ) );
        Assert.assertEquals( "OT_ABC_123", utils.existsWithPrefix( "ABC_123" ) );
        Mockito.verify( organizationUnitService ).findMetadataByReference( Mockito.eq( new Reference( "OT_ABC_123", ReferenceType.CODE ) ) );
    }

    @Test
    public void existsNotWithPrefix()
    {
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "http://test.com", "OT_", null, null );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.ORGANIZATION );

        Mockito.doReturn( Optional.empty() ).when( organizationUnitService ).findMetadataByReference( Mockito.eq( new Reference( "OT_ABC_123", ReferenceType.CODE ) ) );
        Assert.assertNull( utils.existsWithPrefix( "ABC_123" ) );
        Mockito.verify( organizationUnitService ).findMetadataByReference( Mockito.eq( new Reference( "OT_ABC_123", ReferenceType.CODE ) ) );
    }

    @Test
    public void findHierarchy()
    {
        final ClientFhirEndpoint clientFhirEndpoint = new ClientFhirEndpoint();
        final FhirClient fhirClient = new FhirClient();
        fhirClient.setFhirEndpoint( clientFhirEndpoint );
        final FhirContext fhirContext = FhirContext.forDstu3();
        final UUID fhirClientResourceId = UUID.randomUUID();
        final FhirClientResource fhirClientResource = new FhirClientResource();
        fhirClientResource.setFhirClient( fhirClient );
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "http://test.com", "OT_", null, null );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( fhirClientResourceId ).when( request ).getFhirClientResourceId();
        Mockito.doReturn( FhirVersion.DSTU3 ).when( request ).getVersion();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.ORGANIZATION );
        Mockito.doReturn( Optional.of( fhirClientResource ) ).when( fhirClientResourceRepository ).findOneByIdCached( Mockito.eq( fhirClientResourceId ) );
        Mockito.doReturn( Optional.of( fhirContext ) ).when( fhirResourceRepository ).findFhirContext( Mockito.eq( FhirVersion.DSTU3 ) );

        final Organization org1 = (Organization) new Organization().setId( new IdType( "Organization", "1" ) );
        final Organization org2 = (Organization) new Organization().setId( new IdType( "2" ) );
        final Organization org3 = (Organization) new Organization().setId( new IdType( ("3") ) );
        final Organization org4 = (Organization) new Organization().setId( new IdType( "Organization", "4" ) );

        Mockito.doAnswer( invocation -> {
            final Function<IBaseResource, IBaseReference> parentReferenceFunction = invocation.getArgument( 6 );
            Assert.assertEquals( org3.getPartOf(), parentReferenceFunction.apply( org3 ) );
            Assert.assertEquals( org4.getPartOf(), parentReferenceFunction.apply( org4 ) );
            return new Bundle()
                .addEntry( new Bundle.BundleEntryComponent().setResource( org3 ) )
                .addEntry( new Bundle.BundleEntryComponent().setResource( org4 ) );
        } )
            .when( hierarchicallyFhirResourceRepository )
            .findWithParents( Mockito.eq( fhirClientResourceId ), Mockito.eq( FhirVersion.DSTU3 ), Mockito.same( clientFhirEndpoint ),
                Mockito.eq( "Organization" ), Mockito.eq( "3" ), Mockito.eq( "organizationPartOf" ), Mockito.any() );

        final org.hl7.fhir.dstu3.model.Reference org2Ref = new org.hl7.fhir.dstu3.model.Reference( org2.getIdElement() );
        org2Ref.setResource( org2 );
        org1.setPartOf( org2Ref );
        org2.setPartOf( new org.hl7.fhir.dstu3.model.Reference( org3.getIdElement() ) );
        org3.setPartOf( new org.hl7.fhir.dstu3.model.Reference( org4.getIdElement() ) );

        final org.hl7.fhir.dstu3.model.Reference org1Ref = new org.hl7.fhir.dstu3.model.Reference( org1.getIdElement() );
        org1Ref.setResource( org1 );

        final List<? extends IBaseResource> hierarchy = utils.findHierarchy( org1Ref );
        Assert.assertThat( hierarchy, Matchers.contains( org1, org2 ) );
    }
}