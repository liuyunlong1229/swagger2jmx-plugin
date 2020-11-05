package com.lyl.test.utils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.callbacks.Callback;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ModelUtils {


    private static final Logger LOGGER = LoggerFactory.getLogger(ModelUtils.class);
    private static final String URI_FORMAT = "uri";
    private static final String generateAliasAsModelKey = "generateAliasAsModel";

    public ModelUtils() {
    }

    public static void setGenerateAliasAsModel(boolean value) {
        // GlobalSettings.setProperty("generateAliasAsModel", Boolean.toString(value));
    }

    public static boolean isGenerateAliasAsModel() {
        // return Boolean.parseBoolean(GlobalSettings.getProperty("generateAliasAsModel", "false"));
        return false;
    }

//    public static CodegenModel getModelByName(String name, Map<String, Object> models) {
//        Object data = models.get(name);
//        if (data instanceof Map) {
//            Map<?, ?> dataMap = (Map)data;
//            Object dataModels = dataMap.get("models");
//            if (dataModels instanceof List) {
//                List<?> dataModelsList = (List)dataModels;
//                Iterator var6 = dataModelsList.iterator();
//
//                while(var6.hasNext()) {
//                    Object entry = var6.next();
//                    if (entry instanceof Map) {
//                        Map<?, ?> entryMap = (Map)entry;
//                        Object model = entryMap.get("model");
//                        if (model instanceof CodegenModel) {
//                            return (CodegenModel)model;
//                        }
//                    }
//                }
//            }
//        }
//
//        return null;
//    }

    public static List<String> getAllUsedSchemas(OpenAPI openAPI) {
        Map<String, List<String>> childrenMap = getChildrenMap(openAPI);
        List<String> allUsedSchemas = new ArrayList();
        visitOpenAPI(openAPI, (s, t) -> {
            if (s.get$ref() != null) {
                String ref = getSimpleRef(s.get$ref());
                if (!allUsedSchemas.contains(ref)) {
                    allUsedSchemas.add(ref);
                }

                if (childrenMap.containsKey(ref)) {
                    Iterator var5 = ((List)childrenMap.get(ref)).iterator();

                    while(var5.hasNext()) {
                        String child = (String)var5.next();
                        if (!allUsedSchemas.contains(child)) {
                            allUsedSchemas.add(child);
                        }
                    }
                }
            }

        });
        return allUsedSchemas;
    }



    private static void visitOpenAPI(OpenAPI openAPI, ModelUtils.OpenAPISchemaVisitor visitor) {
        Map<String, PathItem> paths = openAPI.getPaths();
        List<String> visitedSchemas = new ArrayList();
        if (paths != null) {
            Iterator var4 = paths.values().iterator();

            while(var4.hasNext()) {
                PathItem path = (PathItem)var4.next();
                visitPathItem(path, openAPI, visitor, visitedSchemas);
            }
        }

    }

    private static void visitPathItem(PathItem pathItem, OpenAPI openAPI, ModelUtils.OpenAPISchemaVisitor visitor, List<String> visitedSchemas) {
        List<Operation> allOperations = pathItem.readOperations();
        if (allOperations != null) {
            Iterator var5 = allOperations.iterator();

            label76:
            while(true) {
                Operation operation;
                Iterator var8;
                Iterator var11;
                label60:
                do {
                    if (!var5.hasNext()) {
                        break label76;
                    }

                    operation = (Operation)var5.next();
                    visitParameters(openAPI, operation.getParameters(), visitor, visitedSchemas);
                    RequestBody requestBody = getReferencedRequestBody(openAPI, operation.getRequestBody());
                    if (requestBody != null) {
                        visitContent(openAPI, requestBody.getContent(), visitor, visitedSchemas);
                    }

                    if (operation.getResponses() != null) {
                        var8 = operation.getResponses().values().iterator();

                        while(true) {
                            ApiResponse apiResponse;
                            do {
                                do {
                                    if (!var8.hasNext()) {
                                        continue label60;
                                    }

                                    ApiResponse r = (ApiResponse)var8.next();
                                    apiResponse = getReferencedApiResponse(openAPI, r);
                                } while(apiResponse == null);

                                visitContent(openAPI, apiResponse.getContent(), visitor, visitedSchemas);
                            } while(apiResponse.getHeaders() == null);

                            Header header;
                            for(var11 = apiResponse.getHeaders().entrySet().iterator(); var11.hasNext(); visitContent(openAPI, header.getContent(), visitor, visitedSchemas)) {
                                Map.Entry<String, Header> e = (Map.Entry)var11.next();
                                header = getReferencedHeader(openAPI, (Header)e.getValue());
                                if (header.getSchema() != null) {
                                    visitSchema(openAPI, header.getSchema(), (String)e.getKey(), visitedSchemas, visitor);
                                }
                            }
                        }
                    }
                } while(operation.getCallbacks() == null);

                var8 = operation.getCallbacks().values().iterator();

                while(true) {
                    Callback callback;
                    do {
                        if (!var8.hasNext()) {
                            continue label76;
                        }

                        Callback c = (Callback)var8.next();
                        callback = getReferencedCallback(openAPI, c);
                    } while(callback == null);

                    var11 = callback.values().iterator();

                    while(var11.hasNext()) {
                        PathItem p = (PathItem)var11.next();
                        visitPathItem(p, openAPI, visitor, visitedSchemas);
                    }
                }
            }
        }

        visitParameters(openAPI, pathItem.getParameters(), visitor, visitedSchemas);
    }

    private static void visitParameters(OpenAPI openAPI, List<Parameter> parameters, ModelUtils.OpenAPISchemaVisitor visitor, List<String> visitedSchemas) {
        if (parameters != null) {
            Iterator var4 = parameters.iterator();

            while(var4.hasNext()) {
                Parameter p = (Parameter)var4.next();
                Parameter parameter = getReferencedParameter(openAPI, p);
                if (parameter != null) {
                    if (parameter.getSchema() != null) {
                        visitSchema(openAPI, parameter.getSchema(), (String)null, visitedSchemas, visitor);
                    }

                    visitContent(openAPI, parameter.getContent(), visitor, visitedSchemas);
                } else {
                    LOGGER.warn("Unreferenced parameter(s) found.");
                }
            }
        }

    }

    private static void visitContent(OpenAPI openAPI, Content content, ModelUtils.OpenAPISchemaVisitor visitor, List<String> visitedSchemas) {
        if (content != null) {
            Iterator var4 = content.entrySet().iterator();

            while(var4.hasNext()) {
                Map.Entry<String, MediaType> e = (Map.Entry)var4.next();
                if (((MediaType)e.getValue()).getSchema() != null) {
                    visitSchema(openAPI, ((MediaType)e.getValue()).getSchema(), (String)e.getKey(), visitedSchemas, visitor);
                }
            }
        }

    }

    private static void visitSchema(OpenAPI openAPI, Schema schema, String mimeType, List<String> visitedSchemas, ModelUtils.OpenAPISchemaVisitor visitor) {
        visitor.visit(schema, mimeType);
        if (schema.get$ref() != null) {
            String ref = getSimpleRef(schema.get$ref());
            if (!visitedSchemas.contains(ref)) {
                visitedSchemas.add(ref);
                Schema referencedSchema = (Schema)getSchemas(openAPI).get(ref);
                if (referencedSchema != null) {
                    visitSchema(openAPI, referencedSchema, mimeType, visitedSchemas, visitor);
                }
            }
        }

        Schema property;
        Iterator var13;
        if (schema instanceof ComposedSchema) {
            List<Schema> oneOf = ((ComposedSchema)schema).getOneOf();
            if (oneOf != null) {
                var13 = oneOf.iterator();

                while(var13.hasNext()) {
                    property = (Schema)var13.next();
                    visitSchema(openAPI, property, mimeType, visitedSchemas, visitor);
                }
            }

            List<Schema> allOf = ((ComposedSchema)schema).getAllOf();
            if (allOf != null) {
                Iterator var16 = allOf.iterator();

                while(var16.hasNext()) {
                    Schema s = (Schema)var16.next();
                    visitSchema(openAPI, s, mimeType, visitedSchemas, visitor);
                }
            }

            List<Schema> anyOf = ((ComposedSchema)schema).getAnyOf();
            if (anyOf != null) {
                Iterator var18 = anyOf.iterator();

                while(var18.hasNext()) {
                    Schema s = (Schema)var18.next();
                    visitSchema(openAPI, s, mimeType, visitedSchemas, visitor);
                }
            }
        } else if (schema instanceof ArraySchema) {
            Schema itemsSchema = ((ArraySchema)schema).getItems();
            if (itemsSchema != null) {
                visitSchema(openAPI, itemsSchema, mimeType, visitedSchemas, visitor);
            }
        } else if (isMapSchema(schema)) {
            Object additionalProperties = schema.getAdditionalProperties();
            if (additionalProperties instanceof Schema) {
                visitSchema(openAPI, (Schema)additionalProperties, mimeType, visitedSchemas, visitor);
            }
        }

        if (schema.getNot() != null) {
            visitSchema(openAPI, schema.getNot(), mimeType, visitedSchemas, visitor);
        }

        Map<String, Schema> properties = schema.getProperties();
        if (properties != null) {
            var13 = properties.values().iterator();

            while(var13.hasNext()) {
                property = (Schema)var13.next();
                visitSchema(openAPI, property, mimeType, visitedSchemas, visitor);
            }
        }

    }

    public static String getSimpleRef(String ref) {
        if (ref.startsWith("#/components/")) {
            ref = ref.substring(ref.lastIndexOf("/") + 1);
        } else {
            if (!ref.startsWith("#/definitions/")) {
                LOGGER.warn("Failed to get the schema name: {}", ref);
                return null;
            }

            ref = ref.substring(ref.lastIndexOf("/") + 1);
        }

        return ref;
    }

    public static boolean isObjectSchema(Schema schema) {
        if (schema instanceof ObjectSchema) {
            return true;
        } else if ("object".equals(schema.getType()) && !(schema instanceof MapSchema)) {
            return true;
        } else {
            return schema.getType() == null && schema.getProperties() != null && !schema.getProperties().isEmpty();
        }
    }

    public static boolean isComposedSchema(Schema schema) {
        return schema instanceof ComposedSchema;
    }

    public static boolean isMapSchema(Schema schema) {
        if (schema instanceof MapSchema) {
            return true;
        } else if (schema == null) {
            return false;
        } else if (schema.getAdditionalProperties() instanceof Schema) {
            return true;
        } else {
            return schema.getAdditionalProperties() instanceof Boolean && (Boolean)schema.getAdditionalProperties();
        }
    }

    public static boolean isArraySchema(Schema schema) {
        return schema instanceof ArraySchema;
    }

    public static boolean isSet(Schema schema) {
        return isArraySchema(schema) && Boolean.TRUE.equals(schema.getUniqueItems());
    }

    public static boolean isStringSchema(Schema schema) {
        return schema instanceof StringSchema || "string".equals(schema.getType());
    }

    public static boolean isIntegerSchema(Schema schema) {
        if (schema instanceof IntegerSchema) {
            return true;
        } else {
            return "integer".equals(schema.getType());
        }
    }

    public static boolean isShortSchema(Schema schema) {
        return "integer".equals(schema.getType()) && "int32".equals(schema.getFormat());
    }

    public static boolean isLongSchema(Schema schema) {
        return "integer".equals(schema.getType()) && "int64".equals(schema.getFormat());
    }

    public static boolean isBooleanSchema(Schema schema) {
        if (schema instanceof BooleanSchema) {
            return true;
        } else {
            return "boolean".equals(schema.getType());
        }
    }

    public static boolean isNumberSchema(Schema schema) {
        if (schema instanceof NumberSchema) {
            return true;
        } else {
            return "number".equals(schema.getType());
        }
    }

    public static boolean isFloatSchema(Schema schema) {
        return "number".equals(schema.getType()) && "float".equals(schema.getFormat());
    }

    public static boolean isDoubleSchema(Schema schema) {
        return "number".equals(schema.getType()) && "double".equals(schema.getFormat());
    }

    public static boolean isDateSchema(Schema schema) {
        if (schema instanceof DateSchema) {
            return true;
        } else {
            return "string".equals(schema.getType()) && "date".equals(schema.getFormat());
        }
    }

    public static boolean isDateTimeSchema(Schema schema) {
        if (schema instanceof DateTimeSchema) {
            return true;
        } else {
            return "string".equals(schema.getType()) && "date-time".equals(schema.getFormat());
        }
    }

    public static boolean isPasswordSchema(Schema schema) {
        if (schema instanceof PasswordSchema) {
            return true;
        } else {
            return "string".equals(schema.getType()) && "password".equals(schema.getFormat());
        }
    }

    public static boolean isByteArraySchema(Schema schema) {
        if (schema instanceof ByteArraySchema) {
            return true;
        } else {
            return "string".equals(schema.getType()) && "byte".equals(schema.getFormat());
        }
    }

    public static boolean isBinarySchema(Schema schema) {
        if (schema instanceof BinarySchema) {
            return true;
        } else {
            return "string".equals(schema.getType()) && "binary".equals(schema.getFormat());
        }
    }

    public static boolean isFileSchema(Schema schema) {
        return schema instanceof FileSchema ? true : isBinarySchema(schema);
    }

    public static boolean isUUIDSchema(Schema schema) {
        if (schema instanceof UUIDSchema) {
            return true;
        } else {
            return "string".equals(schema.getType()) && "uuid".equals(schema.getFormat());
        }
    }

    public static boolean isURISchema(Schema schema) {
        return "string".equals(schema.getType()) && "uri".equals(schema.getFormat());
    }

    public static boolean isEmailSchema(Schema schema) {
        if (schema instanceof EmailSchema) {
            return true;
        } else {
            return "string".equals(schema.getType()) && "email".equals(schema.getFormat());
        }
    }

    public static boolean isModel(Schema schema) {
        if (schema == null) {
            LOGGER.error("Schema cannot be null in isModel check");
            return false;
        } else {
            return schema.getProperties() != null && !schema.getProperties().isEmpty() ? true : schema instanceof ComposedSchema;
        }
    }

    public static boolean isAnyTypeSchema(Schema schema) {
        if (schema == null) {
            LOGGER.error("Schema cannot be null in isAnyTypeSchema check");
            return false;
        } else if (isFreeFormObject(schema)) {
            return false;
        } else {
            return schema.getClass().equals(Schema.class) && schema.get$ref() == null && schema.getType() == null && (schema.getProperties() == null || schema.getProperties().isEmpty()) && schema.getAdditionalProperties() == null && schema.getNot() == null && schema.getEnum() == null;
        }
    }

    public static boolean isFreeFormObject(Schema schema) {
        if (schema == null) {
            LOGGER.error("Schema cannot be null in isFreeFormObject check");
            return false;
        } else {
            if (schema instanceof ComposedSchema) {
                ComposedSchema cs = (ComposedSchema)schema;
                List<Schema> interfaces = getInterfaces(cs);
                if (interfaces != null && !interfaces.isEmpty()) {
                    return false;
                }
            }

            if ("object".equals(schema.getType()) && (schema.getProperties() == null || schema.getProperties().isEmpty())) {
                Schema addlProps = getAdditionalProperties(schema);
                if (addlProps == null) {
                    return true;
                }

                if (addlProps instanceof ObjectSchema) {
                    ObjectSchema objSchema = (ObjectSchema)addlProps;
                    if (objSchema.getProperties() == null || objSchema.getProperties().isEmpty()) {
                        return true;
                    }
                } else if (addlProps instanceof Schema && addlProps.getType() == null && (addlProps.getProperties() == null || addlProps.getProperties().isEmpty())) {
                    return true;
                }
            }

            return false;
        }
    }

    public static Schema getReferencedSchema(OpenAPI openAPI, Schema schema) {
        if (schema != null && StringUtils.isNotEmpty(schema.get$ref())) {
            String name = getSimpleRef(schema.get$ref());
            Schema referencedSchema = getSchema(openAPI, name);
            if (referencedSchema != null) {
                return referencedSchema;
            }
        }

        return schema;
    }

    public static Schema getSchema(OpenAPI openAPI, String name) {
        return name == null ? null : (Schema)getSchemas(openAPI).get(name);
    }

    public static Map<String, Schema> getSchemas(OpenAPI openAPI) {
        return openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getSchemas() != null ? openAPI.getComponents().getSchemas() : Collections.emptyMap();
    }

    public static List<Schema> getAllSchemas(OpenAPI openAPI) {
        List<Schema> allSchemas = new ArrayList();
        List<String> refSchemas = new ArrayList();
        getSchemas(openAPI).forEach((key, schema) -> {
            visitSchema(openAPI, schema, (String)null, refSchemas, (s, mimetype) -> {
                allSchemas.add(s);
            });
        });
        return allSchemas;
    }

    public static RequestBody getReferencedRequestBody(OpenAPI openAPI, RequestBody requestBody) {
        if (requestBody != null && StringUtils.isNotEmpty(requestBody.get$ref())) {
            String name = getSimpleRef(requestBody.get$ref());
            RequestBody referencedRequestBody = getRequestBody(openAPI, name);
            if (referencedRequestBody != null) {
                return referencedRequestBody;
            }
        }

        return requestBody;
    }

    public static RequestBody getRequestBody(OpenAPI openAPI, String name) {
        if (name == null) {
            return null;
        } else {
            return openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getRequestBodies() != null ? (RequestBody)openAPI.getComponents().getRequestBodies().get(name) : null;
        }
    }

    public static ApiResponse getReferencedApiResponse(OpenAPI openAPI, ApiResponse apiResponse) {
        if (apiResponse != null && StringUtils.isNotEmpty(apiResponse.get$ref())) {
            String name = getSimpleRef(apiResponse.get$ref());
            ApiResponse referencedApiResponse = getApiResponse(openAPI, name);
            if (referencedApiResponse != null) {
                return referencedApiResponse;
            }
        }

        return apiResponse;
    }

    public static ApiResponse getApiResponse(OpenAPI openAPI, String name) {
        if (name == null) {
            return null;
        } else {
            return openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getResponses() != null ? (ApiResponse)openAPI.getComponents().getResponses().get(name) : null;
        }
    }

    public static Parameter getReferencedParameter(OpenAPI openAPI, Parameter parameter) {
        if (parameter != null && StringUtils.isNotEmpty(parameter.get$ref())) {
            String name = getSimpleRef(parameter.get$ref());
            Parameter referencedParameter = getParameter(openAPI, name);
            if (referencedParameter != null) {
                return referencedParameter;
            }
        }

        return parameter;
    }

    public static Parameter getParameter(OpenAPI openAPI, String name) {
        if (name == null) {
            return null;
        } else {
            return openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getParameters() != null ? (Parameter)openAPI.getComponents().getParameters().get(name) : null;
        }
    }

    public static Callback getReferencedCallback(OpenAPI openAPI, Callback callback) {
        if (callback != null && StringUtils.isNotEmpty(callback.get$ref())) {
            String name = getSimpleRef(callback.get$ref());
            Callback referencedCallback = getCallback(openAPI, name);
            if (referencedCallback != null) {
                return referencedCallback;
            }
        }

        return callback;
    }

    public static Callback getCallback(OpenAPI openAPI, String name) {
        if (name == null) {
            return null;
        } else {
            return openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getCallbacks() != null ? (Callback)openAPI.getComponents().getCallbacks().get(name) : null;
        }
    }

    public static Schema getSchemaFromRequestBody(RequestBody requestBody) {
        return getSchemaFromContent(requestBody.getContent());
    }

    public static Schema getSchemaFromResponse(ApiResponse response) {
        return getSchemaFromContent(response.getContent());
    }

    private static Schema getSchemaFromContent(Content content) {
        if (content != null && !content.isEmpty()) {
            Map.Entry<String, MediaType> entry = (Map.Entry)content.entrySet().iterator().next();
            if (content.size() > 1) {
                LOGGER.warn("Multiple schemas found in the OAS 'content' section, returning only the first one ({})", entry.getKey());
            }

            return ((MediaType)entry.getValue()).getSchema();
        } else {
            return null;
        }
    }

    public static Schema unaliasSchema(OpenAPI openAPI, Schema schema) {
        return unaliasSchema(openAPI, schema, Collections.emptyMap());
    }

    public static Schema unaliasSchema(OpenAPI openAPI, Schema schema, Map<String, String> importMappings) {
        Map<String, Schema> allSchemas = getSchemas(openAPI);
        if (allSchemas != null && !allSchemas.isEmpty()) {
            if (schema != null && StringUtils.isNotEmpty(schema.get$ref())) {
                String simpleRef = getSimpleRef(schema.get$ref());
                if (importMappings.containsKey(simpleRef)) {
                    LOGGER.info("Schema unaliasing of {} omitted because aliased class is to be mapped to {}", simpleRef, importMappings.get(simpleRef));
                    return schema;
                } else {
                    Schema ref = (Schema)allSchemas.get(simpleRef);
                    if (ref == null) {
                        //OnceLogger.once(LOGGER).warn("{} is not defined", schema.get$ref());
                        return schema;
                    } else if (ref.getEnum() != null && !ref.getEnum().isEmpty()) {
                        return schema;
                    } else if (isArraySchema(ref)) {
                        return isGenerateAliasAsModel() ? schema : unaliasSchema(openAPI, (Schema)allSchemas.get(getSimpleRef(schema.get$ref())), importMappings);
                    } else if (isComposedSchema(ref)) {
                        return schema;
                    } else if (isMapSchema(ref)) {
                        if (ref.getProperties() != null && !ref.getProperties().isEmpty()) {
                            return schema;
                        } else {
                            return isGenerateAliasAsModel() ? schema : unaliasSchema(openAPI, (Schema)allSchemas.get(getSimpleRef(schema.get$ref())), importMappings);
                        }
                    } else if (isObjectSchema(ref)) {
                        return ref.getProperties() != null && !ref.getProperties().isEmpty() ? schema : unaliasSchema(openAPI, (Schema)allSchemas.get(getSimpleRef(schema.get$ref())), importMappings);
                    } else {
                        return unaliasSchema(openAPI, (Schema)allSchemas.get(getSimpleRef(schema.get$ref())), importMappings);
                    }
                }
            } else {
                return schema;
            }
        } else {
            return schema;
        }
    }

    public static Schema getAdditionalProperties(Schema schema) {
        if (schema.getAdditionalProperties() instanceof Schema) {
            return (Schema)schema.getAdditionalProperties();
        } else {
            return schema.getAdditionalProperties() instanceof Boolean && (Boolean)schema.getAdditionalProperties() ? new ObjectSchema() : null;
        }
    }

    public static Header getReferencedHeader(OpenAPI openAPI, Header header) {
        if (header != null && StringUtils.isNotEmpty(header.get$ref())) {
            String name = getSimpleRef(header.get$ref());
            Header referencedheader = getHeader(openAPI, name);
            if (referencedheader != null) {
                return referencedheader;
            }
        }

        return header;
    }

    public static Header getHeader(OpenAPI openAPI, String name) {
        if (name == null) {
            return null;
        } else {
            return openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getHeaders() != null ? (Header)openAPI.getComponents().getHeaders().get(name) : null;
        }
    }

    public static Map<String, List<String>> getChildrenMap(OpenAPI openAPI) {
        Map<String, Schema> allSchemas = getSchemas(openAPI);
        Map<String, List<Map.Entry<String, Schema>>> groupedByParent = (Map)allSchemas.entrySet().stream().filter((entry) -> {
            return isComposedSchema((Schema)entry.getValue());
        }).filter((entry) -> {
            return getParentName((ComposedSchema)entry.getValue(), allSchemas) != null;
        }).collect(Collectors.groupingBy((entry) -> {
            return getParentName((ComposedSchema)entry.getValue(), allSchemas);
        }));
        return (Map)groupedByParent.entrySet().stream().collect(Collectors.toMap((entry) -> {
            return (String)entry.getKey();
        }, (entry) -> {
            return (List)((List)entry.getValue()).stream().map((e) -> {
                return (String)entry.getKey();
            }).collect(Collectors.toList());
        }));
    }

    public static List<Schema> getInterfaces(ComposedSchema composed) {
        if (composed.getAllOf() != null && !composed.getAllOf().isEmpty()) {
            return composed.getAllOf();
        } else if (composed.getAnyOf() != null && !composed.getAnyOf().isEmpty()) {
            return composed.getAnyOf();
        } else {
            return composed.getOneOf() != null && !composed.getOneOf().isEmpty() ? composed.getOneOf() : Collections.emptyList();
        }
    }

    public static String getParentName(ComposedSchema composedSchema, Map<String, Schema> allSchemas) {
        List<Schema> interfaces = getInterfaces(composedSchema);
        int nullSchemaChildrenCount = 0;
        boolean hasAmbiguousParents = false;
        List<String> refedWithoutDiscriminator = new ArrayList();
        if (interfaces != null && !interfaces.isEmpty()) {
            Iterator var6 = interfaces.iterator();

            while(var6.hasNext()) {
                Schema schema = (Schema)var6.next();
                if (StringUtils.isNotEmpty(schema.get$ref())) {
                    String parentName = getSimpleRef(schema.get$ref());
                    Schema s = (Schema)allSchemas.get(parentName);
                    if (s == null) {
                        LOGGER.error("Failed to obtain schema from {}", parentName);
                        return "UNKNOWN_PARENT_NAME";
                    }

                    if (hasOrInheritsDiscriminator(s, allSchemas)) {
                        return parentName;
                    }

                    hasAmbiguousParents = true;
                    refedWithoutDiscriminator.add(parentName);
                } else if (isNullType(schema)) {
                    ++nullSchemaChildrenCount;
                }
            }

            if (refedWithoutDiscriminator.size() == 1 && nullSchemaChildrenCount == 1) {
                hasAmbiguousParents = false;
            }
        }

        if (refedWithoutDiscriminator.size() == 1) {
            if (hasAmbiguousParents) {
                LOGGER.warn("[deprecated] inheritance without use of 'discriminator.propertyName' is deprecated and will be removed in a future release. Generating model for composed schema name: {}. Title: {}", composedSchema.getName(), composedSchema.getTitle());
            }

            return (String)refedWithoutDiscriminator.get(0);
        } else {
            return null;
        }
    }

    public static List<String> getAllParentsName(ComposedSchema composedSchema, Map<String, Schema> allSchemas, boolean includeAncestors) {
        List<Schema> interfaces = getInterfaces(composedSchema);
        List<String> names = new ArrayList();
        if (interfaces != null && !interfaces.isEmpty()) {
            Iterator var5 = interfaces.iterator();

            while(var5.hasNext()) {
                Schema schema = (Schema)var5.next();
                if (StringUtils.isNotEmpty(schema.get$ref())) {
                    String parentName = getSimpleRef(schema.get$ref());
                    Schema s = (Schema)allSchemas.get(parentName);
                    if (s == null) {
                        LOGGER.error("Failed to obtain schema from {}", parentName);
                        names.add("UNKNOWN_PARENT_NAME");
                    } else if (hasOrInheritsDiscriminator(s, allSchemas)) {
                        names.add(parentName);
                        if (includeAncestors && s instanceof ComposedSchema) {
                            names.addAll(getAllParentsName((ComposedSchema)s, allSchemas, true));
                        }
                    }
                }
            }
        }

        String parentName = getParentName(composedSchema, allSchemas);
        if (parentName != null && !names.contains(parentName)) {
            names.add(parentName);
        }

        return names;
    }

    private static boolean hasOrInheritsDiscriminator(Schema schema, Map<String, Schema> allSchemas) {
        if (schema.getDiscriminator() != null && StringUtils.isNotEmpty(schema.getDiscriminator().getPropertyName())) {
            return true;
        } else {
            if (StringUtils.isNotEmpty(schema.get$ref())) {
                String parentName = getSimpleRef(schema.get$ref());
                Schema s = (Schema)allSchemas.get(parentName);
                if (s != null) {
                    return hasOrInheritsDiscriminator(s, allSchemas);
                }

                LOGGER.error("Failed to obtain schema from {}", parentName);
            } else if (schema instanceof ComposedSchema) {
                ComposedSchema composed = (ComposedSchema)schema;
                List<Schema> interfaces = getInterfaces(composed);
                Iterator var4 = interfaces.iterator();

                while(var4.hasNext()) {
                    Schema i = (Schema)var4.next();
                    if (hasOrInheritsDiscriminator(i, allSchemas)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static boolean isNullable(Schema schema) {
        if (schema == null) {
            return false;
        } else if (Boolean.TRUE.equals(schema.getNullable())) {
            return true;
        } else if (schema.getExtensions() != null && schema.getExtensions().get("x-nullable") != null) {
            return Boolean.valueOf(schema.getExtensions().get("x-nullable").toString());
        } else {
            return schema instanceof ComposedSchema ? isNullableComposedSchema((ComposedSchema)schema) : false;
        }
    }

    public static boolean isNullableComposedSchema(ComposedSchema schema) {
        List<Schema> oneOf = schema.getOneOf();
        if (oneOf != null && oneOf.size() <= 2) {
            Iterator var2 = oneOf.iterator();

            while(var2.hasNext()) {
                Schema s = (Schema)var2.next();
                if (isNullType(s)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isNullType(Schema schema) {
        return "null".equals(schema.getType());
    }



    @FunctionalInterface
    private interface OpenAPISchemaVisitor {
        void visit(Schema var1, String var2);
    }
}
