/*
 * Copyright 2022 The Flowman Authors
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;


@Data
public class Descriptor {
    @JsonProperty(value="flowman", required = true)
    private FlowmanSettings flowmanSettings = new FlowmanSettings();

    @JsonProperty(value="build", required = false)
    private BuildSettings buildSettings = new BuildSettings();

    @JsonProperty(value="execution", required = false)
    private ExecutionSettings executionSettings = new ExecutionSettings();

    @JsonProperty(value="projects", required = true)
    private List<File> projects = Collections.emptyList();

    @JsonProperty(value="resources", required = false)
    private List<File> resources = Collections.emptyList();

    @JsonDeserialize(converter= PackageNameResolver.class)
    @JsonProperty(value="packages", required = true)
    private Map<String, Package> packages = Collections.emptyMap();

    @JsonDeserialize(converter= DeploymentNameResolver.class)
    @JsonProperty(value="deployments", required = true)
    private Map<String, Deployment> deployments = Collections.emptyMap();


    public List<Deployment> getDeployments() {
        return deployments.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public List<Package> getPackages() {
        return packages.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
