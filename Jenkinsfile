#!/usr/bin/env groovy

pipeline {
	agent none
	
	stages {
		stage ('Build') {
			agent any
			withEnv(["JAVA_HOME=${ tool 'JDK8_131' }", "PATH+ANT_HOME=${tool 'Ant_1.10.1'}/bin"]) {
				git branch: '$BRANCH_NAME', url: 'https://github.com/minalovelace/workinghours'
				bat echo %PATH%
				bat echo %ANT_HOME%
			}
		}
		stage ('Test') {
			agent any
			
		}
		stage ('Deploy') {
			agent any
			
		}
	}
}