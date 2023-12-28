# ITSU Excel Task

## Deployed environment

* [API URL](https://itsu-excel.azurewebsites.net)
* [Swagger UI](https://itsu-excel.azurewebsites.net/swagger-ui/index.html)

## How to set up local environment

### For Developers
1) Install and open [Docker Desktop](https://www.docker.com/products/docker-desktop/)
2) Run [docker compose](https://docs.docker.com/engine/reference/commandline/compose_up/);
   execute `docker compose up` in `docker/` folder
3) In IntelliJ "Preferences...", under Build, Execution, Deployment > Build Tools > Maven > Runner, select the option "
   Delegate IDE build/run actions to Maven."
4) Run Application by starting RAYWApplication class or using the following commands `./mvnw spring-boot:run`


### Run via Docker Container
1. Make sure you have installed [Docker](https://www.docker.com/get-started/) on you development environment.
2. Run the command in Terminal
`docker run -d -p 8080:8080 --name excel mrzhenshen/itsu-excel:latest`
3. For now you have access to Excel API locally available by URL: [http://localhost:8080](http://localhost:8080/). Also сheck out [Swagger](http://localhost:8080/swagger-ui/index.html) with the API definition.
4. Use `docker [stop | start] excel` to manage the created container.
