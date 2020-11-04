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
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.*;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;


@SuppressWarnings("rawtypes")
public class MyDefaultGenerator  {

    private static final Logger logger= LoggerFactory.getLogger(MyDefaultGenerator.class);

    private OpenAPI openAPI ;


    public void generate(String location) throws Exception {
        SwaggerParseResult swaggerParseResult= new SwaggerParser().readWithInfo(location,null);
        this.openAPI=swaggerParseResult.getOpenAPI();
        TemplateParamVO templateParamVO=  prepareTemplateData();
        paddingTemplate(templateParamVO);


    }

    public void paddingTemplate(TemplateParamVO templateParamVO) throws Exception {


            // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号。
            Configuration configuration = new Configuration(Configuration.getVersion());
            // 第二步：设置模板文件所在的路径。
            configuration.setDirectoryForTemplateLoading(new File("D:\\jmeter"));
            // 第三步：设置模板文件使用的字符集。一般就是utf-8.
            configuration.setDefaultEncoding("utf-8");
            // 第四步：加载一个模板，创建一个模板对象。
            Template template = configuration.getTemplate("template.jmx");
            // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是Map。
//            Map dataModel = new HashMap<>();
//            //向数据集中添加数据
//            dataModel.put("hello", "this is my first freemarker test.");
            // 第六步：创建一个Writer对象，一般创建一FileWriter对象，指定生成的文件名。
            Writer out = new FileWriter(new File("D:/auto_test.jmx"));
            // 第七步：调用模板对象的process方法输出文件。
            template.process(templateParamVO, out);
            // 第八步：关闭流。
            out.close();
    }




    private Map<String, TagNode> groupTagWithName (){
        List<Tag>  tags= openAPI.getTags();
        Map<String, TagNode> tagNodeMap=new HashMap<>();
        for(int i=0;i<tags.size();i++){
            Tag tag=  tags.get(i);
            TagNode  tagNode=new TagNode();
            tagNode.setName(tag.getName());
            tagNode.setDescription(tag.getDescription());
            tagNodeMap.put(tag.getName(),tagNode);
        }

        return tagNodeMap;

    }

    public TemplateParamVO prepareTemplateData() {


        Map<String, TagNode> tagNodeMap= groupTagWithName();
        Iterator var3 = openAPI.getPaths().keySet().iterator();

        while(var3.hasNext()) {

            String resourcePath = (String)var3.next();
            PathItem path = (PathItem)openAPI.getPaths().get(resourcePath);
            RequestNode requestNode = bulid(resourcePath,path);
            Iterator<String> it= requestNode.getTag().iterator();
            while(it.hasNext()){
               String tagName= it.next();
               if(tagNodeMap.get(tagName) != null){
                   TagNode tagNode= tagNodeMap.get(tagName);
                   tagNode.getRequestNodes().add(requestNode);
               }
            }
        }

        TemplateParamVO templateParamVO=new TemplateParamVO();
        templateParamVO.setServiceName(openAPI.getInfo().getTitle());
        for(Map.Entry<String,TagNode> tagNodeEntry:tagNodeMap.entrySet()){
            if(tagNodeEntry.getValue().getRequestNodes().isEmpty()){
                continue;
            }
            templateParamVO.getTagList().add(tagNodeEntry.getValue());
        }

        return templateParamVO;
    }




    private RequestNode bulid( String resourcePath ,PathItem path){

        RequestNode requestNode=new RequestNode();
        requestNode.setRequestUrl(resourcePath);
        List<Parameter>  parameterList=path.getParameters();
        List<String> tags=new ArrayList<>();
        Operation operation=null;

        if(path.getGet() != null) {
            operation = path.getGet();
            requestNode.setHttpMethod(PathItem.HttpMethod.GET.name());
        }else if(path.getPost() != null){
            operation = path.getPost();
            requestNode.setHttpMethod(PathItem.HttpMethod.POST.name());
            requestNode.setTag(path.getPost().getTags());
            RequestBody requestBody= path.getPost().getRequestBody();
        }else if(path.getDelete() != null){
            operation =path.getDelete();
            requestNode.setHttpMethod(PathItem.HttpMethod.DELETE.name());
            tags= path.getDelete().getTags();
        }else if(path.getPut() != null){
            operation =path.getPut();
            requestNode.setHttpMethod(PathItem.HttpMethod.PUT.name());
            RequestBody requestBody= path.getPost().getRequestBody();
            this.openAPI.getComponents().getSchemas();
        }else{
            throw new RuntimeException("非法的请求方式");
        }

        requestNode.setTag(operation.getTags());
        requestNode.setOprationName(operation.getSummary());

        List<ParamNode> paramNodeList=new ArrayList<>();

        if(parameterList != null) {
            for (Parameter param : parameterList) {

                ParamNode paramNode = new ParamNode();
                if ((param instanceof QueryParameter) || "query".equalsIgnoreCase(param.getIn())) {
                    paramNode.setParamName(param.getName());
                    paramNodeList.add(paramNode);
                } else if ((param instanceof PathParameter) || "path".equalsIgnoreCase(param.getIn())) {
                    paramNode.setParamName(param.getName());
                    paramNodeList.add(paramNode);
                } else if ("body".equalsIgnoreCase(param.getIn())) {
                    paramNode.setParamName(param.getName());
                    paramNodeList.add(paramNode);
                } else if ((param instanceof HeaderParameter) || "header".equalsIgnoreCase(param.getIn())) {
                    paramNode.setParamName(param.getName());
                }

                requestNode.setParamNodes(paramNodeList);
            }
        }

        return requestNode;
    }

}
