# launchpad-backend

Sets up a web api on a local network for listing and launching media files on the host computer.

This is just a small hobby project, no effort has been put into security or access control, do not expose on the internet.

# Requirements
* Linux or possibly a posix os.
* Java runtime 17

# Installation

## How to build
### Method 1 - native
1. Install open jdk 17 and apache maven
2. Run `mvn clean package` to build the jar.
3. Resulting .jar is located in the target directory.

### Method 2 - docker
1. Install docker
2. Run `make build`
3. Resulting .jar is located in `docker/output/`


## Configuration
* Create `/etc/launchpad/settings.json`. See the examples in examples/settings

## Starting the server
Run `java -jar launchpad.jar`. Java is a memory hog if not constrained, you can apply heap limits by passing jvm arguments
like so `java -Xmx256M -jar launchpad.jar`.

## Usage
Use the web frontend [launchpad-ui](https://github.com/n1mras/launchpad-ui) to start a library refresh by pressing the refresh button in the top right corner.
Alternatively peform a post using curl or the provided swagger UI at http://localhost:8000/swagger-ui/index.html

    curl -X 'POST' \
    'http://localhost:8000/api/v1/library/video/refresh' \
    -H 'accept: */*' \
    -d ''`