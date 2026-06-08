#!/usr/bin/env groovy

node {
    @Library('pipeline-library') _
    def jenkinsRoot = "${JENKINS_HOME}/workspace"
    def appVer = ''
    def lastCommitMessage = ''

    environment {
        colordust = credentials('colordust')
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
