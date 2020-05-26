# Profanity Check - Backend App (REST API)
This repository is one of 2 compositions:

 1. [WebUI - written in ReactJS & Node.js](https://github.com/daanney/profanity-check-ui)
 2. [REST API - written in Java & spring-boot](https://github.com/daanney/profanity-check-app)


## Introduction
This project uses spring-boot to form a microservice for profanity checks on uploaded documents. As persistence layer, an in-memory H2 database is used (any data will be lost on restart/redeployment of the backend).

### Features
The spring boot app currently serves the following features.

#### Document upload and validation
Documents can be uploaded as raw file with the name of `file`. Currently, they are stored in a global collection (without the logic to link it to a client note, those are used only in the UI to form a use case). The retrieval of documents metadata is exposed as API endpoint, however the file contents are not served after upload.
Before supplied documents are accepted and saved, a number of validation will be performed. The first basic validation consists of: file is not empty, a valid type and not too large. As a last validation, the profanity check will be performed. This process uses the list of [banned words](#banned-words) to validate the data in the file and also its filename. The data is transformed to lower-case and non-relevant characters are filtered out before this check. If a file is considered invalid based on above verification, it will be rejected for storage and an error is returned with respective message indicating the reason.

#### Banned Words
Banned words are considered as the blacklist for the profanity check performed against uploaded files. The backend exposes public CRUD endpoints to easily maintain the list of banned words for the sake of this example project. In a real-world scenario, such endpoints shall be secured properly. 
Banned words are always saved lower-case (as the profanity check is performed in lower-case) and they can be equipped with an optional description.
> Please note that on restart of the spring-boot application all the data is reset and can be created newly via the user interface.

### Properties
The following properties can be configured based on your needs.
| name | Description |
|--|--|
| `profanitycheck.upload.dir` | The path to the directory where documents should be uploaded. Must have a suffix of `/` |
| `profanitycheck.upload.maxSize` | Maximum file size to be checked when document is uploaded |
| `profanitycheck.upload.allowedTypes` | Comma separated list of mime-types to be allowed for document upload |
| `server.port` | Port number to be used by the app |
> If you change the port configuration, please make sure to use the same configuration in the ui to have it reach the backend.

### Endpoints
The following endpoints are provided by the backend

#### Documents API
| HTTP | URL | Description |
|--|--|--|
| `GET` | `/api/documents` | Retrieve collection of all document metadata |
| `POST` | `/api/documents` | Upload raw document `file` |
| `GET` | `/api/documents/:id` | Retrieve document metadata with id `:id` |


#### Banned Words API
| HTTP | URL | Description |
|--|--|--|
| `GET` | `/api/bannedwords` | Retrieve collection of all banned words |
| `POST` | `/api/bannedwords` | Add new banned word |
| `DELETE` | `/api/bannedwords/:id` | Delete banned word by ID |


## Installation
Clone this project anywhere on your machine using `git clone git@github.com:daanney/profanity-check-app.git`.


## Testing
The test coverage is focused on the main business logic only. The test cases can be run by mvn using `./mvnw clean test` (or using tools in your IDE). The test classes are located in `src/test/com/sforce/profanitycheck`. The tests cover the main functionality of both `BannedWordsService` and `DocumentsService`.


## Usage
Run the project by directly running the main class in `com.sforce.profanitycheck.ProfanityCheckApplication` from any IDE (e.g. Intellij Idea).
Additionally it can be started by command line using `./mvnw spring-boot:run`


## Deployment 
The deployment can be done manually or using an automated pipeline connecting to this repository.

### Manual deployment
Run `./mvnw clean package` to generate the production build of the app, then copy the following runnable jar as artifact to be used on your webserver:

    target/profanity-check-1.0.0.jar


### Automated deployment
In order to setup automated deployments with AWS CodePipeline, the buildpec.yml located in this repository can be used. Following high-level steps can be used to setup the environment:

 1. Fork the Github repository
 2. In AWS, create an Elastic Beanstalk application, using Java
 3. Create a new CodePipeline, specifying a name and default settings
 4. Select Github as Source Provider, selecting the forked repository and master branch
 5. Select AWS CodeBuild as build provider, select a build project (or create new)
 6. Select AWS Elastic Beanstalk as deploy provider and select the previously created Elastic Beanstalk application

## Additional notes
- **Upload Size Limit:** The maximum upload size is not only dependent on the properties files mentioned in the spring config, but also from the webserver configuration.
- **Upload Of Larger Files:** An upload of very large files could potentially be realized via a stream/chunk based approach, where the file is sent in parts instead of all in one request. The backend then should be able to receive and recognize those parts to write the complete file.

##
> Written with [StackEdit](https://stackedit.io/).