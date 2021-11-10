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

  stages {
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
    stage('Build and Test') {
      agent {
        kubernetes {
          cloud 'kubernetes'
          // defaultContainer 'jnlp'  
          label 'java8-pod'
          yamlFile 'gke/maven-pod.yaml'
        }
      }
      steps {
        container('java') {
          // build
          script {
            echo 'Running build automation'
            echo 'Executing pipeline for branch $BRANCH_NAME'
          }
          sh "pwd"
          sh "./mvnw package"
          // bundle the generated artifact    
          sh "cp target/${APP_NAME}-*.jar $APP_JAR"

          // archive the build context for kaniko			    
          sh "tar --exclude='./.git' -zcvf /tmp/$BUILD_CONTEXT ."
          sh "mv /tmp/$BUILD_CONTEXT ."
          step([$class: 'ClassicUploadStep',
            credentialsId: env.JENK_INT_IT_CRED_ID,
            bucket: "gs://${BUILD_CONTEXT_BUCKET}",
            pattern: env.BUILD_CONTEXT
          ])
        }
      }
    }
    stage("Publish Image") {
      agent {
        kubernetes {
          cloud 'kubernetes'
          label 'kaniko-pod'
          yamlFile 'gke/kaniko-pod.yaml'
        }
      }
      environment {
        PATH = "/busybox:/kaniko:$PATH"
      }
      steps {
        container(name: 'kaniko', shell: '/busybox/sh') {
          sh ''
          '#!/busybox/sh /
          kaniko / executor - f `pwd` / Dockerfile - c `pwd`--context = "gs://${BUILD_CONTEXT_BUCKET}/${BUILD_CONTEXT}"--destination = "${GCR_IMAGE}"--build - arg JAR_FILE = "${APP_JAR}"
          ''
          '
        }
      }
    }
    stage("Deploy to QA") {
      // QA branch
      agent {
        kubernetes {
          cloud 'kubernetes'
          label 'gke-deploy'
          yamlFile 'gke/gke-deploy-pod.yaml'
        }
      }

      steps {

        container('gke-deploy') {
          sh "sed -i.bak s#IMAGE#${GCR_IMAGE}#g gke/app-deployment.yaml"
          step([$class: 'KubernetesEngineBuilder', namespace: 'qa', projectId: env.PROJECT_ID, clusterName: env.PROD_CLUSTER, location: env.PROJECT_ZONE, manifestPattern: 'gke/app-service.yaml', credentialsId: env.JENK_INT_IT_CRED_ID, verifyDeployments: false])
          step([$class: 'KubernetesEngineBuilder', namespace: 'qa', projectId: env.PROJECT_ID, clusterName: env.PROD_CLUSTER, location: env.PROJECT_ZONE, manifestPattern: 'gke/app-deployment.yaml', credentialsId: env.JENK_INT_IT_CRED_ID, verifyDeployments: true])
        }
      }
    }

    stage("Deploy to prod") {
      // Production branch
      when {
        branch 'main'
      }
      agent {
        kubernetes {
          cloud 'kubernetes'
          label 'gke-deploy'
          yamlFile 'gke/gke-deploy-pod.yaml'
        }
      }

      steps {

        input 'Should we continue?'
        milestone(1)

        container('gke-deploy') {
          sh "sed -i.bak s#IMAGE#${GCR_IMAGE}#g gke/app-deployment.yaml"
          step([$class: 'KubernetesEngineBuilder', namespace: 'production', projectId: env.PROJECT_ID, clusterName: env.PROD_CLUSTER, location: env.PROJECT_ZONE, manifestPattern: 'gke/app-service.yaml', credentialsId: env.JENK_INT_IT_CRED_ID, verifyDeployments: false])
          step([$class: 'KubernetesEngineBuilder', namespace: 'production', projectId: env.PROJECT_ID, clusterName: env.PROD_CLUSTER, location: env.PROJECT_ZONE, manifestPattern: 'gke/app-deployment.yaml', credentialsId: env.JENK_INT_IT_CRED_ID, verifyDeployments: true])
        }
      }
    }
  }
}