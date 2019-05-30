package org.dhis2.fhir.adapter.util;

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
import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * The cast utils provide helper methods to avoid that overloaded methods that receive
 * a <code>null</code> value in JavaScript executions cannot be selected.
 *
 * @author volsch
 */
public abstract class CastUtils
{
    /**
     * @param value the value that should be casted.
     * @param c1    the first assumed type of the value.
     * @param f1    the conversion function from the first assumed type to the returned type.
     * @param c2    the second assumed type of the value.
     * @param f2    the conversion function from the second assumed type to the returned type.
     * @param <T>   the concrete return type of the cast.
     * @param <V1>  the concrete type first assumed type of the specified value.
     * @param <V2>  the concrete type second assumed type of the specified value.
     * @return the casted value.
     * @throws ClassCastException thrown if the value is neither <code>null</code>
     *                            nor an instance of any specified class.
     */
    @Nullable
    public static <T, V1, V2> T cast( @Nullable Object value, @Nonnull Class<V1> c1, @Nonnull Function<V1, T> f1, @Nonnull Class<V2> c2, @Nonnull Function<V2, T> f2 ) throws ClassCastException
    {
        if ( value == null )
        {
            return null;
        }
        if ( c1.isInstance( value ) )
        {
            return f1.apply( c1.cast( value ) );
        }
        if ( c2.isInstance( value ) )
        {
            return f2.apply( c2.cast( value ) );
        }
        throw new ClassCastException( "Value of type " + value.getClass().getName() + " cannot be handled." );
    }

    /**
     * @param value the value that should be casted.
     * @param c1 the first assumed type of the value.
     * @param f1 the conversion function from the first assumed type to the returned type.
     * @param c2 the second assumed type of the value.
     * @param f2 the conversion function from the second assumed type to the returned type.
     * @param c3 the third assumed type of the value.
     * @param f3 the conversion function from the third assumed type to the returned type.
     * @param <T> the concrete return type of the cast.
     * @param <V1> the concrete type first assumed type of the specified value.
     * @param <V2> the concrete type second assumed type of the specified value.
     * @param <V3> the concrete type second assumed type of the specified value.
     * @return the casted value.
     * @throws ClassCastException thrown if the value is neither <code>null</code>
     * nor an instance of any specified class.
     */
    @Nullable
    public static <T, V1, V2, V3> T cast( @Nullable Object value, @Nonnull Class<V1> c1, @Nonnull Function<V1, T> f1, @Nonnull Class<V2> c2, @Nonnull Function<V2, T> f2, @Nonnull Class<V3> c3, @Nonnull Function<V3, T> f3 ) throws ClassCastException
    {
        if ( value == null )
        {
            return null;
        }
        if ( c1.isInstance( value ) )
        {
            return f1.apply( c1.cast( value ) );
        }
        if ( c2.isInstance( value ) )
        {
            return f2.apply( c2.cast( value ) );
        }
        if ( c3.isInstance( value ) )
        {
            return f3.apply( c3.cast( value ) );
        }
        throw new ClassCastException( "Value of type " + value.getClass().getName() + " cannot be handled." );
    }

    /**
     * @param value the value that should be casted.
     * @param c1    the first assumed type of the value.
     * @param f1    the conversion function from the first assumed type to the returned type.
     * @param c2    the second assumed type of the value.
     * @param f2    the conversion function from the second assumed type to the returned type.
     * @param c3    the third assumed type of the value.
     * @param f3    the conversion function from the third assumed type to the returned type.
     * @param c4    the third assumed type of the value.
     * @param f4    the conversion function from the fourth assumed type to the returned type.
     * @param <T>   the concrete return type of the cast.
     * @param <V1>  the concrete type first assumed type of the specified value.
     * @param <V2>  the concrete type second assumed type of the specified value.
     * @param <V3>  the concrete type second assumed type of the specified value.
     * @param <V4>  the concrete type second assumed type of the specified value.
     * @return the casted value.
     * @throws ClassCastException thrown if the value is neither <code>null</code>
     *                            nor an instance of any specified class.
     */
    @Nullable
    public static <T, V1, V2, V3, V4> T cast( @Nullable Object value, @Nonnull Class<V1> c1, @Nonnull Function<V1, T> f1, @Nonnull Class<V2> c2, @Nonnull Function<V2, T> f2, @Nonnull Class<V3> c3, @Nonnull Function<V3, T> f3,
        @Nonnull Class<V4> c4, @Nonnull Function<V4, T> f4 ) throws ClassCastException
    {
        if ( value == null )
        {
            return null;
        }
        if ( c1.isInstance( value ) )
        {
            return f1.apply( c1.cast( value ) );
        }
        if ( c2.isInstance( value ) )
        {
            return f2.apply( c2.cast( value ) );
        }
        if ( c3.isInstance( value ) )
        {
            return f3.apply( c3.cast( value ) );
        }
        if ( c4.isInstance( value ) )
        {
            return f4.apply( c4.cast( value ) );
        }
        throw new ClassCastException( "Value of type " + value.getClass().getName() + " cannot be handled." );
    }

    private CastUtils()
    {
        super();
    }
}
