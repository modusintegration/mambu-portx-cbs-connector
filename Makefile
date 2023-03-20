export DOCKER_REPOSITORY_URL := 117317084632.dkr.ecr.us-east-1.amazonaws.com/mambu-portx-cbs-connector

MAVEN_VERSION = $(shell docker run --rm $(DOCKER_REPOSITORY_URL):builder-$(GIT_BRANCH) mvn -Dexec.executable="echo" -Dexec.args='$${project.version}' -Dhttp.agent="routing-engine" --non-recursive -q org.codehaus.mojo:exec-maven-plugin:1.6.0:exec)

GIT_REPOSITORY_NAME := $(shell basename `git config --get remote.origin.url` .git)
GIT_COMMIT := $(shell git log -1 --pretty=%h)
HEAD_REF := $(shell git symbolic-ref HEAD --short 2>/dev/null)
ifeq ($(HEAD_REF),)
GIT_BRANCH_FULL := $(shell git branch -a --points-at HEAD | sed -n 2p | awk '{print $$NF}')
UNMUNGED_GIT_BRANCH := $(GIT_BRANCH_FULL:remotes/origin/%=%)
else
UNMUNGED_GIT_BRANCH := $(HEAD_REF)
endif
GIT_BRANCH := $(shell echo $(subst /,-,$(or $(filter pr/%,$(CODEBUILD_SOURCE_VERSION)),$(UNMUNGED_GIT_BRANCH))) | tr '[:upper:]' '[:lower:]')

# Do not attempt to use before the container is built
BRANCH_VERSION = $(MAVEN_VERSION)-$(GIT_BRANCH)
REF_VERSION = $(MAVEN_VERSION)-ref-$(GIT_COMMIT)

status:
	@echo Repository: $(GIT_REPOSITORY_NAME)
	@echo Git commit: $(GIT_COMMIT)
	@echo Symbolic ref of HEAD:
	-git symbolic-ref HEAD --short
	@echo Branches containing head:
	-git branch -a --points-at HEAD
	@echo Calculated branch: $(GIT_BRANCH)

pull:
	@echo Pulling based on $(GIT_BRANCH)
	docker pull $(DOCKER_REPOSITORY_URL):builder-$(GIT_BRANCH) || true
	docker pull $(DOCKER_REPOSITORY_URL):runtime-$(GIT_BRANCH) || true

full_build: build
	DOCKER_BUILDKIT=0 docker build --pull --cache-from $(DOCKER_REPOSITORY_URL):builder-$(GIT_BRANCH) --cache-from $(DOCKER_REPOSITORY_URL):runtime-$(GIT_BRANCH) --tag $(DOCKER_REPOSITORY_URL):runtime-$(GIT_BRANCH) --tag $(DOCKER_REPOSITORY_URL):runtime-ref-$(GIT_COMMIT) --tag $(DOCKER_REPOSITORY_URL):dev-$(GIT_BRANCH) --build-arg SENTRY_RELEASE=$(GIT_REPOSITORY_NAME)@$(GIT_COMMIT) --build-arg SENTRY_AUTH_TOKEN=$(SENTRY_AUTH_TOKEN) .

build:
	DOCKER_BUILDKIT=0 docker build --pull --cache-from $(DOCKER_REPOSITORY_URL):builder-$(GIT_BRANCH) --target builder --tag $(DOCKER_REPOSITORY_URL):builder-$(GIT_BRANCH) --tag $(DOCKER_REPOSITORY_URL):builder-ref-$(GIT_COMMIT) --tag $(DOCKER_REPOSITORY_URL):dev-$(GIT_BRANCH) .

tag:
	docker tag $(DOCKER_REPOSITORY_URL):builder-$(GIT_BRANCH) $(DOCKER_REPOSITORY_URL):builder-$(BRANCH_VERSION)
	docker tag $(DOCKER_REPOSITORY_URL):builder-$(GIT_BRANCH) $(DOCKER_REPOSITORY_URL):builder-$(REF_VERSION)
	docker tag $(DOCKER_REPOSITORY_URL):runtime-$(GIT_BRANCH) $(DOCKER_REPOSITORY_URL):runtime-$(BRANCH_VERSION)
	docker tag $(DOCKER_REPOSITORY_URL):runtime-$(GIT_BRANCH) $(DOCKER_REPOSITORY_URL):runtime-$(REF_VERSION)
	docker tag $(DOCKER_REPOSITORY_URL):dev-$(GIT_BRANCH) $(DOCKER_REPOSITORY_URL):dev-$(REF_VERSION)
  
push:
	docker push $(DOCKER_REPOSITORY_URL):builder-$(GIT_BRANCH)
	docker push $(DOCKER_REPOSITORY_URL):builder-ref-$(GIT_COMMIT)
	docker push $(DOCKER_REPOSITORY_URL):builder-$(BRANCH_VERSION)
	docker push $(DOCKER_REPOSITORY_URL):builder-$(REF_VERSION)
	docker push $(DOCKER_REPOSITORY_URL):runtime-$(GIT_BRANCH)
	docker push $(DOCKER_REPOSITORY_URL):runtime-ref-$(GIT_COMMIT)
	docker push $(DOCKER_REPOSITORY_URL):runtime-$(BRANCH_VERSION)
	docker push $(DOCKER_REPOSITORY_URL):runtime-$(REF_VERSION)
	docker push $(DOCKER_REPOSITORY_URL):dev-$(REF_VERSION)
