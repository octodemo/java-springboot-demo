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
    state Developer-Workflow {
    Commits --> PR: Developers Commit new changes in a Pull Request
    PR --> Build: Security Scans, Build & Unit Test Suit
    }
    
    state Continuous-Integration {
        state GitHub-Advanced-Security {
        Build --> PR: Feedback of failed tests - back to dev
        Build --> JunitTests: Security Scans, Build & Unit Test Suite
        JunitTests --> Publish: If CI passes, \nmerging to main branch \nand publishing Containerised\n App to GitHub\n Container Registry
        state Parallel-Testing {
        JunitTests --> JunitTest1: Each test runs in \na containerized environment
        JunitTests --> JunitTest2
        JunitTests --> JunitTest3
        JunitTests --> JunitTest4
        JunitTests --> JunitTest5
        JunitTests --> JunitTest..N
        }
      }
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
