package com.ycb.socket.utils;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;

/**
 * HTTP 请求的封装对象。
 */
public class HttpClientSend implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(HttpClientSend.class);
    private static final long serialVersionUID = -176092625883595547L;

    private static final int OK = 200; // OK
    private static final int NOT_MODIFIED = 304; // Not Modified
    private static final int BAD_REQUEST = 400; // Bad Request
    private static final int NOT_AUTHORIZED = 401; // Not Authorized
    private static final int FORBIDDEN = 403; // Forbidden
    private static final int NOT_FOUND = 404; // Not Found
    private static final int NOT_ACCEPTABLE = 406; // Not Acceptable
    private static final int INTERNAL_SERVER_ERROR = 500; // Internal Server Error
    private static final int BAD_GATEWAY = 502; // Bad Gateway
    private static final int SERVICE_UNAVAILABLE = 503; // Service Unavailable
    private final static boolean DEBUG = false;

    // 全局唯一实例
    private static HttpClientSend httpClientSend = new HttpClientSend();

    public static HttpClientSend getHttpClientSend() {
        return httpClientSend;
    }

    /**
     * log调试
     */
    private static void log(String message) {
        if (DEBUG) {
            logger.info(message);
        }
    }

    public Response httpRequest(HttpMethod method) {
        Response response = new Response();
        EasySSLProtocolSocketFactory easySSL = new EasySSLProtocolSocketFactory();
        Protocol easyhttps = new Protocol("https", (ProtocolSocketFactory) easySSL, 443);
        Protocol.registerProtocol("https", easyhttps);

        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpClient client = new HttpClient(connectionManager);
        client.getParams().setBooleanParameter("http.protocol.expect-continue", false);

        int responseCode = -1;
        try {
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(3, false));
            method.addRequestHeader("Connection", "close");

            client.executeMethod(method);

            Header[] resHeader = method.getResponseHeaders();
            responseCode = method.getStatusCode();

            log("Response:");
            log("https StatusCode:" + String.valueOf(responseCode));
            for (Header header : resHeader) {
                log(header.getName() + ":" + header.getValue());
            }

            response.setResponseAsString(method.getResponseBodyAsString());

            log(response.toString() + "\n");
            if (responseCode != OK) {
                logger.error(response.toString());
                logger.error(getCause(responseCode));
            }
        } catch (Exception ioe) {
            logger.error(ioe.getMessage());
        } finally {
            method.releaseConnection();
        }

        return response;
    }

    /**
     * httpclint get传输
     *
     * @param url
     * @param params
     * @return
     */
    public Response get(String url, PostParameter[] params) {
        log("Request:");
        log("GET:" + url);
        if (null != params && params.length > 0) {
            String encodedParams = HttpClientSend.encodeParameters(params);
            if (-1 == url.indexOf("?")) {
                url += "?" + encodedParams;
            } else {
                url += "&" + encodedParams;
            }
        }
        log("all_GET:" + url);
        GetMethod getmethod = new GetMethod(url);
        return httpRequest(getmethod);
    }

    /**
     * httpclint POST传输
     *
     * @param url
     * @param params
     * @return
     */
    public Response post(String url, PostParameter[] params) {
        log("Request:");
        log("POST" + url);
        PostMethod postMethod = new PostMethod(url);
        for (int i = 0; i < params.length; i++) {
            postMethod.addParameter(params[i].getName(), params[i].getValue());
        }
        HttpMethodParams param = postMethod.getParams();
        param.setContentCharset("UTF-8");
        return httpRequest(postMethod);
    }

    /**
     * httpclint delete传输
     *
     * @param url
     * @param params
     * @return
     */
    public Response delete(String url, PostParameter[] params) {
        if (0 != params.length) {
            String encodedParams = HttpClientSend.encodeParameters(params);
            if (-1 == url.indexOf("?")) {
                url += "?" + encodedParams;
            } else {
                url += "&" + encodedParams;
            }
        }
        DeleteMethod deleteMethod = new DeleteMethod(url);
        return httpRequest(deleteMethod);

    }

    private static class ByteArrayPart extends PartBase {
        private byte[] mData;
        private String mName;

        public ByteArrayPart(byte[] data, String name, String type)
                throws IOException {
            super(name, type, "UTF-8", "binary");
            mName = name;
            mData = data;
        }

        protected void sendData(OutputStream out) throws IOException {
            out.write(mData);
        }

        protected long lengthOfData() throws IOException {
            return mData.length;
        }

        protected void sendDispositionHeader(OutputStream out)
                throws IOException {
            super.sendDispositionHeader(out);
            StringBuilder buf = new StringBuilder();
            buf.append("; filename=\"").append(mName).append("\"");
            out.write(buf.toString().getBytes());
        }
    }

    /*
     * 对parameters进行encode处理
     */
    public static String encodeParameters(PostParameter[] postParams) {
        StringBuffer buf = new StringBuffer();

        for (int j = 0; j < postParams.length; j++) {
            if (j != 0) {
                buf.append("&");
            }

            try {
                buf.append(URLEncoder.encode(postParams[j].getName(), "UTF-8")).append("=")
                        .append(URLEncoder.encode(postParams[j].getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException neverHappen) {
                logger.error(neverHappen.getMessage());
            }
        }

        return buf.toString();
    }

    private static String getCause(int statusCode) {
        String cause = null;

        switch (statusCode) {
            case NOT_MODIFIED:
                break;
            case BAD_REQUEST:
                cause = "The request was invalid.  An accompanying error message will explain why. This is the status code will be returned during rate limiting.";
                break;
            case NOT_AUTHORIZED:
                cause = "Authentication credentials were missing or incorrect.";
                break;
            case FORBIDDEN:
                cause = "The request is understood, but it has been refused.  An accompanying error message will explain why.";
                break;
            case NOT_FOUND:
                cause = "The URI requested is invalid or the resource requested, such as a user, does not exists.";
                break;
            case NOT_ACCEPTABLE:
                cause = "Returned by the Search API when an invalid format is specified in the request.";
                break;
            case INTERNAL_SERVER_ERROR:
                cause = "Something is broken.  Please post to the group so the Weibo team can investigate.";
                break;
            case BAD_GATEWAY:
                cause = "Weibo is down or being upgraded.";
                break;
            case SERVICE_UNAVAILABLE:
                cause = "Service Unavailable: The Weibo servers are up, but overloaded with requests. Try again later. The search and trend methods use this to indicate when you are being rate limited.";
                break;
            default:
                cause = "";
        }
        return statusCode + ":" + cause;
    }
}
