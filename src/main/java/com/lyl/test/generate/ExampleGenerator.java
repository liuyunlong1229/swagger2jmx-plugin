package com.lyl.test.generate;

import com.lyl.test.utils.ModelUtils;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author yunlong.liu
 * @date 2020-11-05 17:10:05
 */

public class ExampleGenerator {


    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleGenerator.class);
    private static final String MIME_TYPE_JSON = "application/json";
    private static final String MIME_TYPE_XML = "application/xml";
    private static final String EXAMPLE = "example";
    private static final String CONTENT_TYPE = "contentType";
    private static final String OUTPUT = "output";
    private static final String NONE = "none";
    private static final String URL = "url";
    private static final String URI = "uri";
    private static final String STATUS_CODE = "statusCode";
    protected Map<String, Schema> examples;
    private OpenAPI openAPI;
    private Random random;

    public ExampleGenerator(Map<String, Schema> examples, OpenAPI openAPI) {
        this.examples = examples;
        this.openAPI = openAPI;
        this.random = new Random((long)"ExampleGenerator".hashCode());
    }

    public List<Map<String, String>> generateFromResponseSchema(String statusCode, Schema responseSchema, Set<String> producesInfo) {
        List<Map<String, String>> examples = this.generateFromResponseSchema(responseSchema, producesInfo);
        if (examples == null) {
            return null;
        } else {
            Iterator var5 = examples.iterator();

            while(var5.hasNext()) {
                Map<String, String> example = (Map)var5.next();
                example.put("statusCode", statusCode);
            }

            return examples;
        }
    }

    private List<Map<String, String>> generateFromResponseSchema(Schema responseSchema, Set<String> producesInfo) {
        if (responseSchema.getExample() == null && StringUtils.isEmpty(responseSchema.get$ref()) && !ModelUtils.isArraySchema(responseSchema)) {
            return null;
        } else if (responseSchema.getExample() != null && !(responseSchema.getExample() instanceof Map)) {
            return this.generate(responseSchema.getExample(), new ArrayList(producesInfo));
        } else if (ModelUtils.isArraySchema(responseSchema)) {
            ArraySchema as = (ArraySchema)responseSchema;
            if (as.getItems() != null && StringUtils.isEmpty(as.getItems().get$ref())) {
                return this.generate((Map)responseSchema.getExample(), new ArrayList(producesInfo), (Schema)as.getItems());
            } else {
                return as.getItems() != null && !StringUtils.isEmpty(as.getItems().get$ref()) ? this.generate((Map)responseSchema.getExample(), new ArrayList(producesInfo), (String)ModelUtils.getSimpleRef(as.getItems().get$ref())) : null;
            }
        } else {
            return StringUtils.isEmpty(responseSchema.get$ref()) ? this.generate((Map)responseSchema.getExample(), new ArrayList(producesInfo), (Schema)responseSchema) : this.generate((Map)responseSchema.getExample(), new ArrayList(producesInfo), (String)ModelUtils.getSimpleRef(responseSchema.get$ref()));
        }
    }

    public List<Map<String, String>> generate(Map<String, Object> examples, List<String> mediaTypes, Schema property) {
        LOGGER.debug("debugging generate in ExampleGenerator");
        List<Map<String, String>> output = new ArrayList();
        Set<String> processedModels = new HashSet();
        Iterator var6;
        HashMap kv;
        if (examples == null) {
            if (mediaTypes == null) {
                mediaTypes = Collections.singletonList("application/json");
            }

            var6 = mediaTypes.iterator();

            label51:
            while(true) {
                while(true) {
                    if (!var6.hasNext()) {
                        break label51;
                    }

                    String mediaType = (String)var6.next();
                    kv = new HashMap();
                    kv.put("contentType", mediaType);
                    String example;
                    if (property != null && (mediaType.startsWith("application/json") || mediaType.contains("*/*"))) {
                        example = Json.pretty(this.resolvePropertyToExample("", mediaType, property, processedModels));
                        if (example != null) {
                            kv.put("example", example);
                            output.add(kv);
                        }
                    } else if (property != null && mediaType.startsWith("application/xml")) {
                        example = (new XmlExampleGenerator(this.examples)).toXml(property);
                        if (example != null) {
                            kv.put("example", example);
                            output.add(kv);
                        }
                    }
                }
            }
        } else {
            var6 = examples.entrySet().iterator();

            while(var6.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry)var6.next();
                kv = new HashMap();
                kv.put("contentType", entry.getKey());
                kv.put("example", Json.pretty(entry.getValue()));
                output.add(kv);
            }
        }

        if (output.size() == 0) {
            Map<String, String> kv1 = new HashMap();
            kv1.put("output", "none");
            output.add(kv1);
        }

        return output;
    }

    public List<Map<String, String>> generate(Map<String, Object> examples, List<String> mediaTypes, String modelName) {
        List<Map<String, String>> output = new ArrayList();
        Set<String> processedModels = new HashSet();
        Iterator var6;
        HashMap kv;
        if (examples == null) {
            if (mediaTypes == null) {
                mediaTypes = Collections.singletonList("application/json");
            }

            var6 = mediaTypes.iterator();

            label53:
            while(true) {
                while(true) {
                    if (!var6.hasNext()) {
                        break label53;
                    }

                    String mediaType = (String)var6.next();
                    kv = new HashMap();
                    kv.put("contentType", mediaType);
                    Schema schema;
                    String example;
                    if (modelName != null && (mediaType.startsWith("application/json") || mediaType.contains("*/*"))) {
                        schema = (Schema)this.examples.get(modelName);
                        if (schema != null) {
                            example = Json.pretty(this.resolveModelToExample(modelName, mediaType, schema, processedModels));
                            if (example != null) {
                                kv.put("example", example);
                                output.add(kv);
                            }
                        }
                    } else if (modelName != null && mediaType.startsWith("application/xml")) {
                        schema = (Schema)this.examples.get(modelName);
                        example = (new XmlExampleGenerator(this.examples)).toXml(schema, 0, Collections.emptySet());
                        if (example != null) {
                            kv.put("example", example);
                            output.add(kv);
                        }
                    }
                }
            }
        } else {
            var6 = examples.entrySet().iterator();

            while(var6.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry)var6.next();
                kv = new HashMap();
                kv.put("contentType", entry.getKey());
                kv.put("example", Json.pretty(entry.getValue()));
                output.add(kv);
            }
        }

        if (output.size() == 0) {
            Map<String, String> kv1 = new HashMap();
            kv1.put("output", "none");
            output.add(kv1);
        }

        return output;
    }

    private List<Map<String, String>> generate(Object example, List<String> mediaTypes) {
        List<Map<String, String>> output = new ArrayList();
        if (this.examples != null) {
            if (mediaTypes == null) {
                mediaTypes = Collections.singletonList("application/json");
            }

            Iterator var4 = mediaTypes.iterator();

            label32:
            while(true) {
                while(true) {
                    if (!var4.hasNext()) {
                        break label32;
                    }

                    String mediaType = (String)var4.next();
                    Map<String, String> kv = new HashMap();
                    kv.put("contentType", mediaType);
                    if (!mediaType.startsWith("application/json") && !mediaType.contains("*/*")) {
                        if (mediaType.startsWith("application/xml")) {
                            LOGGER.warn("XML example value of (array/primitive) is not handled at the moment: " + example);
                        }
                    } else {
                        kv.put("example", Json.pretty(example));
                        output.add(kv);
                    }
                }
            }
        }

        if (output.size() == 0) {
            Map<String, String> kv = new HashMap();
            kv.put("output", "none");
            output.add(kv);
        }

        return output;
    }

    private Object resolvePropertyToExample(String propertyName, String mediaType, Schema property, Set<String> processedModels) {
        LOGGER.debug("Resolving example for property {}...", property);
        if (property.getExample() != null) {
            LOGGER.debug("Example set in openapi spec, returning example: '{}'", property.getExample().toString());
            return property.getExample();
        } else if (ModelUtils.isBooleanSchema(property)) {
            Object defaultValue = property.getDefault();
            return defaultValue != null ? defaultValue : Boolean.TRUE;
        } else {
            if (ModelUtils.isArraySchema(property)) {
                Schema innerType = ((ArraySchema)property).getItems();
                if (innerType != null) {
                    int arrayLength = null == ((ArraySchema)property).getMaxItems() ? 2 : ((ArraySchema)property).getMaxItems();
                    arrayLength = Math.min(arrayLength, 5);
                    Object[] objectProperties = new Object[arrayLength];
                    Object objProperty = this.resolvePropertyToExample(propertyName, mediaType, innerType, processedModels);

                    for(int i = 0; i < arrayLength; ++i) {
                        objectProperties[i] = objProperty;
                    }

                    return objectProperties;
                }
            } else {
                if (ModelUtils.isDateSchema(property)) {
                    return "2000-01-23";
                }

                if (ModelUtils.isDateTimeSchema(property)) {
                    return "2000-01-23T04:56:07.000+00:00";
                }

                Double min;
                Double max;
                if (ModelUtils.isNumberSchema(property)) {
                    min = this.getPropertyValue(property.getMinimum());
                    max = this.getPropertyValue(property.getMaximum());
                    if (ModelUtils.isFloatSchema(property)) {
                        return (float)this.randomNumber(min, max);
                    }

                    if (ModelUtils.isDoubleSchema(property)) {
                        return BigDecimal.valueOf(this.randomNumber(min, max));
                    }

                    return this.randomNumber(min, max);
                }

                if (ModelUtils.isFileSchema(property)) {
                    return "";
                }

                if (ModelUtils.isIntegerSchema(property)) {
                    min = this.getPropertyValue(property.getMinimum());
                    max = this.getPropertyValue(property.getMaximum());
                    if (ModelUtils.isLongSchema(property)) {
                        return (long)this.randomNumber(min, max);
                    }

                    return (int)this.randomNumber(min, max);
                }

                if (ModelUtils.isMapSchema(property)) {
                    Map<String, Object> mp = new HashMap();
                    if (property.getName() != null) {
                        mp.put(property.getName(), this.resolvePropertyToExample(propertyName, mediaType, ModelUtils.getAdditionalProperties(property), processedModels));
                    } else {
                        mp.put("key", this.resolvePropertyToExample(propertyName, mediaType, ModelUtils.getAdditionalProperties(property), processedModels));
                    }

                    return mp;
                }

                if (ModelUtils.isUUIDSchema(property)) {
                    return "046b6c7f-0b8a-43b9-b35d-6489e6daee91";
                }

                if (ModelUtils.isURISchema(property)) {
                    return "https://openapi-generator.tech";
                }

                String simpleName;
                if (ModelUtils.isStringSchema(property)) {
                    LOGGER.debug("String property");
                    simpleName = (String)property.getDefault();
                    if (simpleName != null && !simpleName.isEmpty()) {
                        LOGGER.debug("Default value found: '{}'", simpleName);
                        return simpleName;
                    }

                    List<String> enumValues = property.getEnum();
                    if (enumValues != null && !enumValues.isEmpty()) {
                        LOGGER.debug("Enum value found: '{}'", enumValues.get(0));
                        return enumValues.get(0);
                    }

                    String format = property.getFormat();
                    if (format == null || !"uri".equals(format) && !"url".equals(format)) {
                        LOGGER.debug("No values found, using property name " + propertyName + " as example");
                        return propertyName;
                    }

                    LOGGER.debug("URI or URL format, without default or enum, generating random one.");
                    return "http://example.com/aeiou";
                }

                if (!StringUtils.isEmpty(property.get$ref())) {
                    simpleName = ModelUtils.getSimpleRef(property.get$ref());
                    Schema schema = ModelUtils.getSchema(this.openAPI, simpleName);
                    if (schema == null) {
                        return "{}";
                    }

                    return this.resolveModelToExample(simpleName, mediaType, schema, processedModels);
                }

                if (ModelUtils.isObjectSchema(property)) {
                    return "{}";
                }
            }

            return "";
        }
    }

    private Double getPropertyValue(BigDecimal propertyValue) {
        return propertyValue == null ? null : propertyValue.doubleValue();
    }

    private double randomNumber(Double min, Double max) {
        if (min != null && max != null) {
            double range = max - min;
            return this.random.nextDouble() * range + min;
        } else if (min != null) {
            return this.random.nextDouble() + min;
        } else {
            return max != null ? this.random.nextDouble() * max : this.random.nextDouble() * 10.0D;
        }
    }

    private Object resolveModelToExample(String name, String mediaType, Schema schema, Set<String> processedModels) {
        if (processedModels.contains(name)) {
            return schema.getExample();
        } else {
            processedModels.add(name);
            Map<String, Object> values = new HashMap();
            LOGGER.debug("Resolving model '{}' to example", name);
            if (schema.getExample() != null) {
                LOGGER.debug("Using example from spec: {}", schema.getExample());
                return schema.getExample();
            } else if (schema.getProperties() == null) {
                return null;
            } else {
                LOGGER.debug("Creating example from model values");
                Iterator var6 = schema.getProperties().keySet().iterator();

                while(var6.hasNext()) {
                    Object propertyName = var6.next();
                    Schema property = (Schema)schema.getProperties().get(propertyName.toString());
                    values.put(propertyName.toString(), this.resolvePropertyToExample(propertyName.toString(), mediaType, property, processedModels));
                }

                schema.setExample(values);
                return schema.getExample();
            }
        }
    }
}
