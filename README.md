# BaSys KGT Management Application

Application for managing the BaSys integration of a Ball Screw.

Part of the BaSys4Maintain Satellite project.

Java / Spring-Boot Application.

The application can manage multiple ball-screws and register them with the BaSyx 4.0 infrastructure. A BaSyx AAS server and registry can also be started with the
applications included docker-compose file (see Installation & set-up). The management application can be used to manage the AAS of multiple ballscrews. This includes
registration and upload, deregistration and deletion, and forwarding health values from an external health-monitoring device.

# API

The OpenApi UI and documentation can be found at http://localhost:8080/swagger-ui/index.html when the server is running.

# Installation and set-up

## Requirements

- Docker (https://www.docker.com/)
- Internet connection (or pre-downloaded docker images)

## Set-up

1. open a terminal in the folder that contains docker-compose.yml
2. execute docker compose -f docker-compose-dev.yml up
3. wait until everything is started up (check in docker desktop)

## Usage

1. go to http://localhost:8080 and click on REST-API to get to the API overview
2. upload an AASX file containing an administration shell vie the POST /api/files endpoint and take note of the file-id that is contained in the api response
3. map the uploaded file to a port via the POST /api/port/{portNumber} endpoint
4. register the AAS to the registry and upload to the AAS server via the GET /api/registration/register endpoint
5. optional: check localhost:4001 and localhost:4000 (or where ever your registry and aas server are running), you should see the AAS listed there
6. you can now use the POST /api/health/{portNumber} endpoint to write health values to the AAS