# Platform Architecture

Platform consists of multiple (micro) services which are initially split based on concerns they are addressing.
As product grows scope of each service can be narrowed more by introducing new ones. Core principles, on the other hand, 
should be kept as long as they don't impede development speed and quality of product.


## General concepts

### Service run and orchestration

Services run by Amazon Elastic Container Service (ESC) within a private subnet. 
Each service will be published as docker image.
ECS will take care of application scaling based on their usage. 

### Service discovery

ESC uses DNS balancing which will be also used for service discovery. 
There will be no dedicated discovery service and services will use internal service names to access each other.
Each service should get its peer service names from its configuration, which will be provided via environment variables via AWS config.

For local/dev environment docker-compose should be used to run the whole app locally. 
Names given to services shall be used to configure service clients accordingly.

### Exposure of public (accessible via internet) resources and balancing

AWS Load Balancer (LB) will be the only service that is exposed to public internet.
API call and static resources (web page) access will be proxied to respective service by LB.

For local/dev environment Docker instance of `nginx` (or any other) proxy can be used to imitate LB behaviour.  

Note:

Other static resources (user avatars or similar data uploaded by users) will be kept in AWS S3 storage.
For the moment there is no such data to serve, but once need arises AWS S3 data can be also exposed to internet bypassing LB.

### Web: Single page application (HTML content)

Serving Single page app should served from separate instance. 
SPA must avoid using routes to `/api/` as it is reserved to map API calls.

### Service implementation recommendations, principles and limitations

#### Exposed API URL format

Two types of API endpoints should be differentiated - EXTERNAL & INTERNAL.
EXTERNAL API will be exposed via Load Balancer (LB) to internet and should be secured according to business model.
INTERNAL API will be accessible only within private network and can omit any access control until approach is changed.

URL mapping should adhere to following convention 
`/external/{api-version-prefix}/{service-own-url-mapping}` for EXTERNAL, example -> /external/v1/user/login  
`/internal/{api-version-prefix}/{service-own-url-mapping}` for INTERNAL, example -> /internal/v1/user/search

LB will proxy calls from `{APP_URL}/{service-name}/api/.*` to `http://{service-name}:{service-port}/extarnal/.*`

While each service is free to choose its own database type to fulfil its functionality, it is recommended during initial 
stage of development to stick to PostgresSQL to avoid complications during development environment setups.

#### Logging

Configure application to log to console. Avoid file appenders or at least use RollingFileAppender and cap file size to prevent excessive disk usage.

#### Mock implementation

Along with main implementation of app also provide mock implementation of same service to smooth development of dependant services.
Check [mock server](http://www.mock-server.com/) for quick fixtures based mock implementations.

#### Service and its mock should be dockerized

Write proper Dockerfile for both main and mock implementations. All service configuration should be possible to pass via 
environment variables. Typical configuration to expose
- Database url and credentials
- Depending services endpoints names and ports
- ip/port service binds to
- jwt secrets

#### Service Client implementation

Implement client library (jar file) that will encapsulate all service calls. Service client should be configurable and configuration should be provided at runtime by consumer service. 
Typical configuration must enable (but not limited to) provisioning of service endpoint URL.
All other services must use only client library to access service INTERNAL API.

### Services and their interconnections (topology)

This initial diagram depicts all services that are needed to launch MVP. 

![](./diagrams/Service%20Topology.png?)


| Service                                     | Path prefix      | Description                                                                                                                                                                                                              | Docs
|---------------------------------------------|------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------
| Identity service                            | /auth            | Responsible for storing user identity information and authentication                                                                                                                                                     | [open](./services/identity/index.md)
| Recommendation API                          | /recommendations | Serves all types of recommendations via REST api. <br>Acts as fast cache and keeps only Thought IDs not content.<br>Exposes internal API for engine to update data once ready.                                           | [open](./services/recommendations/index.md)
| Books for sale                              | /books           | Provides API for book sellers to describe and set books for sale.<br>Acts as main repository of books for sale. Also provides internal API to<br>payments service to update (deduct) books quantity after sale complete. | [open](./services/books/index.md)
| GraphQL Resolver                            | /graphql         | Provides graphql accesses to other API. <br>Example use case is to load all all recommended quotes from books with single request.                                                                                       |
| Payments                                    | /payments        | Responsible for accepting and dispatching payments and fees.                                                                                                                                                             |
| Platform Administration                     | /admin           | Responsible to carry out platform administration tasks                                                                                                                                                                   |
| Message Queue                               |                  | Durable message queue to asynchronously feed Recommendation engine with books updates                                                                                                                                    |
| The Big Thought <br>(Recommendation engine) |                  | Analyses data in background and publishes findings to recommendations API services                                                                                                                                       |
| SPA portal                                  | /                | Instance serving all web portal related static content (html, css, images, js)                                                                                                                                           |
| Load Balancer                               | /                | Proxies all external requests to private subnet. Also enables services auto scaling                                                                                                                                      |