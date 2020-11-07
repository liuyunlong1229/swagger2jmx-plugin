package com.lyl.plugin.parse;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author yunlong.liu
 * @date 2020-11-04 15:39:24
 */

public class SwaggerParser {


    private static final Logger log= LoggerFactory.getLogger(SwaggerParser.class);

    public SwaggerParseResult readWithInfo(String location,String auth) {
        final List<AuthorizationValue> authorizationValues = AuthParser.parse(auth);
        ParseOptions options = new ParseOptions();
        options.setResolve(true);
        SwaggerParseResult result = new OpenAPIParser().readLocation(location, authorizationValues, options);
        return result;
    }
}
