#!/usr/bin/env groovy
/*
Multibranch UTD build for Jenkins

Sets version to the branch name for downstream BrailleBlaster job on the same branch
*/

pipeline {
	agent any

/*
	triggers {
		Disabled, 
		triggered with http://test.brailleblaster.org/jenkins.php 
		due to https://issues.jenkins-ci.org/browse/JENKINS-48656 
		bitbucketPush()
	}
*/

	stages {
		stage("Build Data") {
			steps {
				sh 'cloc --by-file --xml --out=cloc.xml src build.gradle'
				sloccountPublish pattern: 'cloc.xml'
				openTasks pattern: '*'	
			}
		}
		stage('Build') {
			steps {
				// Run gradle
				sh '''
					export BUILD=$(if [ "$( hg branch )" = "default" ]; then echo publish; else echo build; fi)
					./gradlew clean $BUILD -PignoreTestFailures=true -PfindbugsXML=true
				'''
			}
			post {
				always {
					// junit testResults: 'build/test-results/test/*.xml'
					// findbugs pattern: 'build/reports/findbugs/*.xml'
					// pmd pattern: 'build/reports/pmd/*.xml'
					warnings consoleParsers: [[parserName: 'Java Compiler (javac)']]
					
					// last to prevent more issues
					archiveArtifacts allowEmptyArchive: true, artifacts: "libembosser-utils/build/libs/*.jar,libembosser-core-drivers/build/libs/*.jar,libembosser-api/build/libs/*.jar"
				}
			}
		}
	}
}

