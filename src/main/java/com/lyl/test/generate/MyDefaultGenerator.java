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

package com.lyl.test.generate;

import com.lyl.test.element.ParamNode;
import com.lyl.test.element.RequestNode;
import com.lyl.test.element.TagNode;
import com.lyl.test.element.TemplateParamVO;
import com.lyl.test.parse.SwaggerParser;
import com.lyl.test.utils.ModelUtils;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.*;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


@SuppressWarnings("rawtypes")
public class MyDefaultGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyDefaultGenerator.class);

    private OpenAPI openAPI;


    public void generate(String location) throws Exception {
        SwaggerParseResult swaggerParseResult = new SwaggerParser().readWithInfo(location, null);
        this.openAPI = swaggerParseResult.getOpenAPI();
        TemplateParamVO templateParamVO = prepareTemplateData();
        paddingTemplate(templateParamVO);


    }

    public void paddingTemplate(TemplateParamVO templateParamVO) throws Exception {

        Configuration configuration = new Configuration(Configuration.getVersion());
        configuration.setClassForTemplateLoading(this.getClass(), "/templates");
        configuration.setDefaultEncoding("utf-8");
        Template template = configuration.getTemplate("template.jmx");
        Writer out = new FileWriter(new File("D:/auto_test.jmx"));
        template.process(templateParamVO, out);
        out.close();
    }


    private Map<String, TagNode> groupTagWithName() {
        List<Tag> tags = openAPI.getTags();
        Map<String, TagNode> tagNodeMap = new HashMap<>();
        for (int i = 0; i < tags.size(); i++) {
            Tag tag = tags.get(i);
            TagNode tagNode = new TagNode();
            tagNode.setName(tag.getName());
            tagNode.setDescription(tag.getDescription());
            tagNodeMap.put(tag.getName(), tagNode);
        }

        return tagNodeMap;

    }

    public TemplateParamVO prepareTemplateData() {


        Map<String, TagNode> tagNodeMap = groupTagWithName();
        Iterator var3 = openAPI.getPaths().keySet().iterator();

        while (var3.hasNext()) {

            String resourcePath = (String) var3.next();
            PathItem path = (PathItem) openAPI.getPaths().get(resourcePath);
            RequestNode requestNode = bulid(resourcePath, path);
            Iterator<String> it = requestNode.getTag().iterator();
            while (it.hasNext()) {
                String tagName = it.next();
                if (tagNodeMap.get(tagName) != null) {
                    TagNode tagNode = tagNodeMap.get(tagName);
                    tagNode.getRequestNodes().add(requestNode);
                }
            }
        }

        TemplateParamVO templateParamVO = new TemplateParamVO();
        templateParamVO.setServiceName(openAPI.getInfo().getTitle());
        for (Map.Entry<String, TagNode> tagNodeEntry : tagNodeMap.entrySet()) {
            if (tagNodeEntry.getValue().getRequestNodes().isEmpty()) {
                continue;
            }
            templateParamVO.getTagList().add(tagNodeEntry.getValue());
        }

        return templateParamVO;
    }


    private RequestNode bulid(String resourcePath, PathItem path) {

        RequestNode requestNode = new RequestNode();
        requestNode.setRequestUrl(resourcePath);

        List<String> tags = new ArrayList<>();
        Operation operation = null;

        if (path.getGet() != null) {
            operation = path.getGet();
            requestNode.setHttpMethod(PathItem.HttpMethod.GET.name());
        } else if (path.getPost() != null) {
            operation = path.getPost();
            requestNode.setHttpMethod(PathItem.HttpMethod.POST.name());
            requestNode.setTag(path.getPost().getTags());
            RequestBody requestBody = path.getPost().getRequestBody();
        } else if (path.getDelete() != null) {
            operation = path.getDelete();
            requestNode.setHttpMethod(PathItem.HttpMethod.DELETE.name());
        } else if (path.getPut() != null) {
            operation = path.getPut();
            requestNode.setHttpMethod(PathItem.HttpMethod.PUT.name());

        } else {
            throw new RuntimeException("非法的请求方式");
        }

        requestNode.setTag(operation.getTags());
        requestNode.setOprationName(StringUtils.isBlank(operation.getSummary()) ? operation.getOperationId() : operation.getSummary());


        List<ParamNode> queryParamNodes = new ArrayList<>();
        List<ParamNode> headerParamNodes = new ArrayList<>();

        List<Parameter> parameterList = operation.getParameters();


        if (operation.getRequestBody() != null) {

            RequestBody requestBody = path.getPost().getRequestBody();
            requestBody = ModelUtils.getReferencedRequestBody(this.openAPI, requestBody);
            Map<String, Schema> schemas = ModelUtils.getSchemas(this.openAPI);

            Schema schema = ModelUtils.getSchemaFromRequestBody(requestBody);

            if(schema.get$ref() !=null){
                String modelTypeNme= ModelUtils.getSimpleRef(schema.get$ref());
                Set<String> contentTypes= getConsumesInfo(openAPI,operation);
                List<Map<String, String>> exampleList = new ExampleGenerator(schemas, this.openAPI).generate((Map) null, new ArrayList(contentTypes), modelTypeNme);
                if(exampleList.size()>0){
                    Map<String, String> element= exampleList.get(0);
                    String example=element.get("example");
                    requestNode.setRequetsBody(example);
                    LOGGER.info("生成的body参数==>{}",example);
                }

              }

            }

            if (parameterList != null) {
                for (Parameter param : parameterList) {

                    ParamNode paramNode = new ParamNode();
                    if ((param instanceof QueryParameter) || "query".equalsIgnoreCase(param.getIn())) {
                        paramNode.setParamName(param.getName());
                        queryParamNodes.add(paramNode);
                    } else if ((param instanceof PathParameter) || "path".equalsIgnoreCase(param.getIn())) {
                        //paramNode.setParamName(param.getName());
                        //queryParamNodes.add(paramNode);
                        //路径参数没法在jmeter里面设置参数列表
                    } else if ("body".equalsIgnoreCase(param.getIn())) {

                    } else if ((param instanceof HeaderParameter) || "header".equalsIgnoreCase(param.getIn())) {
                        paramNode.setParamName(param.getName());
                        headerParamNodes.add(paramNode);
                    }
                }
                requestNode.setQueryParamNodes(queryParamNodes);
                requestNode.setHeaderParamNodes(headerParamNodes);
            }

            return requestNode;
        }

        public static Set<String> getConsumesInfo (OpenAPI openAPI, Operation operation){
            RequestBody requestBody = ModelUtils.getReferencedRequestBody(openAPI, operation.getRequestBody());
            return requestBody != null && requestBody.getContent() != null && !requestBody.getContent().isEmpty() ? requestBody.getContent().keySet() : Collections.emptySet();
        }

}
