package org.yascode.bank_account.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class CustomBeanDecoder implements Decoder {
    private final ObjectMapper objectMapper;

    public CustomBeanDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException {
        String responseBody = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);

        InputStream inputStream = new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8));

        return objectMapper.readValue(inputStream, objectMapper.constructType(type));
    }
}
