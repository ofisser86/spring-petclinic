pipeline {
    agent any

        environment {
        PROJECT_ZONE = "${JENK_INT_IT_ZONE}"
        PROJECT_ID = "${JENK_PROJECT_ID}"
        PROD_CLUSTER = "${JENK_PROD}"
        BUILD_CONTEXT_BUCKET = "${JENK_BUCKET}"
        BUILD_CONTEXT = "build-context-${BUILD_ID}.tar.gz"
        APP_NAME = "spring-petclinic-gke"
        GCR_IMAGE = "gcr.io/${PROJECT_ID}/${APP_NAME}:${BUILD_ID}"
        APP_JAR = "${APP_NAME}.jar"
    }

    stages {
        stage('Build and Test') {
            agent {
    	    	kubernetes {
      		    cloud 'kubernetes'
                defaultContainer 'jnlp'  
      		    label 'java8-pod'
      		    yamlFile 'gke/maven-pod.yaml'
		    }       
	    }
            steps {
                container('maven') {
                        // build
                        echo 'Running build automation'
	    	            sh "mvn clean package"

		                // run tests
		                sh "mvn verify"
			            // bundle the generated artifact    
		                sh "cp target/${APP_NAME}-*.jar $APP_JAR"

		                // archive the build context for kaniko			    
			            sh "tar --exclude='./.git' -zcvf /tmp/$BUILD_CONTEXT ."
		                sh "mv /tmp/$BUILD_CONTEXT ."
		                step([$class: 'ClassicUploadStep',
                               credentialsId: env.JENK_INT_IT_CRED_ID,
                               bucket: "gs://${BUILD_CONTEXT_BUCKET}",
                               pattern: env.BUILD_CONTEXT])
		        }
	        }
        }
    }
}
