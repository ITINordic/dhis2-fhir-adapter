package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.jackson.AdapterBeanPropertyFilter;

import javax.annotation.Nonnull;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Charles Chigoriwa (ITINORDIC)
 */
@Entity
@Table( name = "fhir_enrollment_rule" )
@DiscriminatorValue( "ENROLLMENT" )
@NamedQueries( {
    @NamedQuery( name = EnrollmentRule.FIND_ALL_EXP_NAMED_QUERY,
        query = "SELECT er FROM EnrollmentRule er JOIN er.program p WHERE "
            + "er.enabled=true AND er.expEnabled=true AND (er.fhirCreateEnabled=true OR er.fhirUpdateEnabled=true) AND er.transformExpScript IS NOT NULL AND "
            + "p.enabled=true AND p.expEnabled=true AND (p.fhirCreateEnabled=true OR p.fhirUpdateEnabled=true) AND "
            + "p.programReference IN (:programReferences)" )
} )
@JsonFilter( value = AdapterBeanPropertyFilter.FILTER_NAME )
public class EnrollmentRule extends AbstractRule
{

    private static final long serialVersionUID = 3878610804052444321L;

    public static final String FIND_ALL_EXP_NAMED_QUERY = "EnrollmentRule.findAllExportedWithoutDataRef";

    private MappedTrackerProgram program;

    public EnrollmentRule()
    {
        super( DhisResourceType.ENROLLMENT );
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "program_id", referencedColumnName = "id", nullable = false )
    public MappedTrackerProgram getProgram()
    {
        return program;
    }

    public void setProgram( MappedTrackerProgram program )
    {
        this.program = program;
    }


    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirCreateEnable()
    {
        return isExpEnabled() && isFhirCreateEnabled();
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirUpdateEnable()
    {
        return isExpEnabled() && isFhirUpdateEnabled();
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirDeleteEnable()
    {
        return isExpEnabled() && isFhirDeleteEnabled();
    }

    @Override
    public boolean coversExecutedRule( @Nonnull AbstractRule executedRule )
    {
        return executedRule instanceof EnrollmentRule && ( (EnrollmentRule) executedRule )
            .getProgram().getId().equals( getProgram().getId() );
    }
}
