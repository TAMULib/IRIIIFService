<a name="readme-top"></a>
# Institutional Repository International Image Interoperability Framework Service Deployment Guide

## Production Deployments

For **production** deployments, deployment should ideally be done using `docker-compose`.
However, there is currently no *App* repository for *IRIIIF Service* providing a `docker-compose` file.

For now, **production** deployments are identical to **development** deployments as described in the **Docker** method below.

<div align="right">(<a href="#readme-top">back to top</a>)</div>


## Development Deployment using Docker

To manually use `docker` rather than `docker-compose`, run the following:

```shell
docker image build -t iriifservice .
docker run -it iriifservice
```

<sub>_* Note: `-t iriifservice` and `-it iriifservice` may be changed to another tag name as desired, such as `-t developing_on_this` and `-it developing_on_this`._</sub><br>

<div align="right">(<a href="#readme-top">back to top</a>)</div>


## Development Deployment using Maven

Manual deployment can be summed up by running:

```shell
mvn spring-boot:run
```

Those steps are a great way to start but they also fail to explain the customization that is often needed.
There are multiple ways to further configure this for deployment to better meet the desired requirements.

It is highly recommended only to perform *manual installation* when developing.
For **production** deployment, please use the **Docker** method above.

<div align="right">(<a href="#readme-top">back to top</a>)</div>


### Directly Configuring the `src/main/resources/application.yml` File

This method of configuration works by altering the configuration file.

With this in mind, the deployment steps now look like:

```shell
# Edit 'src/main/resources/application.yml' here.

mvn spring-boot:run
```

<div align="right">(<a href="#readme-top">back to top</a>)</div>


<!-- LINKS -->
