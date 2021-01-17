# FinalProjectSDP
REST API and stuff
# How to build
## Run these
```docker build src/main/docker/model -t postgres:alpine
docker run --network net --name postgres-0 postgres:alpine
docker build src/main/docker/controller -t asabino/wildfly:21.0.2.Final-jdk15
docker run --name sdpwildfly --network net asabino/wildfly:21.0.2.Final-jdk15
docker build src/main/docker/view -t webservernginx
docker run --name webserver --network net -d -p 8080:80 webservernginx
```

## And then

http://localhost:8080

# How to Interact

## Use Postman or Curl to register new items, deposits or deliveries

For items use: `http://localhost:8080/api/Items`

For stock use: `http://localhost:8080/api/Stock`

For deliveries use: `http://localhost:8080/api/Delivery`

### Examples for POST commands
Items: `{"name": "copper ore", "description": "1g"}`

Stock: `{"name": "copper ore", "qty" : 30}`

Delivery: `{"address": "undercity", "items": [{"name" : "peacebloom", "qty": 10}, {"name" : "copper ore", "qty": 30}]}`


### Examples for PUT commands
Items: `{"name": "copper ore", "description": "2g"}`

Stock: `{"name": "copper ore", "qty" : 30}`

Deliveries: `{"id": 33, "address": "dalaran"}`

### Examples for DELETE commands
Items: `{"name": "copper ore"}`
