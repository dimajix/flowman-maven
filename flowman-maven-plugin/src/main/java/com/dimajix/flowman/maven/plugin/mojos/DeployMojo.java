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

package com.dimajix.flowman.maven.plugin.mojos;

import java.util.Collections;

import lombok.val;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;


@Mojo(name = "deploy", threadSafe = true)
public class DeployMojo extends FlowmanMojo {
    @Parameter( property="flowman.deployment")
    protected String deployment;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        val deployments = StringUtils.isEmpty(deployment) ? getDeployments() : Collections.singletonList(getDeployment(deployment));

        for (var deployment : deployments) {
            getLog().info("");
            getLog().info("-- Deploying deployment '" + deployment.getName() + "'");

            val project = createMavenProject(deployment);
            val previousProject = mavenSession.getCurrentProject();
            try {
                mavenSession.setCurrentProject(project);
                deployment.deploy();
            }
            finally {
                mavenSession.setCurrentProject(previousProject);
            }
        }
    }
}
