package com.lyl.test.generate;

import com.lyl.test.utils.ModelUtils;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.XML;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author yunlong.liu
 * @date 2020-11-05 17:11:12
 */

public class XmlExampleGenerator {
    protected final Logger LOGGER = LoggerFactory.getLogger(XmlExampleGenerator.class);
    public static String NEWLINE = "\n";
    public static String TAG_START = "<";
    public static String CLOSE_TAG = ">";
    public static String TAG_END = "</";
    private static String EMPTY = "";
    protected Map<String, Schema> examples;

    public XmlExampleGenerator(Map<String, Schema> examples) {
        this.examples = examples;
        if (examples == null) {
            this.examples = new HashMap();
        }

    }

    public String toXml(Schema schema) {
        return this.toXml((String)null, schema, 0, Collections.emptySet());
    }

    protected String toXml(Schema schema, int indent, Collection<String> path) {
        if (schema == null) {
            return "";
        } else {
            if (StringUtils.isNotEmpty(schema.get$ref())) {
                Schema actualSchema = (Schema)this.examples.get(schema.get$ref());
                if (actualSchema != null) {
                    return this.modelImplToXml(actualSchema, indent, path);
                }
            }

            return this.modelImplToXml(schema, indent, path);
        }
    }

    protected String modelImplToXml(Schema schema, int indent, Collection<String> path) {
        String modelName = schema.getName();
        if (path.contains(modelName)) {
            return EMPTY;
        } else {
            Set<String> selfPath = new HashSet(path);
            selfPath.add(modelName);
            StringBuilder sb = new StringBuilder();
            Map<String, Schema> attributes = new LinkedHashMap();
            Map<String, Schema> elements = new LinkedHashMap();
            String name = modelName;
            XML xml = schema.getXml();
            if (xml != null && xml.getName() != null) {
                name = xml.getName();
            }

            Map<String, Schema> properties = schema.getProperties();
            Iterator var12;
            String pName;
            Schema property;
            if (properties != null && !properties.isEmpty()) {
                var12 = properties.keySet().iterator();

                label53:
                while(true) {
                    while(true) {
                        if (!var12.hasNext()) {
                            break label53;
                        }

                        pName = (String)var12.next();
                        property = (Schema)properties.get(pName);
                        if (property != null && property.getXml() != null && property.getXml().getAttribute() != null && property.getXml().getAttribute()) {
                            attributes.put(pName, property);
                        } else {
                            elements.put(pName, property);
                        }
                    }
                }
            }

            sb.append(this.indent(indent)).append(TAG_START);
            sb.append(name);
            var12 = attributes.keySet().iterator();

            while(var12.hasNext()) {
                pName = (String)var12.next();
                property = (Schema)attributes.get(pName);
                sb.append(" ").append(pName).append("=").append(this.quote(this.toXml((String)null, property, 0, selfPath)));
            }

            sb.append(CLOSE_TAG);
            sb.append(NEWLINE);
            var12 = elements.keySet().iterator();

            while(var12.hasNext()) {
                pName = (String)var12.next();
                property = (Schema)elements.get(pName);
                String asXml = this.toXml(pName, property, indent + 1, selfPath);
                if (!StringUtils.isEmpty(asXml)) {
                    sb.append(asXml);
                    sb.append(NEWLINE);
                }
            }

            sb.append(this.indent(indent)).append(TAG_END).append(name).append(CLOSE_TAG);
            return sb.toString();
        }
    }

    protected String quote(String string) {
        return "\"" + string + "\"";
    }

    protected String toXml(String name, Schema schema, int indent, Collection<String> path) {
        if (schema == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            if (ModelUtils.isArraySchema(schema)) {
                ArraySchema as = (ArraySchema)schema;
                Schema inner = as.getItems();
                boolean wrapped = false;
                if (schema.getXml() != null && schema.getXml().getWrapped() != null && schema.getXml().getWrapped()) {
                    wrapped = true;
                }

                if (wrapped) {
                    String prefix = EMPTY;
                    if (name != null) {
                        sb.append(this.indent(indent));
                        sb.append(this.openTag(name));
                        prefix = NEWLINE;
                    }

                    String asXml = this.toXml(name, inner, indent + 1, path);
                    if (StringUtils.isNotEmpty(asXml)) {
                        sb.append(prefix).append(asXml);
                    }

                    if (name != null) {
                        sb.append(NEWLINE);
                        sb.append(this.indent(indent));
                        sb.append(this.closeTag(name));
                    }
                } else {
                    sb.append(this.toXml(name, inner, indent, path));
                }
            } else if (StringUtils.isNotEmpty(schema.get$ref())) {
                Schema actualSchema = (Schema)this.examples.get(schema.get$ref());
                sb.append(this.toXml(actualSchema, indent, path));
            } else {
                if (name != null) {
                    sb.append(this.indent(indent));
                    sb.append(this.openTag(name));
                }

                sb.append(this.getExample(schema));
                if (name != null) {
                    sb.append(this.closeTag(name));
                }
            }

            return sb.toString();
        }
    }

    protected String getExample(Schema schema) {
        if (schema.getExample() != null) {
            return schema.getExample().toString();
        } else if (ModelUtils.isDateTimeSchema(schema)) {
            return "2000-01-23T04:56:07.000Z";
        } else if (ModelUtils.isDateSchema(schema)) {
            return "2000-01-23";
        } else if (ModelUtils.isBooleanSchema(schema)) {
            return "true";
        } else if (ModelUtils.isNumberSchema(schema)) {
            return ModelUtils.isFloatSchema(schema) ? "1.3579" : "3.149";
        } else if (ModelUtils.isPasswordSchema(schema)) {
            return "********";
        } else if (ModelUtils.isUUIDSchema(schema)) {
            return "046b6c7f-0b8a-43b9-b35d-6489e6daee91";
        } else if (ModelUtils.isURISchema(schema)) {
            return "https://openapi-generator.tech";
        } else if (ModelUtils.isStringSchema(schema)) {
            return "aeiou";
        } else if (ModelUtils.isIntegerSchema(schema)) {
            return ModelUtils.isLongSchema(schema) ? "123456789" : "123";
        } else {
            this.LOGGER.debug("default example value not implemented for {}. Default to UNDEFINED_EXAMPLE_VALUE", schema);
            return "UNDEFINED_EXAMPLE_VALUE";
        }
    }

    protected String openTag(String name) {
        return "<" + name + ">";
    }

    protected String closeTag(String name) {
        return "</" + name + ">";
    }

    protected String indent(int indent) {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < indent; ++i) {
            sb.append("  ");
        }

        return sb.toString();
    }
}