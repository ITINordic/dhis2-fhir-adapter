package org.dhis2.fhir.adapter.fhir.transform.config;

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

import org.dhis2.fhir.adapter.dhis.DhisBasePackage;
import org.dhis2.fhir.adapter.fhir.FhirBasePackage;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.impl.ThreadLocalScriptExecutionContext;
import org.dhis2.fhir.adapter.geo.GeoBasePackage;
import org.dhis2.fhir.adapter.script.ScriptEvaluator;
import org.dhis2.fhir.adapter.script.impl.ScriptEvaluatorImpl;
import org.dhis2.fhir.adapter.scriptable.generator.JavaScriptGeneratorConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nonnull;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Configuration
@Component
@ConfigurationProperties( "dhis2.fhir-adapter.transformation" )
@Validated
public class TransformationConfig implements Serializable
{
    private static final long serialVersionUID = 8855767131640620761L;

    @NotBlank
    private String scriptEngineName;

    @NotNull
    private List<String> scriptEngineArgs;

    @Min( 1 )
    private int maxCachedScriptLifetimeSecs = 24 * 60 * 60;

    @Min( 1 )
    private int maxCachedScripts = 10000;

    public String getScriptEngineName()
    {
        return scriptEngineName;
    }

    public void setScriptEngineName( String scriptEngineName )
    {
        this.scriptEngineName = scriptEngineName;
    }

    public List<String> getScriptEngineArgs()
    {
        return scriptEngineArgs;
    }

    public void setScriptEngineArgs( List<String> scriptEngineArgs )
    {
        this.scriptEngineArgs = scriptEngineArgs;
    }

    public int getMaxCachedScriptLifetimeSecs()
    {
        return maxCachedScriptLifetimeSecs;
    }

    public void setMaxCachedScriptLifetimeSecs( int maxCachedScriptLifetimeSecs )
    {
        this.maxCachedScriptLifetimeSecs = maxCachedScriptLifetimeSecs;
    }

    public int getMaxCachedScripts()
    {
        return maxCachedScripts;
    }

    public void setMaxCachedScripts( int maxCachedScripts )
    {
        this.maxCachedScripts = maxCachedScripts;
    }

    @Bean
    @Nonnull
    protected ScriptExecutionContext scriptExecutionContext()
    {
        return new ThreadLocalScriptExecutionContext();
    }

    @Bean
    @Nonnull
    protected ScriptEvaluator scriptEvaluator()
    {
        return new ScriptEvaluatorImpl( getScriptEngineName(), scriptEngineArgs, maxCachedScriptLifetimeSecs, maxCachedScripts );
    }

    @Bean
    @Nonnull
    protected JavaScriptGeneratorConfig javaScriptGeneratorConfig()
    {
        return new JavaScriptGeneratorConfig().setBasePackageClasses(
            GeoBasePackage.class,
            DhisBasePackage.class,
            FhirBasePackage.class );
    }
}
