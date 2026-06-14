#!/usr/bin/env groovy

node {
    @Library('pipeline-library') _
    def jenkinsRoot = "${JENKINS_HOME}/workspace"
    def appVer = ''
    def lastCommitMessage = ''

    environment {
        colordust = credentials('colordust')
        JAVA_HOME = "/usr/lib/jvm/jdk"
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    try {
        stage('Clean') {
            clean()
        }

        stage('Environment') {
            envi()

            echo "test page 1 : https://saravanjs.com:4001/statichtmls "
            echo "test page 2 : https://saravanjs.com:8001/ "
        }

        stage('Checkout') {
            (appVer, lastCommitMessage) = checkoutscm();
        }

        stage('Build') {
            sh '''
                    JAVA_HOME="/usr/lib/jvm/jdk" mvn clean package install -T 1C -DskipTests  
                    java -version
                '''


        }





        stage('SonarQube') {
            try
            {
                def scannerHome = tool 'sonar-scanner';
                withSonarQubeEnv() {
                    def sonarServerReachable = sh(script: 'curl -s -o /dev/null -w "%{http_code}" http://sjsdb:9000', returnStatus: true) == 200
                    if (sonarServerReachable) {
                        echo "SonarQube server is running."
                        sh "${scannerHome}/bin/sonar-scanner"
                    } else {
                        echo 'SonarQube server is down. Continuing without analysis.'
                    }
                }
            } catch (Exception e) {
                echo "SonarQube analysis failed: ${e.message}. Continuing the job."
            }
        }

        stage('Package') {
            tarpack()
        }

        stage('Deploy') {
            deploy()
        }

        stage('Archive') {
            archive(appVer)
        }

        stage('Install') {
            install()
        }

        stage('Email') {
            email.success( appVer, lastCommitMessage)
        }
    } catch (Exception error) {
        email.failed( appVer, lastCommitMessage)
    }

}
