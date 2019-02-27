package org.dhis2.fhir.adapter.setup;

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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * The initial setup data of the adapter. The setup contains example or default values
 *  * that can be changed by the administrator in the user interface as appropriate.
 *
 * @author volsch
 */
public class Setup implements Serializable
{
    private static final long serialVersionUID = -5257148949174992807L;

    private boolean fhirRestInterfaceOnly;

    @Valid
    private FhirClientSetup fhirClientSetup;

    @Valid
    private OrganizationCodeSetup organizationCodeSetup;

    @Valid
    @NotNull
    private TrackedEntitySetup trackedEntitySetup = new TrackedEntitySetup();

    public boolean isFhirRestInterfaceOnly()
    {
        return fhirRestInterfaceOnly;
    }

    public void setFhirRestInterfaceOnly( boolean fhirRestInterfaceOnly )
    {
        this.fhirRestInterfaceOnly = fhirRestInterfaceOnly;
    }

    public FhirClientSetup getFhirClientSetup()
    {
        return fhirClientSetup;
    }

    public void setFhirClientSetup( FhirClientSetup fhirClientSetup )
    {
        this.fhirClientSetup = fhirClientSetup;
    }

    public OrganizationCodeSetup getOrganizationCodeSetup()
    {
        return organizationCodeSetup;
    }

    public void setOrganizationCodeSetup( OrganizationCodeSetup organizationCodeSetup )
    {
        this.organizationCodeSetup = organizationCodeSetup;
    }

    public TrackedEntitySetup getTrackedEntitySetup()
    {
        return trackedEntitySetup;
    }

    public void setTrackedEntitySetup( TrackedEntitySetup trackedEntitySetup )
    {
        this.trackedEntitySetup = trackedEntitySetup;
    }
}
