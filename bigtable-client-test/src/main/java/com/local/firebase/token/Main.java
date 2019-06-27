package com.local.firebase.token;

/* Do not add a package declaration */
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

/* DO NOT CHANGE ANYTHING ABOVE THIS LINE */
/* You may add any imports here, if you wish, but only from the
 standard library */

/* Do not add a namespace declaration */

public class Main {

  private static final Pattern PATTERN = Pattern.compile("\\d+");

  public static void main(String[] args) {
    class AnotherApp {
      private List<String> consumerList = new ArrayList<>();
      private int version;
      private boolean isUsedMoreThanOnce = false;

      //These are implicitly private bcoz class is private
      AnotherApp(String[] apiInfo) {
        if (apiInfo.length != 3) {
          throw new RuntimeException("CSV file seems deformed");
        }
        consumerList.add(apiInfo[0]);
        Matcher matcher = PATTERN.matcher(apiInfo[2]);
        if (matcher.find()) {
          version = Integer.parseInt(matcher.group());
        } else {
          version = Integer.MAX_VALUE;
        }
      }

      AnotherApp(){
      }

      @Override
      public String toString() {
        return "AnotherApp{" + "consumerList=" + consumerList + ", version=" + version
            + '}';
      }
    }

    Map<String, AnotherApp> apiData = new HashMap<>();
    String line = "";
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(
          new File("input.txt")));

      while ((line = bufferedReader.readLine()) != null) {
        String[] appInfo = line.split(",");
        String producerApiName = appInfo[1];
        AnotherApp app = new AnotherApp(appInfo);

        if (apiData.containsKey(producerApiName)) {
          AnotherApp current = apiData.get(producerApiName);
          current.isUsedMoreThanOnce = true;
          app.isUsedMoreThanOnce = true;
          if(current.version > app.version) {

            apiData.put(producerApiName, app);
          } else if(current.version == app.version){

            current.consumerList.addAll(app.consumerList);
          }
        } else {
          apiData.put(producerApiName, app);
        }
      }
      bufferedReader.close();

      AnotherApp minVerAPI = new AnotherApp();
      minVerAPI.version = Integer.MAX_VALUE;
      for (AnotherApp api : apiData.values()) {
        if (api.isUsedMoreThanOnce && api.version < minVerAPI.version) {
          minVerAPI = api;
        }
      }

      PrintWriter output = new PrintWriter(new BufferedWriter(
          new FileWriter("output.txt")));
      for (String s : minVerAPI.consumerList) {
        output.println(s);
      }
      output.close();
    } catch (IOException e) {
      System.out.println("IO error in input.txt or output.txt");
    }
  }
}