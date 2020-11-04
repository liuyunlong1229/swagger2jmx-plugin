/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
 * Copyright 2018 SmartBear Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openapitools.codegen;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.tags.Tag;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.config.GlobalSettings;
import org.openapitools.codegen.api.TemplatingEngineAdapter;
import org.openapitools.codegen.ignore.CodegenIgnoreProcessor;
import org.openapitools.codegen.languages.PythonClientExperimentalCodegen;
import org.openapitools.codegen.meta.GeneratorMetadata;
import org.openapitools.codegen.meta.Stability;
import org.openapitools.codegen.serializer.SerializerUtils;
import org.openapitools.codegen.templating.MustacheEngineAdapter;
import org.openapitools.codegen.utils.ImplementationVersion;
import org.openapitools.codegen.utils.ModelUtils;
import org.openapitools.codegen.utils.ProcessUtils;
import org.openapitools.codegen.utils.URLPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.*;

import static org.openapitools.codegen.utils.OnceLogger.once;

@SuppressWarnings("rawtypes")
public class MyDefaultGenerator  {

    private OpenAPI openAPI ;


    public void generate(){

        Map<String,List<PathItem>> pathGroupWithTag=new HashMap<>();
        for(int i=0;i<openAPI.getPaths().size();i++){

            PathItem pathItem= openAPI.getPaths().get(i);
            pathItem.getGet().getOperationId();
            pathItem.getParameters();

        }


    }

}
