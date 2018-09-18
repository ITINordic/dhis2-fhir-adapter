package org.dhis2.fhir.adapter.dhis.model;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ReferenceConverter}.
 *
 * @author volsch
 */
public class ReferenceConverterTest
{
    private final ReferenceConverter converter = new ReferenceConverter();

    @Test
    public void convertToDatabaseColumnNull()
    {
        Assert.assertNull( converter.convertToDatabaseColumn( null ) );
    }

    @Test
    public void convertToDatabaseColumn()
    {
        Assert.assertEquals( "CODE:678", converter.convertToDatabaseColumn( new Reference( "678", ReferenceType.CODE ) ) );
    }

    @Test
    public void convertToEntityAttributeNull()
    {
        Assert.assertNull( converter.convertToEntityAttribute( null ) );
    }

    @Test
    public void convertToEntityAttribute()
    {
        Assert.assertEquals( new Reference( "678", ReferenceType.CODE ), converter.convertToEntityAttribute( "CODE:678" ) );
    }

    @Test( expected = IllegalArgumentException.class )
    public void convertToEntityAttributeInvalidCode()
    {
        converter.convertToEntityAttribute( "COD:678" );
    }

    @Test( expected = IllegalArgumentException.class )
    public void convertToEntityAttributeInvalidValue()
    {
        converter.convertToEntityAttribute( "COD:" );
    }

    @Test( expected = IllegalArgumentException.class )
    public void convertToEntityAttributeInvalid()
    {
        converter.convertToEntityAttribute( "CODE678" );
    }
}