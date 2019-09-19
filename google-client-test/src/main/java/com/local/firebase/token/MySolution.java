package com.local.firebase.token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySolution {

  private static final Pattern pattern = Pattern.compile("\\d+");

  private static final List<String> inputData =
      Arrays.asList(
          "Mail App, Authentication API, v6",
          "TEMP App, Authentication API, v6",
          "Video Call App, Authentication API, v7",
          "Mail App, Data Storage API, v10",
          "Chat App, Data Storage API, v11",
          "Mail App, Search API, v6",
          "Chat App, Authentication API, v8",
          "Chat App, Presence API, v2",
          "asf App, Presence API, v1",
          "Video Call App, Data Storage API, v11",
          "Video Call App, Video Compression API, v3");

  public static void maisan(String[] args) {

    class Data {
      private final String consumerName;
      private final int version;

      private Data(String[] apiInfo) {
        if (apiInfo.length != 3) {
          throw new RuntimeException("CSV file seems deformed");
        }
        consumerName = apiInfo[1];
        Matcher matcher = pattern.matcher(apiInfo[2]);
        if (matcher.find()) {
          version = Integer.parseInt(matcher.group());
        } else {
          version = 0;
        }
      }

      @Override
      public String toString() {
        return "Data{" + "'consumerName='" + consumerName + '\'' + ", version=" + version + '}';
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return version == data.version && Objects.equals(consumerName, data.consumerName);
      }

      @Override
      public int hashCode() {
        return Objects.hash(consumerName, version);
      }
    }

    Map<String, Set<Data>> map = new HashMap<>();

    for (String input : inputData) {
      String[] appInfo = input.split(",");
      String producerApiName = appInfo[1];

      if (map.containsKey(producerApiName)) {
        map.get(producerApiName).add(new Data(appInfo));
      } else {
        Set<Data> set = new TreeSet<>();
        set.add(new Data(appInfo));
        map.put(producerApiName, set);
      }
    }

    List<Data> leastApiUser = null;
    int minVer = Integer.MAX_VALUE;
    for (Set<Data> f : map.values()) {
      if (f.size() <= 1) {
        continue;
      }

      //      if (f.get(0).version <= minVer) {
      //        leastApiUser = f;
      //        minVer = f.get(0).version;
      //      }
    }

    if (leastApiUser != null) {
      for (Data s : leastApiUser) {
        System.out.println(s);
      }
    }
  }

  public static void workingExample() {
    class AnotherApp {
      private List<String> consumerList = new ArrayList<>();
      private int version;
      private boolean isUsedMoreThanOnce = false;

      AnotherApp(String[] apiInfo) {
        if (apiInfo.length != 3) {
          throw new RuntimeException("CSV file seems deformed");
        }
        consumerList.add(apiInfo[0]);
        Matcher matcher = pattern.matcher(apiInfo[2]);
        if (matcher.find()) {
          version = Integer.parseInt(matcher.group());
        } else {
          version = Integer.MAX_VALUE;
        }
      }

      AnotherApp() {}

      @Override
      public String toString() {
        return "AnotherApp{" + "consumerList=" + consumerList + ", version=" + version + '}';
      }
    }

    Map<String, AnotherApp> map = new HashMap<>();

    for (String input : inputData) {
      String[] appInfo = input.split(",");
      String producerApiName = appInfo[1];
      AnotherApp app = new AnotherApp(appInfo);

      if (map.containsKey(producerApiName)) {
        AnotherApp current = map.get(producerApiName);
        current.isUsedMoreThanOnce = true;
        app.isUsedMoreThanOnce = true;
        if (current.version > app.version) {

          map.put(producerApiName, app);
        } else if (current.version == app.version) {

          current.consumerList.addAll(app.consumerList);
        }
      } else {
        map.put(producerApiName, app);
      }
    }

    AnotherApp minVerAPI = new AnotherApp();
    minVerAPI.version = Integer.MAX_VALUE;
    for (AnotherApp api : map.values()) {
      if (api.isUsedMoreThanOnce && api.version < minVerAPI.version) {
        minVerAPI = api;
      }
    }

    // System.out.println(minVerAPI);
    for (String out : minVerAPI.consumerList) {
      System.out.println(out);
    }
  }
}
