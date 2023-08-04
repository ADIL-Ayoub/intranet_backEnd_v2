pipeline {
  agent any
  stages {
    stage("verify tooling") {
      steps {
        sh '''
          docker version
          docker info
          docker-compose version 
          curl --version
          jq --version
        '''
      }
    }
        stage('Change fileName on the pom file') {
      steps {
        sh 'sed -i "s_</build>_<finalName>intranet</finalName></build>_g" pom.xml'
      }
    }
          stage('Build the war file') {
      steps {
        sh 'mvn clean'
        sh 'mvn install -DskipTests'
      }
    }
          stage('Build docker image') {
      steps {
        sh 'docker build -t intranet.jar .'
      }
    }
    stage('Start container') {
      steps {
        sh 'docker-compose up -d'
        sh 'docker-compose ps'
      }
    }
  }
}
