# What's need for production?

1. Container with PostgreSQL
2. Container with Keycloak
3. File with configuration for Keycloak

## Files which help you
* docker-compose.yml - run all containers
* car-rental.json - security configuration for Keycloak. Container just import this

## How to start containers
Just run docker compose and that's it:
```shell
docker-compose up
```
