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

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Utility methods to create comparators that are useful for transformations.
 *
 * @author volsch
 */
public abstract class TransformerComparatorUtils
{
    /**
     * Creates a constant string for the specified values that can be used
     * for comparisons in order to generate a stable sort criteria.
     *
     * @param objects the objects from which a string should be generated.
     * @return the generated string.
     */
    @Nonnull
    public static String comparatorValue( Object... objects )
    {
        final StringBuilder sb = new StringBuilder();
        for ( final Object object : objects )
        {
            sb.append( "||" );
            if ( object != null )
            {
                if ( object instanceof Collection )
                {
                    sb.append( ((Collection<?>) object).size() );
                    for ( final Object innerObject : (Collection<?>) object )
                    {
                        sb.append( "||" );
                        if ( innerObject != null )
                        {
                            sb.append( innerObject );
                        }
                    }
                }
                else
                {
                    sb.append( object );
                }
            }
        }
        return sb.toString();
    }

    private TransformerComparatorUtils()
    {
        super();
    }
}
