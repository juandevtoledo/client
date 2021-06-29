# clients-business-capability

### Documentation 

[Swagger UI sandbox](http://lbk-nlb-sand-b4ae09a807e738b7.elb.us-east-1.amazonaws.com:8081/clients/swagger-ui/#/)

Swagger URL pattern: 

```${LoadBalancerURL}:8081/clients/swagger-ui/#/```

Run component with the Sandbox infra

```shell script
GOOGLE_APPLICATION_CREDENTIALS=firebase/firebase-adminsdk.json gradle bootRun --args='--spring.profiles.active=sandbox'
```

In addition, it is necessary to export the following vars for the micro to interact with Keycloak.  

export FLEXI_CLIENT_ID="asdfasdf"

export FLEXI_SECRET="asdfasdf"

export KEYCLOAK_API_USER="asdfasdf"

export KEYCLOAK_API_PASSWORD="asdfasdf"

Please, check with Devops team these vars values.

Enjoy programming