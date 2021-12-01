pipeline {
    agent none
        environment {
        PROJECT_ZONE = "${JENK_INT_IT_ZONE}"
        PROJECT_ID = "${JENK_PROJECT_ID}"
        PROD_CLUSTER = "${JENK_PROD}"
        BUILD_CONTEXT_BUCKET = "${JENK_BUCKET}"
        BUILD_CONTEXT = "build-context-${BUILD_ID}.tar.gz"
        APP_NAME = "spring-petclinic"
        GCR_IMAGE = "gcr.io/${PROJECT_ID}/${APP_NAME}:${BUILD_ID}"
        APP_JAR = "${APP_NAME}.jar"
        FE_SVC_NAME = "${APP_NAME}-frontend"
    }

    stages{
        stage('Checkout code') {
            agent {
    	    	kubernetes {
      		    cloud 'kubernetes'
                defaultContainer 'jnlp'  
      		    		    }
            }
          steps {
            checkout scm
        }
          }
        stage("Deploy to QA") {
            // QA branch
        
                agent {
                    kubernetes {
                    cloud 'kubernetes'
                    yamlFile 'gke/gke-deploy-pod.yaml'
                    }
                }
            input {
                    message "Select BUILD ID to deploy"
                    ok "Done"
                    parameters {
                       string(name: "BUILD_ID", defaultValue: "latest", description:"Enter Build number")
                              }
                  }
	        steps{
                echo "Deploy to qa environment the build with number ->> ${BUILD_ID}"
		        container('gke-deploy') {
		        sh "sed -i.bak s#IMAGE#${GCR_IMAGE}#g gke/app-deployment.yaml"
                step([$class: 'KubernetesEngineBuilder', namespace:'qa', projectId: env.PROJECT_ID, clusterName: env.PROD_CLUSTER, location: env.PROJECT_ZONE, manifestPattern: 'gke/app-service.yaml', credentialsId: env.JENK_INT_IT_CRED_ID, verifyDeployments: false])
                step([$class: 'KubernetesEngineBuilder', namespace:'qa', projectId: env.PROJECT_ID, clusterName: env.PROD_CLUSTER, location: env.PROJECT_ZONE, manifestPattern: 'gke/app-deployment.yaml', credentialsId: env.JENK_INT_IT_CRED_ID, verifyDeployments: true])
            }
        }
	}
  }
}