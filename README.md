# JobRunner
* Job Runner is a Java Spring Boot application that enables the execution of predefined job step by step.
* It interacts with a remote service through HTTP requests to trigger an action, check the status of the action, and get the response when the action is completed every 5 minutes.
* The application is designed to run multiple jobs simultaneously, handle failures, and continue execution from the last stopped point.

## Table of Contents
- [Features](#features)
- [Requirements](#requirements)
- [Configuration](#configuration)
- [Getting Started](#getting-started)

## Features
* Trigger predefined jobs to initiate actions on remote services.
* Check the status of actions and wait for them to complete.
* Print the response when the actions are completed successfully.
* Support for running multiple jobs simultaneously from a shared job pool.
* Restarting and continuing job execution in case of failures.
* In-memory storage of job status and execution details.
* Test cases for the service layer.

## Requirements
- Java 11 or later
- Apache Maven 3.6.x or later

## Configuration
The application can be configured through the application.properties file located in the ```src/main/resources``` directory.
Customize the properties as needed, especially the remote service URLs.<br>
You can also change the Database configuration in the application.properties file.

## Getting Started
1. Clone the repository from GitHub: <br>
``` git clone https://github.com/AnshulPandey-01/job-processing.git ```
2. Build the project using Maven: <br>
``` mvn clean install ```
3. Run the Application: <br>
``` mvn spring-boot:run ```
4. The application will start running on port 8080.
