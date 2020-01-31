Currently deployed in GKE with 8CPU-30GB machine:

1. With resources: CPU:"1.0", limit:"4" -> returns 4CPU:
    - veneer-app-deployment-7bcb74b878-f42z8   1/1     Running   0          7d10h
    - veneer-app-deployment-7bcb74b878-gm2vp   1/1     Running   0          7d10h
    - veneer-app-deployment-7bcb74b878-m79fz   1/1     Running   0          7d10h

2. On Local(mac) machine with 8CPU:
    - grpc-default-executor-1 - 1 thread
    - grpc-nio-worker-ELG-1 - 8 thread

3. Suppose if our nodes have 4 cpus and the current deployment has occupied 3 cores and if you try to start a new deployment with 2 core as well tehn it won't stop the existing jobs.
Those jobs needs to be deleted manually/ or the previous jobs deplyment can be deleted(less peferred route).

4. requests & limits config is for each pod which is deployed under the nodes.

5. if u just specify resources->requests->1 then also it takes 100% of CPUs.



--------------------
--------------------

## Container Deployment

While deploying this client in [Google Kubernetes Engine(GKE)](https://cloud.google.com/kubernetes-engine) with [CoS](https://cloud.google.com/container-optimized-os/docs/). Please make sure to provide CPU configuration in your deployment file. If `resources` are not configured than JVM would detect only 1 CPU, which affects the channel creation, resulting in performance degradation.

For example when deploying this client on a cluster with 8 CPU nodes:
```yaml
appVersion: v1
...
spec:
  ...
  container:
    resources:
      requests:
        cpu: "1" # Here 1 represents 100% of single node CPUs(i.e. 8) whereas other than 1 represents the number of CPU it would use from a node.
```
see [Assign CPU Resources to Containers](https://kubernetes.io/docs/tasks/configure-pod-container/assign-cpu-resource/#specify-a-cpu-request-and-a-cpu-limit) for more information.
































