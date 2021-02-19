pipeline {
    agent any

    stages {
        stage('Build') {
            agent { podman 'maven:3.6.3-jdk-11' }
            steps {
                sh 'mvn clean compile'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('package') {
            agent { podman 'maven:3.6.3-jdk-11' }
            steps {
                echo 'mvn package'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}