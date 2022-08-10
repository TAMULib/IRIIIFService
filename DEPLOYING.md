<a name="readme-top"></a>
# Deployment Guide

## Production Deployments

For **production** deployments, deploy using `docker`.
This is the recommended method of deployment for production systems.

### Build Docker image
```shell
docker build --tag=service
```
### Configure
Follow spring-boot external configuration guide to override application.yml properties using environment variables.
### Run
Deploy image in infrastructure using configured environment variables.

The **development** deployment can also use `docker` to run locally.

<div align="right">(<a href="#readme-top">back to top</a>)</div>

## Development Deployment using **Docker**

Build and run using `docker`, run the following:

```shell
docker image build -t service .
docker run -it service
```

```
docker build --help
# -t, --tag list                Name and optionally a tag in the
```

```
docker run --help
# -i, --interactive             Keep STDIN open even if not attached.
# -t, --tty                     Allocate a pseudo-TTY
```

<div align="right">(<a href="#readme-top">back to top</a>)</div>

## Development Deployment using **Maven**

Running locally is easiest using spring boot:

```shell
mvn spring-boot:run
```

<div align="right">(<a href="#readme-top">back to top</a>)</div>
