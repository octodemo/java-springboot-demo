# All policies start with the org package definition
package org

policy_name["config_version"]

# signal to circleci that check_version is enabled and must be included when making a decision
enable_rule["check_version"]

# signal to circleci that check_version is a hard_failure condition and that builds should be
# stopped if this rule is not satisfied.
hard_fail["check_version"]

# define check version
check_version = reason {
    not input.version # check the case where version is not in the input
    reason := "version must be defined"
} {
    not is_number(input.version) # check that version is number
    reason := "version must be a number"
} {
    not input.version >= 2.1 # check that version is at least 2.1
    reason := sprintf("version must be at least 2.1 but got %s", [input.version])
}