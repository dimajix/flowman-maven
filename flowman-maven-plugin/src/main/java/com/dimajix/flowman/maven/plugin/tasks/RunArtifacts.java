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

package com.dimajix.flowman.maven.plugin.tasks;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import lombok.val;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

import com.dimajix.flowman.maven.plugin.model.Deployment;
import com.dimajix.flowman.maven.plugin.mojos.FlowmanMojo;
import com.dimajix.flowman.maven.plugin.util.Collections;


public class RunArtifacts extends Task {
    private File homeDirectory;
    private File confDirectory;


    public RunArtifacts(FlowmanMojo mojo, Deployment deployment, MavenProject mavenProject) throws MojoFailureException {
        super(mojo, deployment, mavenProject);
    }

    public RunArtifacts(FlowmanMojo mojo, Deployment deployment, MavenProject mavenProject, File homeDirectory, File confDirectory) throws MojoFailureException {
        super(mojo, deployment, mavenProject);
        this.homeDirectory = homeDirectory;
        this.confDirectory = confDirectory;
    }

    public void runTests(File projectDirectory) throws MojoExecutionException, MojoFailureException {
        run("com.dimajix.flowman.tools.exec.Driver", projectDirectory, "test", "run");
    }

    public void runShell(File projectDirectory) throws MojoExecutionException, MojoFailureException {
        run("com.dimajix.flowman.tools.shell.Shell", projectDirectory);
    }

    public void run(String mainClass, File projectDirectory, String... args) throws MojoExecutionException, MojoFailureException {
        val executionSettings = deployment.getEffectiveExecutionSettings();

        // Construct classpath
        val depres = resolveDependencies();
        val classPath = new StringBuffer();
        depres.getResolvedDependencies().stream().forEach(dep -> {
            if (classPath.length() > 0)
                classPath.append(File.pathSeparator);
            classPath.append(dep.getArtifact().getFile());
        });

        // Collect arguments
        val allArgs = new LinkedList<String>();
        val args0 = Arrays.asList(
                "-classpath",
                classPath.toString(),
                mainClass,
                "-f", projectDirectory.toString()
            );
        allArgs.addAll(executionSettings.getJavaOptions());
        allArgs.addAll(args0);
        allArgs.addAll(executionSettings.getFlowmanOptions());
        executionSettings.getProfiles().forEach(p -> {
            allArgs.add("-P");
            allArgs.add(p);
        });
        executionSettings.getEnvironment().forEach(e -> {
            allArgs.add("-D");
            allArgs.add(e);
        });
        executionSettings.getConfig().forEach(c -> {
            allArgs.add("--conf");
            allArgs.add(c);
        });
        allArgs.addAll(Arrays.asList(args));

        val systemEnvironment = new HashMap<String,String>();
        systemEnvironment.put("FLOWMAN_HOME", homeDirectory != null ? homeDirectory.toString() : "");
        systemEnvironment.put("FLOWMAN_CONF_DIR", confDirectory != null ? confDirectory.toString() : "");
        systemEnvironment.putAll(Collections.splitSettings(executionSettings.getSystemEnvironment()));

        executeMojo(
            plugin(
                groupId("org.codehaus.mojo"),
                artifactId("exec-maven-plugin"),
                version("3.1.0")
            ),
            goal("exec"),
            configuration(
                element(name("addOutputToClasspath"), "false"),
                element(name("classpathScope"), "compile"),
                element(name("inheritIo"), "true"),
                element(name("environmentVariables"),
                    systemEnvironment.entrySet().stream().map(arg -> element(name(arg.getKey()), arg.getValue())).toArray(Element[]::new)
                ),
                element(name("executable"), "java"),
                element(name("arguments"),
                    allArgs.stream().map(arg -> element(name("argument"), arg)).toArray(Element[]::new)
                )
            ),
            executionEnvironment(
                mavenProject,
                mavenSession,
                pluginManager
            )
        );
    }
}
