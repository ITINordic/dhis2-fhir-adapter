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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Base implementation of scripted metadata.
 *
 * @author volsch
 */
public class ImmutableScriptedDhisMetadata implements ScriptedDhisMetadata, Serializable
{
    private static final long serialVersionUID = -3081103677950925231L;

    private final ScriptedDhisMetadata metadata;

    public ImmutableScriptedDhisMetadata( @Nonnull ScriptedDhisMetadata metadata )
    {
        this.metadata = metadata;
    }

    @Override
    @Nullable
    public String getCode()
    {
        return metadata.getCode();
    }

    @Override
    @Nullable
    public String getName()
    {
        return metadata.getName();
    }

    @Override
    @Nullable
    public String getId()
    {
        return metadata.getId();
    }

    @Override
    @Nonnull
    public DhisResourceType getResourceType()
    {
        return metadata.getResourceType();
    }

    @Override
    @Nullable
    public DhisResourceId getResourceId()
    {
        return metadata.getResourceId();
    }

    @Override
    public boolean isNewResource()
    {
        return metadata.isNewResource();
    }

    @Override
    public boolean isLocal()
    {
        return metadata.isLocal();
    }

    @Override
    public boolean isDeleted()
    {
        return metadata.isDeleted();
    }

    @Override
    @Nullable
    public ZonedDateTime getLastUpdated()
    {
        return metadata.getLastUpdated();
    }

    @Override
    @Nullable
    public String getOrganizationUnitId()
    {
        return metadata.getOrganizationUnitId();
    }

    @Override
    @Nullable
    public ScriptedTrackedEntityInstance getTrackedEntityInstance()
    {
        return metadata.getTrackedEntityInstance();
    }

    @Override
    public void validate() throws TransformerException
    {
        metadata.validate();
    }
}
