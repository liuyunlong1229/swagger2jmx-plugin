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

package com.lyl.plugin.generate;

import com.lyl.plugin.model.ParamNode;
import com.lyl.plugin.model.RequestNode;
import com.lyl.plugin.model.TagNode;
import com.lyl.plugin.model.VariableNode;
import com.lyl.plugin.vo.TemplateParamVO;
import com.lyl.plugin.parse.SwaggerParser;
import com.lyl.plugin.utils.ModelUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.*;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


@SuppressWarnings("rawtypes")
public class MyDefaultGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyDefaultGenerator.class);

    private OpenAPI openAPI;


    public void generate(String swaggerSourceLocation,String outPutDir) throws Exception {

        //step1 :解析swagger内容，生成openApi
        SwaggerParseResult swaggerParseResult = new SwaggerParser().readWithInfo(swaggerSourceLocation, null);
        this.openAPI = swaggerParseResult.getOpenAPI();
       //step2: 封装模板参数对象
        TemplateParamVO templateParamVO = prepareTemplateData();
        //step3: 填充目标
        paddingTemplate(templateParamVO,outPutDir);


    }

    public void paddingTemplate(TemplateParamVO templateParamVO,String outPutDir) throws Exception {
        Configuration configuration = new Configuration(Configuration.getVersion());
        configuration.setClassForTemplateLoading(this.getClass(), "/templates");
        configuration.setDefaultEncoding("utf-8");
        Template template = configuration.getTemplate("template.jmx");
        File fileDir= new File(outPutDir);
        if(!fileDir.exists()){
            fileDir.mkdir();
        }
        Writer out = new FileWriter(new File(outPutDir+"/auto_test.jmx"));
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


        TemplateParamVO templateParamVO = new TemplateParamVO();
        templateParamVO.setTitle(StringUtils.isBlank(openAPI.getInfo().getTitle())?"测试计划":openAPI.getInfo().getTitle());
        templateParamVO.setDescription(openAPI.getInfo().getDescription());


        String host= "localhost";
        int port=8080;
        if(openAPI.getServers().size()> 0){
            Server server=openAPI.getServers().get(0);
            URL serverURL= getServerURL(server);
            if(serverURL != null) {
                host = serverURL.getHost();
                port = serverURL.getPort();
            }
        }

        templateParamVO.setHost(host);
        templateParamVO.setPort(port==-1?80:port);

        Map<String, TagNode> tagNodeMap = groupTagWithName();
        Iterator pathIterator = openAPI.getPaths().keySet().iterator();
        List<VariableNode> customVariableList=new ArrayList<>();
        while (pathIterator.hasNext()) {

            String resourcePath = (String) pathIterator.next();
            PathItem path = (PathItem) openAPI.getPaths().get(resourcePath);
            RequestNode requestNode = build(resourcePath, path,customVariableList);
            Iterator<String> it = requestNode.getTag().iterator();
            while (it.hasNext()) {
                String tagName = it.next();
                if (tagNodeMap.get(tagName) != null) {
                    TagNode tagNode = tagNodeMap.get(tagName);
                    tagNode.getRequestNodes().add(requestNode);
                }
            }
        }

        templateParamVO.setCustomVariableList(customVariableList);



        for (Map.Entry<String, TagNode> tagNodeEntry : tagNodeMap.entrySet()) {
            if (tagNodeEntry.getValue().getRequestNodes().isEmpty()) {
                continue;
            }
            templateParamVO.getTagList().add(tagNodeEntry.getValue());
        }

        return templateParamVO;
    }




    public  URL getServerURL(Server server) {
        String url = server.getUrl();
        if (StringUtils.isNotBlank(url)) {
            url = sanitizeUrl(url);

            try {
                return new URL(url);
            } catch (MalformedURLException var6) {
                LOGGER.warn("Not valid URL: {}. Default to {}.", server.getUrl(), "http://localhost");
            }
        }
        LOGGER.warn("Not Server URL: {}. Default to {}.", server.getUrl(), "http://localhost");
        return null;
    }
    private  String sanitizeUrl(String url) {
        if (url != null) {
            if (url.startsWith("//")) {
                url = "http:" + url;
            } else if (url.startsWith("/")) {
                url = "http://localhost" + url;
            } else if (!url.matches("[a-zA-Z][0-9a-zA-Z.+\\-]+://.+")) {
                url = "http://" + url;
            }
        }

        return url;
    }



    private RequestNode build(String resourcePath, PathItem path, List<VariableNode> customVariableList) {

        RequestNode requestNode = new RequestNode();
        requestNode.setRequestUrl(resourcePath);

        Operation operation = null;

        if (path.getGet() != null) {
            operation = path.getGet();
            requestNode.setHttpMethod(PathItem.HttpMethod.GET.name());
        } else if (path.getPost() != null) {
            operation = path.getPost();
            requestNode.setHttpMethod(PathItem.HttpMethod.POST.name());
        } else if (path.getDelete() != null) {
            operation = path.getDelete();
            requestNode.setHttpMethod(PathItem.HttpMethod.DELETE.name());
        } else if (path.getPut() != null) {
            operation = path.getPut();
            requestNode.setHttpMethod(PathItem.HttpMethod.PUT.name());
        } else {
            throw new RuntimeException("["+resourcePath+"]非法的请求方式");
        }

        requestNode.setTag(operation.getTags());
        if(StringUtils.isNotBlank(operation.getSummary())){
           String summary= operation.getSummary().replaceAll("&","&amp;");
            requestNode.setOperationName(summary);
        }else{
            requestNode.setOperationName(operation.getOperationId());
        }
        requestNode.setOperationId(operation.getOperationId());
        List<ParamNode> queryParamNodes = new ArrayList<>();
        List<ParamNode> headerParamNodes = new ArrayList<>();


        boolean requestBodyIsEmpty=true;

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
                    requestNode.setRequestBody(example);
                    requestBodyIsEmpty=false;
                    LOGGER.info("生成的body参数==>{}",example);

                }
              }

            }

            StringBuilder queryString=new StringBuilder();
            Boolean isFirstParam=true;
            List<Parameter> parameterList = operation.getParameters();
            if (parameterList != null && !parameterList.isEmpty()) {
                for (Parameter param : parameterList) {
                    String variableName=operation.getOperationId()+"."+param.getName();
                    ParamNode paramNode = new ParamNode();
                    if ((param instanceof QueryParameter) || "query".equalsIgnoreCase(param.getIn())) {
                        if(requestBodyIsEmpty){
                            paramNode.setParamName(param.getName());
                            paramNode.setParamValue("${"+variableName+"}");
                            customVariableList.add(new VariableNode(variableName,null,param.getDescription()));
                            queryParamNodes.add(paramNode);
                        }else{
                            if(isFirstParam){
                                queryString= queryString.append("?");
                                isFirstParam=false;
                            }else {
                                queryString= queryString.append("&amp;");
                            }
                            queryString= queryString.append(param.getName()).append("=").append(variableName);
                            customVariableList.add(new VariableNode(variableName,null,param.getDescription()));

                        }

                    } else if ((param instanceof PathParameter) || "path".equalsIgnoreCase(param.getIn())) {
                        //paramNode.setParamName(param.getName());
                        //queryParamNodes.add(paramNode);
                        //路径参数没法在jmeter里面设置参数列表
                        String exp="{"+param.getName()+"}";
                        String target="${"+variableName+"}";

                        requestNode.setRequestUrl(requestNode.getRequestUrl().replace(exp,target));
                        customVariableList.add(new VariableNode(variableName,null,param.getDescription()));
                    } else if ("body".equalsIgnoreCase(param.getIn())) {
                        LOGGER.info("生成的body参数");
                    } else if ((param instanceof HeaderParameter) || "header".equalsIgnoreCase(param.getIn())) {
                        paramNode.setParamName(param.getName());
                        paramNode.setParamValue("${"+variableName+"}");
                        headerParamNodes.add(paramNode);
                        customVariableList.add(new VariableNode(variableName,null,param.getDescription()));
                    }
                }

                if(StringUtils.isNotBlank(queryString.toString())){
                    requestNode.setRequestUrl(requestNode.getRequestUrl()+queryString);
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
