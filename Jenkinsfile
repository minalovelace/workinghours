#!/usr/bin/env groovy

pipeline {
	agent none
	
	stages {
		stage ('Build') {
			agent any
			def ANT_HOME = tool 'Ant_1.10.1'
			steps {
				withEnv(["JAVA_HOME=${ tool 'JDK8_131' }", "ANT_HOME=${ ANT_HOME }", "PATH+ANT_HOME=${ANT_HOME }\\bin"]) {
					git branch: '$BRANCH_NAME', url: 'https://github.com/minalovelace/workinghours'
					bat 'echo PATH: %PATH%'
					echo "-------------------------"
					bat 'echo ANT_HOME: %ANT_HOME%'
				}
			}
		}
		stage ('Test') {
			agent any
			steps {
				echo "Test step"
			}			
		}
		stage ('Deploy') {
			agent any
			steps {
				echo "Deploy step"
			}			
		}
	}
}