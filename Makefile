DOCKERTAG=local/launchpad-builder

.PHONY: help build

help:
	$(info ---------------------------------------------------------)
	$(info `make build`: builds a runnable jar using docker. Result in docker/output)
	$(info ---------------------------------------------------------)

build:
	docker build --pull -t $(DOCKERTAG) -f docker/Dockerfile .
	docker run --mount type=bind,source=$(shell pwd)/docker/output,target=/opt/output $(DOCKERTAG)