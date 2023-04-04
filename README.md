## Springboot app with Liquibase and Oracle Database
This is a Maven project that demos how to run a Spring application in CircleCI/CD with the [Liquibase spring integration](https://www.liquibase.org/javadoc/liquibase/integration/spring/SpringLiquibase.html) and the Liquibase [Maven Plugin](https://docs.liquibase.com/tools-integrations/maven/home.html).

## GitHub Actions or CircleCI Workflows
1. The workflow will first run [Quality Checks](https://www.liquibase.com/quality-checks) using the Liquibase [Maven Plugin](https://docs.liquibase.com/tools-integrations/maven/home.html).
2. If the Quality Checks run successfully, the workflow will then build the application.  That will include running the database changes with [Liquibase spring integration](https://www.liquibase.org/javadoc/liquibase/integration/spring/SpringLiquibase.html) and packaging the application code (compiling, running unit tests and generating artifacts).
