test-docker images


```bash
PORT=8080 && docker run \
   -e PORT=${PORT} \
   -e GOOGLE_APPLICATION_CREDENTIALS=/Users/rahul/Documents/My_Home/GCP_Work/Important_Docs/rahulkesharwani-service-account.json \
   -v $GOOGLE_APPLICATION_CREDENTIALS:/Users/rahul/Documents/My_Home/GCP_Work/Important_Docs/rahulkesharwani-service-account.json:ro \
   gcr.io/${google_cloud_project}/verify-bigtable:v1
```


To Send image to gcr
```bash
./mvnw -DskipTests com.google.cloud.tools:jib-maven-plugin:build \
  -Dimage=gcr.io/${google_cloud_project}/verify-bigtable:v4
  
  gcloud auth print-access-token | docker login -u oauth2accesstoken --password-stdin https://gcr.io
  
  docker run \
     -ti --rm -p 8080:8080 \
     -e GOOGLE_APPLICATION_CREDENTIALS=/Users/rahul/Documents/My_Home/GCP_Work/Important_Docs/rahulkesharwani-service-account.json \
     -v $GOOGLE_APPLICATION_CREDENTIALS:/Users/rahul/Documents/My_Home/GCP_Work/Important_Docs/rahulkesharwani-service-account.json:ro \
     gcr.io/${google_cloud_project}/verify-bigtable:v1
```

To Deploy in the kubernetes clusters
```bash
kubectl create deployment hello-java \
  --image=gcr.io/${google_cloud_project}/verify-bigtable:v1
  
  docker build -t gcr.io/${google_cloud_project}/verify-bigtable:v4
```

To update with another version of application
```bash

kubectl create deployment understand-pod \ 
--image=gcr.io/${google_cloud_project}/verify-bigtable:v3
kubectl set image deployment/hello-java \
  hello-java=gcr.io/$GOOGLE_CLOUD_PROJECT/hello-java:v2
```

Rollback
kubectl rollout undo deployment/hello-java

./mvnw -DskipTests com.google.cloud.tools:jib-maven-plugin:build \
  -Dimage=gcr.io/${google_cloud_project}/verify-bigtable:v3