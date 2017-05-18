package com.gallery.camera.framework.utils;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

/**
 * 上传文件工具类
 * Created by kachem on 2017/3/16.
 */

public class UploadFile {
    private static final String BOUNDARY = UUID.randomUUID().toString();// 边界标识、随机生成、数据分割线
    private static final String PREFIX = "--"; // 前缀
    private static final String LINE_END = "\r\n"; // 一行的结束标识
    private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型

    private static int readTimeOut = 10 * 1000; // 读取超时
    private static int connectTimeout = 10 * 1000; // 超时时间
    /***
     * 请求使用多长时间
     */
    private static int requestTime = 0;

    private static final String CHARSET = "utf-8"; // 设置编码

    /**
     * 上传多个文件
     *
     * @param ip      url
     * @param params  参数
     * @param files   文件
     * @param fileKey key
     */
    public static int toUploadFile(String ip, Map<String, String> params,
                                   Map<String, File> files, String fileKey) {
        requestTime = 0;
        long startRequestTime = System.currentTimeMillis();
        long responseTime = 0;

        try {

            URL url = new URL(ip);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(readTimeOut);
            conn.setConnectTimeout(connectTimeout);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            StringBuilder sb = new StringBuilder();

            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    sb.append("Content-Disposition: form-data; name=\"")
                            .append(entry.getKey()).append("\"")
                            .append(LINE_END).append(LINE_END);
                    sb.append(entry.getValue()).append(LINE_END);
                }
            }

            dos.write(sb.toString().getBytes());

            if (files != null) {
                for (Map.Entry<String, File> entry : files.entrySet()) {
                    sb = new StringBuilder();
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    sb.append("Content-Disposition:form-data; name=\""
                            + fileKey + "\"; filename=\""
                            + entry.getValue().getName() + "\"" + LINE_END);
                    sb.append("Content-Type:image/jpeg" + LINE_END);
                    sb.append(LINE_END);
                    dos.write(sb.toString().getBytes());

                    InputStream is = new FileInputStream(entry.getValue());
                    byte[] bytes = new byte[1024];
                    int len;
                    while ((len = is.read(bytes)) != -1) {
                        dos.write(bytes, 0, len);
                    }

                    is.close();
                    dos.write(LINE_END.getBytes());
                }
            }

            byte[] after = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(after);
            dos.flush();
            dos.close();
            int responseCode = conn.getResponseCode();
            responseTime = System.currentTimeMillis();
            requestTime = (int) ((responseTime - startRequestTime) / 1000);
            if (responseCode == 200) {
                InputStream input = conn.getInputStream();
                StringBuilder builder = new StringBuilder();
                int ss;
                while ((ss = input.read()) != -1) {
                    builder.append((char) ss);
                }
                JSONObject myJsonObject = new JSONObject(builder.toString());
                //获取对应的值
                return myJsonObject.getInt("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 2;
    }

    /**
     * 上传单个文件
     */
    public static int toUploadFile(String ip, String params, File file, String fileKey) {
        requestTime = 0;
        long startRequestTime = System.currentTimeMillis();
        long responseTime;

        try {

            URL url = new URL(ip);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(readTimeOut);
            conn.setConnectTimeout(connectTimeout);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            StringBuilder sb = new StringBuilder();

            sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
            sb.append("Content-Disposition: form-data; name=\"")
                    .append(params).append("\"")
                    .append(LINE_END).append(LINE_END);
            sb.append(params).append(LINE_END);

            // 写入参数信息
            dos.write(sb.toString().getBytes());

            if (file != null && file.exists()) {
                sb = new StringBuilder();
                sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                sb.append("Content-Disposition:form-data; name=\""
                        + fileKey + "\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type:image/jpeg" + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());

                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }

                is.close();

                dos.write(LINE_END.getBytes());
            }

            // 请求结束符
            byte[] after = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(after);
            dos.flush();
            dos.close();
            int responseCode = conn.getResponseCode();
            responseTime = System.currentTimeMillis();
            requestTime = (int) ((responseTime - startRequestTime) / 1000);
            if (responseCode == 200) {
                InputStream input = conn.getInputStream();
                StringBuilder builder = new StringBuilder();
                int ss;
                while ((ss = input.read()) != -1) {
                    builder.append((char) ss);
                }
                JSONObject myJsonObject = new JSONObject(builder.toString());
                //获取对应的值
                return myJsonObject.getInt("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 2;
    }
}
