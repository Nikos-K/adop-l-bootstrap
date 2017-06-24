def ADOP_PLATFORM_REPO_URL = "https://github.com/Nikos-K/adop-lite-platform-management.git"

job('Load_ADOP_Lite'){
	description("This job is responsible for retrieving the ADOP Lite platform management repository as well as creating and running all the jobs required to load cartridges.")
	parameters{
    stringParam("ADOP_PLATFORM_REPO_URL","https://github.com/Nikos-K/adop-lite-platform-management.git","The URL of the git repo for Platform Management.")
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
      external("bootstrap/Jenkins_Configuration/add_env_vars.groovy")
			lookupStrategy('JENKINS_ROOT')
    }
		dsl {
      external("bootstrap/**/*.groovy")
			lookupStrategy('JENKINS_ROOT')
    }
    conditionalSteps {
      condition {
        booleanCondition('${SETUP_PLUGGABLE_SCM_LIBRARY}')
      }
      runner('Fail')
      steps {
        downstreamParameterized {
          trigger('Jenkins_Configuration/Setup_Adop_Pluggable_Scm_Library') {
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
        booleanCondition('${GENERATE_EXAMPLE_WORKSPACE}')
      }
      runner('Fail')
      steps {
        downstreamParameterized {
          trigger('Platform_Management/Generate_Example_Workspace') {
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
}
