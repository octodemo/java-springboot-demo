# Java Demo App

**The App**

This is a simple Sales Manager Java App that stores sales items in a table presented in a web app.

This demo repo is designed to help understand some of **CI/CD** (Continuous Integration/Continuous Delivery) principles and best practices.

**The Settings**

**Java** (Spring Boot framework) with **MVC** (Model View Controller) and **OOP** (Object Oriented Programming) design patterns.
 
 *CI/CD Pipeline*
 [GitHub Actions](https://docs.github.com/en/enterprise-cloud@latest/actions) as the main CI/CD pipeline orchestrator
Tools used to optimize the pipeline (See the `.github/workflows/ci.yml` for more detailed configuration).

 - [Caching Dependencies to Speed Up Workflows](https://docs.github.com/en/enterprise-cloud@latest/actions/using-workflows/caching-dependencies-to-speed-up-workflows)
 - [Using the Metrix Strategy to Run Unit Tests In Parallel](https://docs.github.com/en/enterprise-cloud@latest/actions/using-jobs/using-a-matrix-for-your-jobs)
              
# CI/CD Diagram
```mermaid
stateDiagram
    state Continuous-Integration {
    Commits --> PR: Developers Commit new changes in a Pull Request
    PR --> CI: Security Scans, Build & Unit Test Suit
    CI --> PR: Feedback of failed tests - back to dev
    CI --> Publish: If CI passes, \nmerging to main branch \nand publishing Containerised\n App to GHCR
    }
    
    state Parallel-Testing {
        CI --> JunitTest1
        CI --> JunitTest2
        CI --> JunitTest3
        CI --> JunitTest..N
    }
    
    state Continuous-Delivery {
    Publish --> PreProdTests: Pulling Image from GHDR
    PreProdTests --> Deploy: Deploy the app to K8s
    }
```

# Gitgraph Diagram - Developer Workflow
```mermaid
%%{init: { 'logLevel': 'debug', 'theme': 'base', 'gitGraph': {'showBranches': true}} }%%
      gitGraph
        commit
        branch hotfix
        checkout hotfix
        commit
        branch develop
        checkout develop
        commit id:"ash" tag:"abc"
        branch featureB
        checkout featureB
        commit type:HIGHLIGHT
        checkout main
        checkout hotfix
        commit type:NORMAL
        checkout develop
        commit type:REVERSE
        checkout featureB
        commit
        checkout main
        merge hotfix
        checkout featureB
        commit
        checkout develop
        branch featureA
        commit
        checkout develop
        merge hotfix
        checkout featureA
        commit
        checkout featureB
        commit
        checkout develop
        merge featureA
        branch release
        checkout release
        commit
        checkout main
        commit
        checkout release
        merge main
        checkout develop
        merge release
```

 *Building and Testing*
 - [Maven](https://maven.apache.org/) as the project management for Building and Testing the application.


User input data is stored in an Oracle (PDB) Database.


For easy demos, an H2 database (Oracle Mode) is setup by default in the `src/main/resources/application.properties` file.