pipeline {
    agent any

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
        stage("Deploy to QA") {
            // Production branch
            when { branch 'QA' }
            
                agent {
                    kubernetes {
                    cloud 'kubernetes'
                    label 'gke-deploy'
                    yamlFile 'gke/gke-deploy-pod.yaml'
                    }
                }
                
	        steps{
                input 'Should we continue?'
                milestone(1)
		        container('gke-deploy') {
		        sh "sed -i.bak s#IMAGE#${GCR_IMAGE}#g gke/app-deployment.yaml"
                step([$class: 'KubernetesEngineBuilder', namespace:'qa', projectId: env.PROJECT_ID, clusterName: env.PROD_CLUSTER, location: env.PROJECT_ZONE, manifestPattern: 'gke/app-service.yaml', credentialsId: env.JENK_INT_IT_CRED_ID, verifyDeployments: false])
                step([$class: 'KubernetesEngineBuilder', namespace:'qa', projectId: env.PROJECT_ID, clusterName: env.PROD_CLUSTER, location: env.PROJECT_ZONE, manifestPattern: 'gke/app-deployment.yaml', credentialsId: env.JENK_INT_IT_CRED_ID, verifyDeployments: true])
            }
        }
	}
  }
}