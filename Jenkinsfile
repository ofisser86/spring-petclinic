pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Running build automation'
                // sh './mvnw package'
                git url: 'https://github.com/ofisser86/spring-petclinic.git'
    withMaven {
      sh "mvn clean verify"
    } // withMaven will discover the generated Maven artifacts, JUnit Surefire & FailSafe reports and FindBugs reports
                archiveArtifacts artifacts: 'dist/petClinic.zip'
            }
        }
        
        }
    }
