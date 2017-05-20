def ADOP_PLATFORM_REPO_URL = "https://github.com/Nikos-K/adop-platform-management.git"

job('Load_ADOP_Lite'){
	description("This job is responsible for retrieving the ADOP Lite platform management repository as well as creating and running all the jobs required to load cartridges.")
	parameters{
    stringParam("ADOP_PLATFORM_REPO_URL","${ADOP_PLATFORM_MANAGEMENT_GIT_URL}","The URL of the git repo for Platform Management.")
    booleanParam("INSTALL_JENKINS_PLUGINS", true, "Set to true to install the Jenkins Pluggins required for ADOP Lite.")
    booleanParam("SETUP_PLUGGABLE_SCM_LIBRARY", true, "Set to true to setup the ADOP Pluggable SCM Library.")
    booleanParam("GENERATE_EXAMPLE_WORKSPACE", true, "Should an example workspace be generated?")
	}
	scm{
		git{
			remote{
				name("origin")
				url("${ADOP_PLATFORM_REPO_URL}")
				credentials("adop-jenkins-master")
			}
			branch("*/master")
		}
	}
	wrappers {
		preBuildCleanup()
		maskPasswords()
		timestamps()
		sshAgent("adop-jenkins-master")
	}
	authenticationToken('gAsuE35s')
	steps {
		dsl {
      external("bootstrap/**/*.groovy")
			lookupStrategy('JENKINS_ROOT')
    }
		conditionalSteps {
      condition {
        booleanCondition('${INSTALL_JENKINS_PLUGINS}')
      }
      runner('Fail')
      steps {
        downstreamParameterized{
          trigger("Jenkins_Configuration/Install_PLugins"){
            block {
              buildStepFailure('FAILURE')
              failure('FAILURE')
              unstable('UNSTABLE')
            }
          }
				}
      }
    }
    conditionalSteps {
      condition {
        booleanCondition('${SETUP_PLUGGABLE_SCM_LIBRARY}')
      }
      runner('Fail')
      steps {
        downstreamParameterized{
          trigger("Jenkins_Configuration/Setup_ADOP_Pluggable_SCM_Library"){
            block {
              buildStepFailure('FAILURE')
              failure('FAILURE')
              unstable('UNSTABLE')
						}
					}
				}
      }
    }
	}
  publishers{
    downstreamParameterized{
      trigger("Platform_Management/Generate_Example_Workspace"){
        condition("SUCCESS")
        triggerWithNoParameters(true)
      }
    }
  }
}
