// =============================================================================
// Day 37 – CI/CD & DevOps | Part 1
// File: 03-jenkins-pipeline.groovy
// Topic: Jenkins Declarative Pipeline — Build, Test, Quality Gate, Publish
// Domain: Bookstore Spring Boot Application
// =============================================================================
// This is a real Jenkinsfile using Jenkins Declarative Pipeline syntax.
// Place it at the root of your repository as: Jenkinsfile
//
// Jenkins Concepts:
//   pipeline    → the entire Jenkinsfile block
//   agent       → where the pipeline runs (any node, Docker container, K8s pod)
//   stages      → collection of stage blocks
//   stage       → a named phase visible in the Jenkins UI (BlueOcean)
//   steps       → commands to run inside a stage
//   post        → actions to run AFTER the pipeline (always, success, failure)
//   environment → pipeline-wide env variables
//   when        → conditional execution of a stage
//   parameters  → values the user can pass when manually triggering the build
// =============================================================================

pipeline {

    // ─────────────────────────────────────────────────────────────────────────
    // AGENT — where to run the pipeline
    // ─────────────────────────────────────────────────────────────────────────
    agent {
        // Option A: Run directly on any Jenkins node with the "java" label
        // label 'java'

        // Option B: Run inside a Docker container (no JDK required on the agent)
        docker {
            image 'maven:3.9.4-eclipse-temurin-17'
            args '-v $HOME/.m2:/root/.m2'   // Mount Maven cache from host → faster builds
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PARAMETERS — user-provided values when triggering manually
    // ─────────────────────────────────────────────────────────────────────────
    parameters {
        choice(
            name: 'DEPLOY_ENV',
            choices: ['none', 'staging', 'production'],
            description: 'Deploy to environment after a successful build?'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip tests (use only for emergency hotfixes)'
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ENVIRONMENT — pipeline-wide variables
    // ─────────────────────────────────────────────────────────────────────────
    environment {
        JAVA_VERSION         = '17'
        MAVEN_OPTS           = '-Xmx2048m'
        APP_NAME             = 'bookstore-app'
        // Jenkins Credentials Plugin — inject secrets safely (not hardcoded)
        DOCKER_REGISTRY_CRED = credentials('docker-hub-credentials')   // username:password
        SONAR_TOKEN          = credentials('sonarqube-token')
        SLACK_WEBHOOK        = credentials('slack-webhook-url')
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TOOLS — pre-install tools via Jenkins Global Tool Configuration
    // ─────────────────────────────────────────────────────────────────────────
    tools {
        maven 'Maven 3.9'           // Name must match Jenkins → Manage Jenkins → Tools
        jdk   'OpenJDK 17'
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STAGES — the actual pipeline stages shown in BlueOcean / Stage View
    // ─────────────────────────────────────────────────────────────────────────
    stages {

        // ── STAGE 1: CHECKOUT ────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                // Checkout from the branch that triggered this build
                checkout scm

                // Display build info for traceability
                sh '''
                    echo "=== Build Info ==="
                    echo "Branch:   ${GIT_BRANCH}"
                    echo "Commit:   ${GIT_COMMIT}"
                    echo "Build #:  ${BUILD_NUMBER}"
                    echo "Job:      ${JOB_NAME}"
                    java -version
                    mvn --version
                '''
            }
        }

        // ── STAGE 2: BUILD (COMPILE) ─────────────────────────────────────────
        stage('Build') {
            steps {
                sh 'mvn compile --no-transfer-progress'
            }
            post {
                failure {
                    echo "❌ Compilation failed. Check for syntax errors."
                }
            }
        }

        // ── STAGE 3: UNIT TESTS ──────────────────────────────────────────────
        stage('Unit Tests') {
            when {
                // Skip this stage if user checked SKIP_TESTS
                not { expression { params.SKIP_TESTS == true } }
            }
            steps {
                sh '''
                    mvn test \
                      -DskipITs \
                      -Dspring.profiles.active=test \
                      --no-transfer-progress
                '''
            }
            post {
                always {
                    // Publish JUnit XML results — shows pass/fail graph over time
                    junit 'target/surefire-reports/**/*.xml'

                    // Archive JaCoCo coverage report
                    jacoco(
                        execPattern:    'target/jacoco.exec',
                        classPattern:   'target/classes',
                        sourcePattern:  'src/main/java',
                        exclusionPattern: '**/*Test*.class',
                        minimumLineCoverage: '80'       // Quality gate: fail if < 80%
                    )
                }
            }
        }

        // ── STAGE 4: CODE QUALITY ────────────────────────────────────────────
        stage('Code Quality') {
            parallel {
                // Run Checkstyle and SpotBugs in PARALLEL — saves time
                stage('Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:check --no-transfer-progress'
                    }
                }
                stage('SpotBugs') {
                    steps {
                        sh 'mvn spotbugs:check --no-transfer-progress'
                        // Archive SpotBugs XML for the Jenkins SpotBugs plugin
                        recordIssues(tools: [spotBugs(pattern: 'target/spotbugsXml.xml')])
                    }
                }
                stage('OWASP Dependency Check') {
                    steps {
                        sh '''
                            mvn org.owasp:dependency-check-maven:check \
                              --no-transfer-progress \
                              -DfailBuildOnCVSS=7
                        '''
                        // Publish the HTML report
                        publishHTML([
                            allowMissing: false,
                            reportDir:    'target',
                            reportFiles:  'dependency-check-report.html',
                            reportName:   'OWASP Dependency Report'
                        ])
                    }
                }
            }
        }

        // ── STAGE 5: SONARQUBE ANALYSIS (optional — requires SonarQube server) ─
        stage('SonarQube Analysis') {
            when {
                // Only run SonarQube on main branch (too slow for every PR)
                branch 'main'
            }
            steps {
                withSonarQubeEnv('SonarQube') {    // Name matches Jenkins config
                    sh '''
                        mvn sonar:sonar \
                          -Dsonar.projectKey=bookstore \
                          -Dsonar.host.url=http://sonarqube:9000 \
                          -Dsonar.login=${SONAR_TOKEN} \
                          --no-transfer-progress
                    '''
                }
                // Wait for SonarQube quality gate result (webhook must be configured)
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // ── STAGE 6: INTEGRATION TESTS ───────────────────────────────────────
        stage('Integration Tests') {
            when {
                not { expression { params.SKIP_TESTS == true } }
            }
            steps {
                // Testcontainers (in the test code) spins up Postgres automatically.
                // No need for an external DB service — Docker must be available on the agent.
                sh '''
                    mvn failsafe:integration-test failsafe:verify \
                      -Dspring.profiles.active=integration-test \
                      --no-transfer-progress
                '''
            }
            post {
                always {
                    junit 'target/failsafe-reports/**/*.xml'
                }
            }
        }

        // ── STAGE 7: PACKAGE ─────────────────────────────────────────────────
        stage('Package') {
            steps {
                script {
                    // Set artifact version: pom version + CI build number
                    def pomVersion = sh(
                        script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                        returnStdout: true
                    ).trim()
                    env.ARTIFACT_VERSION = "${pomVersion}-build.${BUILD_NUMBER}"
                    echo "Artifact version: ${env.ARTIFACT_VERSION}"
                }
                sh 'mvn package -DskipTests --no-transfer-progress'
                // Archive the JAR as a Jenkins build artifact (downloadable from build page)
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        // ── STAGE 8: PUBLISH TO ARTIFACT REPOSITORY ──────────────────────────
        stage('Publish Artifact') {
            when {
                branch 'main'
            }
            steps {
                // Deploy JAR to Nexus / Artifactory / GitHub Packages
                sh 'mvn deploy -DskipTests --no-transfer-progress'
                echo "✅ Published ${APP_NAME}:${env.ARTIFACT_VERSION} to artifact repository"
            }
        }

        // ── STAGE 9: DEPLOY (triggered manually via parameters) ──────────────
        stage('Deploy') {
            when {
                // Only deploy if the user selected an environment in build parameters
                not { expression { params.DEPLOY_ENV == 'none' } }
            }
            steps {
                script {
                    def targetEnv = params.DEPLOY_ENV
                    echo "🚀 Deploying ${APP_NAME}:${env.ARTIFACT_VERSION} to ${targetEnv}"
                    // In a real pipeline, this would call your deploy script or kubectl
                    sh "./scripts/deploy.sh ${targetEnv} ${env.ARTIFACT_VERSION}"
                }
            }
        }

    } // end stages

    // ─────────────────────────────────────────────────────────────────────────
    // POST — actions that run after ALL stages complete
    // ─────────────────────────────────────────────────────────────────────────
    post {
        always {
            echo "Pipeline completed. Status: ${currentBuild.currentResult}"
            // Clean up workspace to free disk space on the agent
            cleanWs()
        }
        success {
            echo "✅ Pipeline SUCCESS — ${JOB_NAME} #${BUILD_NUMBER}"
            // Notify Slack (requires Slack Notification Plugin)
            // slackSend(
            //     channel: '#deployments',
            //     color: 'good',
            //     message: "✅ Build passed: ${JOB_NAME} #${BUILD_NUMBER} - ${env.ARTIFACT_VERSION}"
            // )
        }
        failure {
            echo "❌ Pipeline FAILED — ${JOB_NAME} #${BUILD_NUMBER}"
            // Email the team
            emailext(
                subject: "❌ Build Failed: ${JOB_NAME} #${BUILD_NUMBER}",
                body:    "Check console output: ${BUILD_URL}console",
                to:      'team@bookstore.com'
            )
        }
        unstable {
            // "Unstable" = build compiled but tests failed or quality gate warned
            echo "⚠️ Build UNSTABLE — tests failed or quality gate warning"
        }
    }

} // end pipeline

// =============================================================================
// JENKINS KEY CONCEPTS REFERENCE
// =============================================================================
//
//  Scripted vs Declarative Pipeline:
//    Declarative → structured syntax (pipeline { stages { stage { steps }}})
//    Scripted    → Groovy DSL, more flexible but harder to read
//    → Use Declarative; use `script {}` blocks inside steps for Groovy logic
//
//  Jenkins Credentials Plugin:
//    Never hardcode passwords. Store in Jenkins → Manage Credentials.
//    Reference with: credentials('credential-id')
//    Or: withCredentials([usernamePassword(...)]) block
//
//  Parallel stages:
//    Use `parallel {}` to run independent stages simultaneously.
//    Reduces total pipeline time significantly for independent checks.
//
//  Branch-based execution:
//    Use `when { branch 'main' }` to gate expensive stages (SonarQube, deploy)
//    to specific branches only.
//
//  BlueOcean:
//    Jenkins' modern UI plugin — shows pipeline as a visual flowchart.
//    Install from Jenkins Plugin Manager.
