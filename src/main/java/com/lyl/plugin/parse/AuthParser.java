package com.lyl.plugin.parse;

import io.swagger.v3.parser.core.models.AuthorizationValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * @author yunlong.liu
 * @date 2020-11-04 15:52:31
 */

public class AuthParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthParser.class);

    public static List<AuthorizationValue> parse(String urlEncodedAuthStr) {
        List<AuthorizationValue> auths = new ArrayList<AuthorizationValue>();
        if (isNotEmpty(urlEncodedAuthStr)) {
            String[] parts = urlEncodedAuthStr.split(",");
            for (String part : parts) {
                String[] kvPair = part.split(":");
                if (kvPair.length == 2) {
                    try {
                        auths.add(new AuthorizationValue(URLDecoder.decode(kvPair[0], "UTF-8"), URLDecoder.decode(kvPair[1], "UTF-8"), "header"));
                    } catch (UnsupportedEncodingException e) {
                        LOGGER.warn(e.getMessage());
                    }
                }
            }
        }
        return auths;
    }

    public static String reconstruct(List<AuthorizationValue> authorizationValueList) {
        if (authorizationValueList != null) {
            StringBuilder b = new StringBuilder();
            for (AuthorizationValue v : authorizationValueList) {
                try {
                    if (b.toString().length() > 0) {
                        b.append(",");
                    }
                    b.append(URLEncoder.encode(v.getKeyName(), "UTF-8"))
                            .append(":")
                            .append(URLEncoder.encode(v.getValue(), "UTF-8"));
                } catch (Exception e) {
                    // continue
                    LOGGER.error(e.getMessage(), e);
                }
            }
            return b.toString();
        } else {
            return null;
        }
    }
}
