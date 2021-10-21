pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Running build automation'
                // sh './mvnw package'
     withMaven {
      sh './mvnw package'
sh 'java -jar target/*.jar'
    } // withMaven will discover the generated Maven artifacts, JUnit Surefire & FailSafe reports and FindBugs reports
                archiveArtifacts artifacts: 'dist/petClinic.zip'
            }
        }
        
        }
    }
