## Source code index

Initial skeleton of components created with their `Dockerfile`s to enable deployment at Amazon ECS.

## Short description of each component

### `api-docs`
- contains swagger yaml files documenting `external` APIs using.
- Dockerfile to publish on AWS for convenience

### `api-gateway`
This component is simple `nginx` reverse proxy that exposes external APIs to web by rewriting paths. 
Also acts as acts as internal balancer. 
It leverages DNS discovery/balancing provided by `AWS Cloud Map`

### `auth-service`
Simple stub implementation of authentication service using `spring-boot`.
It uses internal `h2` database and populates some users during startup.

### `spa-portal`

Some landing page created using `Angular`. 
Dockerfile enables build and serve from `nginx` container.

### `docker-compose.yml`

Configures all above service containers and provide aws-like setup for local run.

To run everything on local machine run following in shell

    git clone https://github.com/zukalt/blueprint-online-shopping.git
    cd  blueprint-online-shopping/source-code
    docker-compose up

Requires `docker` & `docker-compose` on local machine. 
All (web app and auth service) builds are done withing docker images.

Access with browser [portal](http://127.0.0.1) and [swagger docs](http://127.0.0.1/swagger-ui/)  


