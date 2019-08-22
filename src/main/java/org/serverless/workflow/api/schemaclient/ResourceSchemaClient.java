/*
 *
 *   Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.serverless.workflow.api.schemaclient;

import java.io.InputStream;
import java.util.Objects;

import org.everit.json.schema.loader.SchemaClient;

public class ResourceSchemaClient implements SchemaClient {

    private final SchemaClient fallbackClient;
    private final String baseResourcePath = "/schema/workflow-01/";

    public ResourceSchemaClient(SchemaClient fallbackClient) {
        this.fallbackClient = Objects.requireNonNull(fallbackClient,
                                                     "fallbackClient cannot be null");
    }

    public InputStream get(String path) {
        path = path.substring("https://wg-serverless.org/".length());
        return this.getClass().getResourceAsStream(baseResourcePath + path);
    }
}
