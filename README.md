

# mambu-portx-cbs-connector
# Mambu PortX CBS Connector


<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#open-banking-api-reference">Open Banking API Reference</a>
    </li>
    <li>
      <a href="#build-and-deploy">Build and Deploy</a>
      <ul>
        <li><a href="#how-to-run-app-locally">How to run app locally</a></li>
        <li><a href="#how-to-deploy-to-dev-cluster">How to deploy to dev cluster</a></li>
      </ul>
    </li>
    <li><a href="#postman-collection">Postman Collection</a></li>
    <li><a href="#running-the-tests">Running the tests</a></li>
    <li><a href="#resources">Resources</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#authors">Authors</a></li>
  </ol>
</details>

# About The Project
This is a PortX connector for Mambu core banking system based on Portx Open Banking API

## Open Banking API Reference

```http

  GET /persons - Search individual customers

  GET /persons/{{personId}} - Get individual customer by Id

  GET /persons/{{personId}}/accounts - Get accounts of individual customer by Id

  GET /organizations - Search non-individual customers

  GET /organizations/{{organizationId}} - Get non-individual customer by id

  GET /organizations/{{organizationId}}/accounts - Get accounts of non-individual customer by id

```


## Open Banking API

## Build and Deploy

### How to run app locally

1. Set username and password in application.yml properties


2. ```export MAVEN_OPTS=--add-opens=java.base/java.util=ALL-UNNAMED```

3. ```mvn clean install -DskipTests```

4. ```mvn clean package -DskipTests```

5. Then run src/main/java/io.portx.mambu.Application

6. Test via Postman Collection

### How to deploy to dev cluster
Please refer to below guide
- https://portx.atlassian.net/wiki/spaces/PRTX/pages/2243428681/Deployment+Guide

## Postman Collection
Collection:
```
postman/mambu-portx-cbs-connector.postman_collection.json
```
Env file:
```
postman/mambu-portx-cbs-connector-local.postman_environment.json
```
## Running the tests
  ```mvn test```

## Resources

* CI/CD repo: https://github.com/modusintegration/demos-custom-environments
* CBS Knowledge Base:
https://portx.atlassian.net/wiki/spaces/PRTX/pages/2254766095/Mambu
* Confluence: 

## Compliance
Information regarding CVEs vulnerabilities.

## License
License information

## Authors
- [Shashikant Hirugade](https://github.com/shashi165)
- [Vinicius Ribeiro](https://github.com/viniciusribeiroportx)
- [Joaquin Rojkind](https://github.com/joaquinrojkind)
- [Juan Correa](https://github.com/gibaros)
