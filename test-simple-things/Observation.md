Currently deployed in GKE with 8CPU-30GB machine:

1. With resources: CPU:"1.0", limit:"4" -> returns 4CPU:
    - veneer-app-deployment-7bcb74b878-f42z8   1/1     Running   0          7d10h
    - veneer-app-deployment-7bcb74b878-gm2vp   1/1     Running   0          7d10h
    - veneer-app-deployment-7bcb74b878-m79fz   1/1     Running   0          7d10h

2. On Local(mac) machine with 8CPU:
    - grpc-default-executor-1 - 1 thread
    - grpc-nio-worker-ELG-1 - 8 thread

3. 