/*
 * Copyright 2022 Kaya Kupferschmidt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dimajix.flowman.maven.plugin.model;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.dimajix.flowman.maven.plugin.impl.DistDeployment;
import com.dimajix.flowman.maven.plugin.impl.JarDeployment;
import com.dimajix.flowman.maven.plugin.mojos.FlowmanMojo;
import com.dimajix.flowman.maven.plugin.util.Collections;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind", visible = false)
@JsonSubTypes({
    @JsonSubTypes.Type(name = "jar", value = JarDeployment.class),
    @JsonSubTypes.Type(name = "dist", value = DistDeployment.class)
})
public abstract class Deployment {
    @Setter
    @Getter
    private String name;

    @Getter
    @Setter
    @JsonProperty(value="flowman", required = false)
    protected FlowmanSettings flowmanSettings = new FlowmanSettings();

    @Getter
    @Setter
    @JsonProperty(value="build", required = false)
    protected BuildSettings buildSettings = new BuildSettings();

    @Getter
    @Setter
    @JsonProperty(value="execute", required = false)
    protected ExecutionSettings executionSettings = new ExecutionSettings();

    abstract public FlowmanSettings getEffectiveFlowmanSettings() throws MojoFailureException;

    abstract public BuildSettings getEffectiveBuildSettings() throws MojoFailureException;
    abstract public ExecutionSettings getEfffextiveExecutionSettings(Deployment deployment) throws MojoFailureException;
    abstract public File getBuildDirectory();


    abstract public void init(FlowmanMojo mojo) throws MojoFailureException;

    abstract public void build() throws MojoFailureException, MojoExecutionException;

    abstract public void test(File project) throws MojoFailureException, MojoExecutionException;

    abstract public void shell(File flow) throws MojoFailureException, MojoExecutionException;

    abstract public void pack() throws MojoFailureException, MojoExecutionException;

    abstract public List<Dependency> getDependencies() throws MojoFailureException, MojoExecutionException;
}
