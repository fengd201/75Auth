package com.fengd201.auth.service;

import com.fengd201.auth.common.constant.HttpMethod;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class WebRequestUtil {
  public static String sendRequest(String targetUrl, HttpMethod httpMethod,
      Map<String, String> params, String userAgent) {
    HttpURLConnection connection = null;
    try {
      URL url = new URL(targetUrl);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod(httpMethod.toString());
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      if (null != userAgent && !userAgent.isEmpty())
        connection.setRequestProperty("User-Agent", userAgent);
      StringBuilder sb = new StringBuilder();
      if (null != params && !params.isEmpty()) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
          if (sb.length() != 0)
            sb.append("&");
          sb.append(entry.getKey()).append("=")
              .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
      }
      String paramsStr = sb.toString();
      connection.setRequestProperty("Content-Length",
          "" + Integer.toString(paramsStr.getBytes().length));
      connection.setRequestProperty("Content-Language", "en-US");

      connection.setUseCaches(false);
      connection.setDoInput(true);
      connection.setDoOutput(true);

      // Send request
      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
      wr.writeBytes(paramsStr);
      wr.flush();
      wr.close();

      // Get Response
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      String line;
      StringBuffer response = new StringBuffer();
      while ((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r');
      }
      rd.close();
      return response.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      if (null != connection)
        connection.disconnect();
    }
  }

  public static String sendRestRequest(String targetUrl, HttpMethod httpMethod, String userAgent) {
    HttpURLConnection connection = null;
    try {
      URL url = new URL(targetUrl);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod(httpMethod.toString());
      connection.setRequestProperty("Accept", "application/json");
      if (null != userAgent && !userAgent.isEmpty())
        connection.setRequestProperty("User-Agent", userAgent);

      // Get Response
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      String line;
      StringBuffer response = new StringBuffer();
      while ((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r');
      }
      rd.close();
      return response.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      if (null != connection)
        connection.disconnect();
    }
  }
}
