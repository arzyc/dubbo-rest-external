pipeline {
    agent any

    stages {
        stage('Build') {
            agent { docker 'maven:3.6.3-jdk-11' }
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
            agent { docker 'maven:3.6.3-jdk-11' }
            steps {
                sh 'mvn package'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}