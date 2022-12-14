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

import com.google.common.io.Resources;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public class DescriptorTest {
    @Test
    public void testDeserialization() throws IOException {
        val url = Resources.getResource(DescriptorTest.class, "descriptor-1.yml");
        val dep = ObjectMapper.read(url, Descriptor.class);

        assertThat(dep.getFlowmanSettings().getVersion()).isEqualTo("0.28.0");
    }
}
