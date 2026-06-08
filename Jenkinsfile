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
            mvnbuild()

        }


        stage('SonarQube') {

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
