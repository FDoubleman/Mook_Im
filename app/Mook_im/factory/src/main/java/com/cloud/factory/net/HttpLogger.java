package com.cloud.factory.net;


import com.cloud.common.utils.logger.Logger;
import com.cloud.factory.net.client.NetworkHeader;
import com.cloud.factory.net.client.NetworkRequest;


import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 拦截http请求信息
 */
public class HttpLogger implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String logVersion = "100";

    @Override
    public Response intercept(Chain chain) throws IOException {
        String logMsg = "";
        NetworkRequest requestLog = new NetworkRequest();
        ArrayList<NetworkHeader> headersLog = new ArrayList<>();

        String uuid = UUID.randomUUID().toString();
        requestLog.setUuid(uuid);
        requestLog.setVersion(logVersion);

//        请求信息
        Request request = chain.request();
//        请求体
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
//        连接信息
        Connection connection = chain.connection();
//        请求协议
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
//        请求方式，请求地址，请求协议
        logMsg += "Request: \n  " + request.method() + ' ' + request.url() + "\n";
        requestLog.setRequestUrl(request.url().toString());
        requestLog.setRequestMethod(request.method());
        logMsg += "  protocol: " + protocol + "\n";
        if (hasRequestBody) {
            logMsg += "Request Headers:\n";
//            contentType
            if (requestBody.contentType() != null) {
                logMsg += "  Content-Type: " + requestBody.contentType() + "\n";
            }
            if (requestBody.contentLength() != -1) {
                logMsg += "  Content-Length: " + requestBody.contentLength() + "\n";
            }
        }

        Headers headers = request.headers();
        for (int i = 0; i < headers.size(); i++) {
            String name = headers.name(i);
//            打印其他头部信息
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                logMsg += "  " + name + ": " + headers.value(i) + "\n";

                NetworkHeader entity = new NetworkHeader();
                entity.setUuid(uuid);
                entity.setHeaderType("request");
                entity.setHeaderKey(name);
                entity.setHeaderValue(headers.value(i));
                headersLog.add(entity);
            }
        }

        if (!hasRequestBody) {
            logMsg += "Request Body: (0-byte body) \n\n";
            requestLog.setRequestBody("0-byte body");
            requestLog.setRequestContentLength(String.valueOf(0));
        } else if (bodyEncoded(request.headers())) {
            logMsg += "Request Body: (encoded body omitted)\n\n";
            requestLog.setRequestBody("encoded body omitted");
        } else {
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
//                Logger.getLogger().d("—type："+contentType.type()+"，subType："+contentType.subtype()+"，string："+contentType.toString());
                if (contentType.subtype().equals("json") || contentType.subtype().equals("x-www-form-urlencoded")) {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);

                    Charset charset = contentType.charset(UTF8);
                    if (isPlaintext(buffer)) {
                        String content = buffer.readString(charset);
                        logMsg += "Request Body: (" + requestBody.contentLength() + "-byte body) \n";
                        logMsg += "  " + content + "\n\n";
                        requestLog.setRequestBody(content);
                        requestLog.setRequestContentLength(String.valueOf(requestBody.contentLength()));
                    } else {
                        logMsg += "Request Body: (binary "
                                + requestBody.contentLength() + "-byte body omitted)" + "\n\n";
                        requestLog.setRequestBody("binary " + requestBody.contentLength() + "-byte body omitted");
                    }
                } else {
                    logMsg += "Request Body: (encoded body omitted)\n\n";
                    requestLog.setRequestBody("encoded body omitted");
                }
            }else {
                logMsg += "Request Body: (contentType is null)\n\n";
                requestLog.setRequestBody("contentType is null");
            }
        }

        long startNs = System.nanoTime();

        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            logMsg += "Response: \n  HTTP FAILED: " + e + "\n";
            Logger.getLogger().e(logMsg);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        requestLog.setRequestTime(String.valueOf(response.sentRequestAtMillis()));
        requestLog.setResponseTime(String.valueOf(response.receivedResponseAtMillis()));
        requestLog.setPeriod(String.valueOf(response.receivedResponseAtMillis() - response.sentRequestAtMillis()));

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        logMsg += "Response: " + "(" + tookMs + "ms" + ")\n  " + response.code() + ' ' + response.message() + "\n";
        requestLog.setResponseStatus(String.valueOf(response.code()));
        requestLog.setResponseMessage(response.message());
        logMsg += "Response Headers:\n";
        Headers respHeaders = response.headers();
        for (int i = 0, count = respHeaders.size(); i < count; i++) {
            logMsg += "  " + respHeaders.name(i) + ": " + respHeaders.value(i) + "\n";

            NetworkHeader entity = new NetworkHeader();
            entity.setUuid(uuid);
            entity.setHeaderType("response");
            entity.setHeaderKey(respHeaders.name(i));
            entity.setHeaderValue(respHeaders.value(i));
            headersLog.add(entity);
        }

        if (!HttpHeaders.hasBody(response)) {
            logMsg += "Response Body: (0-byte body)";
            requestLog.setResponseBody("0-byte body");
        } else if (bodyEncoded(response.headers())) {
            logMsg += "Response Body: (encoded body omitted)";
            requestLog.setResponseBody("encoded body omitted");
        } else {
            if (contentLength == -1) {
                logMsg += "Response Body: (chunked)\n" ;
                requestLog.setResponseBody("chunked");
            } else {
                logMsg += "Response Body: (" + contentLength + "-byte body)\n" ;
                requestLog.setResponseContentLength(String.valueOf(contentLength));
            }

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                if (contentType.subtype().equals("json") || contentType.subtype().equals("plain")) {
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();

                    if (!isPlaintext(buffer)) {
                        logMsg += "" + "\n";
                        logMsg += "  (binary " + buffer.size() + "-byte body omitted)";
                        requestLog.setResponseBody("binary " + buffer.size() + "-byte body omitted");
                        Logger.getLogger().d(logMsg);
                        return response;
                    }

                    if (contentLength != 0) {
                        String content = buffer.clone().readString(charset);
                        logMsg += content;
                    }
                } else {
                    logMsg += "";
                }
            }
        }
        Logger.getLogger().d(logMsg);
//        NetworkRequestLogger.getInstance().addRequestLog(requestLog);
//        NetworkRequestLogger.getInstance().addHeaderListLog(headersLog);
//        NetworkRequestLogger.getInstance().addAppInfoLog(uuid);
        return response;
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    private boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                if (Character.isISOControl(prefix.readUtf8CodePoint())) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
