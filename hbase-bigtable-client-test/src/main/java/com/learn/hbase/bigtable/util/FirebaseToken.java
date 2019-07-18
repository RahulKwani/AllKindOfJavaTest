package com.learn.hbase.bigtable.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class FirebaseToken {

  private static final String SCOPES = "https://www.googleapis.com/auth/firebase.messaging";

  public static void main(String[] args) throws IOException {
    System.out.println(getAccessToken());
  }

  private static String getAccessToken() throws IOException {
    GoogleCredential googleCredential =
        GoogleCredential.fromStream(
                new FileInputStream(
                    "/Users/rahul/Documents/My_Home/PerProj/Apps/Impfiles"
                        + "/chat-app-performace-firebase.json"))
            .createScoped(
                Arrays.asList(
                    "https://www.googleapis.com/auth/firebase.database",
                    "https://www.googleapis.com/auth/userinfo.email"
                )
            );
    googleCredential.refreshToken();
    return googleCredential.getAccessToken();
  }
}
