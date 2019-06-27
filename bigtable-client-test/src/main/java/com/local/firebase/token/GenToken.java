package com.local.firebase.token;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;

public class GenToken {

  public static void mainOld(String[] args) throws IOException {
    // Load the service account key JSON file
    FileInputStream serviceAccount = new FileInputStream("/Users/rahul/Documents/My_Home/PerProj"
        + "/Apps/Impfiles/chat-app-performace-firebase.json");

    GoogleCredential scoped = GoogleCredential
        .fromStream(serviceAccount)
        .createScoped(
            Arrays.asList(
                "https://www.googleapis.com/auth/firebase.database",
                "https://www.googleapis.com/auth/userinfo.email"
            )
        );
    // Use the Google credential to generate an access token
    scoped.refreshToken();
    String token = scoped.getAccessToken();
    System.out.println(token);
  }

  public static void main(String[] args) {
    ArrayList<String> inputDataList = new ArrayList<String>();
    // int numLines = 0;
    String line = "";
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(
          new File("input.txt")));

      while ((line = bufferedReader.readLine()) != null) {
        // String line = in.nextLine();
        if (!line.isEmpty())
          inputDataList.add(line);
        // numLines++;
      }
      bufferedReader.close();
      ArrayList<String> apiList = new ArrayList<String>();
      HashMap<String, Integer> apiCountMap = new HashMap<String, Integer>();


      for (String str : inputDataList) {
        str = str.substring(str.indexOf(",") + 1);
        apiList.add(str);
        str = str.substring(0, str.indexOf(","));

        if (apiCountMap.containsKey(str)) {
          apiCountMap.put(str, apiCountMap.get(str) + 1);
        } else {
          apiCountMap.put(str, 1);
        }
      }
      Collections.reverse(apiList);
      TreeMap<String, String> sortedApiMap = new TreeMap<String, String>();
      for (String api : apiList) {
        sortedApiMap.put(api.substring(0, api.indexOf(",")),
            api.substring(api.indexOf(",") + 1));

      }
      Iterator<String> it = sortedApiMap.keySet().iterator();
      Set<String> outputApps = new TreeSet<String>();
      while (it.hasNext()) {
        String apiName = it.next();
        if (apiCountMap.get(apiName) > 1) {
          for (String str : inputDataList) {
            if (str.contains(apiName + ","
                + sortedApiMap.get(apiName))) {
              outputApps.add(str.substring(0, str.indexOf(",")));
            }
          }

        }
      }
      PrintWriter output = new PrintWriter(new BufferedWriter(
          new FileWriter("output.txt")));
      Iterator<String> it1 = outputApps.iterator();
      while (it1.hasNext()) {
        output.write(it1.next());
      }

      output.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("IO error in input.txt or output.txt");
    }
  }

}
