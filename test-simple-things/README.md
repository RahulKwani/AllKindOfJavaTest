test-docker images


```bash
export google_cloud_project=ProjectName

PORT=8080 && docker run \
   -e PORT=${PORT} \
   -e GOOGLE_APPLICATION_CREDENTIALS=/Users/rahul/Documents/My_Home/GCP_Work/Important_Docs/rahulkesharwani-service-account.json \
   -v $GOOGLE_APPLICATION_CREDENTIALS:/Users/rahul/Documents/My_Home/GCP_Work/Important_Docs/rahulkesharwani-service-account.json:ro \
   gcr.io/${google_cloud_project}/verify-bigtable:v1

# To Send image to gcr

./mvnw -DskipTests com.google.cloud.tools:jib-maven-plugin:build \
  -Dimage=gcr.io/${google_cloud_project}/verify-bigtable:v5
  
  gcloud auth print-access-token | docker login -u oauth2accesstoken --password-stdin https://gcr.io
  
  docker run \
     -ti --rm -p 8080:8080 \
     -e GOOGLE_APPLICATION_CREDENTIALS=/Users/rahul/Documents/My_Home/GCP_Work/Important_Docs/rahulkesharwani-service-account.json \
     -v $GOOGLE_APPLICATION_CREDENTIALS:/Users/rahul/Documents/My_Home/GCP_Work/Important_Docs/rahulkesharwani-service-account.json:ro \
     gcr.io/${google_cloud_project}/verify-bigtable:v1

# To Deploy in the kubernetes clusters

kubectl create deployment hello-java \
  --image=gcr.io/${google_cloud_project}/verify-bigtable:v1
  
  docker build -t gcr.io/${google_cloud_project}/verify-bigtable:v4

# To update with another version of application

kubectl create deployment understand-pod \ 
--image=gcr.io/${google_cloud_project}/verify-bigtable:v3
kubectl set image deployment/hello-java \
  hello-java=gcr.io/$GOOGLE_CLOUD_PROJECT/hello-java:v2

kubectl rollout undo deployment/hello-java

./mvnw -DskipTests com.google.cloud.tools:jib-maven-plugin:build \
  -Dimage=gcr.io/${google_cloud_project}/verify-bigtable:v3
  
```
  
  

To enable the JVM to use maximum of the CPUs within each pod, the please refer this [section](https://kubernetes.io/docs/tasks/configure-pod-container/assign-cpu-resource/#specify-a-cpu-request-and-a-cpu-limit).
  		
```yaml
apiVersion: apps/v1beta1
kind: Deployment
metadata:
  name: app-name
spec:
  ...
  template:
    metadata:
      labels:
        app: app-name
    spec:
      containers:
        - name: container-name
          ...
          resources:
            limits:
              cpu: "4" # To avail more than one CPU while deploying in GKE.
```
