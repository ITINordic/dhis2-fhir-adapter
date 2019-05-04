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
import org.dhis2.fhir.adapter.jackson.AdapterBeanPropertyFilter;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;
import org.dhis2.fhir.adapter.validator.EnumValue;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Defines a constant that can be substituted by its value in rules and transformations. E.g. the code value for a
 * gender that is defined in DHIS2 can be defined as a constant.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_constant", uniqueConstraints = {
    @UniqueConstraint( name = "fhir_constant_uk_name", columnNames = "name" ),
    @UniqueConstraint( name = "fhir_constant_uk_code", columnNames = "code" )
} )
@JsonFilter( value = AdapterBeanPropertyFilter.FILTER_NAME )
public class Constant extends VersionedBaseMetadata implements Serializable
{
    private static final long serialVersionUID = -4219974054617859678L;

    public static final int MAX_NAME_LENGTH = 230;

    public static final int MAX_CODE_LENGTH = 50;

    public static final int MAX_VALUE_LENGTH = 250;

    @NotBlank
    @Size( max = MAX_NAME_LENGTH )
    private String name;

    private String description;

    @NotNull
    @EnumValue( ConstantCategory.class )
    private ConstantCategory category;

    @NotBlank
    @Size( max = MAX_CODE_LENGTH )
    private String code;

    @NotNull
    @EnumValue( DataType.class )
    private DataType dataType;

    @Size( max = MAX_VALUE_LENGTH )
    private String value;

    @Basic
    @Column( name = "name", nullable = false, length = MAX_NAME_LENGTH )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Basic
    @Column( name = "description", columnDefinition = "TEXT" )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @Basic
    @Column( name = "category", length = 30, nullable = false )
    @Enumerated( EnumType.STRING )
    public ConstantCategory getCategory()
    {
        return category;
    }

    public void setCategory( ConstantCategory category )
    {
        this.category = category;
    }

    @Basic
    @Column( name = "code", nullable = false, length = MAX_CODE_LENGTH )
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @Basic
    @Column( name = "data_type", nullable = false, length = 30 )
    @Enumerated( EnumType.STRING )
    public DataType getDataType()
    {
        return dataType;
    }

    public void setDataType( DataType dataType )
    {
        this.dataType = dataType;
    }

    @Basic
    @Column( name = "value", length = 250 )
    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }
}
