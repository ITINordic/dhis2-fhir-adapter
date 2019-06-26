package org.dhis2.fhir.adapter.fhir.metadata.repository.impl;

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

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgram;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CustomEnrollmentRuleRepository;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CustomEnrollmentRuleRepository}.
 *
 * @author Charles Chigoriwa (ITINORDIC)
 */
@PreAuthorize( "hasRole('DATA_MAPPING')" )
public class CustomEnrollmentRuleRepositoryImpl implements CustomEnrollmentRuleRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public CustomEnrollmentRuleRepositoryImpl( @Nonnull EntityManager entityManager )
    {
        this.entityManager = entityManager;
    }

    @Nonnull
    @RestResource( exported = false )
    @Cacheable( keyGenerator = "enrollmentRuleFindAllExpKeyGenerator", cacheManager = "metadataCacheManager", cacheNames = "enrollmentRuleRule" )
    @Transactional( readOnly = true )
    @Override
    public Collection<RuleInfo<EnrollmentRule>> findAllExp( @Nonnull Collection<Reference> programReferences )
    {
        final List<EnrollmentRule> rules;

        rules = new ArrayList<>(
            entityManager.createNamedQuery( EnrollmentRule.FIND_ALL_EXP_NAMED_QUERY, EnrollmentRule.class )
                .setParameter( "programReferences", programReferences ).getResultList() );

        return rules.stream().map( r -> {
            Hibernate.initialize( r.getDhisDataReferences() );
            return new RuleInfo<>( r, r.getDhisDataReferences() );
        } ).collect( Collectors.toList() );
    }

    @RestResource( exported = false )
    @CacheEvict( allEntries = true, cacheManager = "metadataCacheManager", cacheNames = "enrollmentRuleeRule" )
    @Transactional
    @Override
    public void deleteAllByProgram( @Nonnull MappedTrackerProgram program )
    {
        entityManager.createQuery( "DELETE FROM EnrollmentRule r WHERE r.program=:program" ).setParameter( "program", program ).executeUpdate();
    }
}
