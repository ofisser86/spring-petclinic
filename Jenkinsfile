pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Running build automation'
                sh './mvnw package'
                archiveArtifacts artifacts: 'dist/petClinic.zip'
            }
        }
        stage('DeployToProduction') {
            when {
                branch 'master'
            }
            steps {
                steps {
                sh 'scripts/deliver.sh'
              }
            }
        }
    }
}
