## ircd-pastiche

A Simple IRCD written in Java, for ease of adding custom code and allow better protection against crackers/hackers.

## Getting Started

### Prerequisites

Java 6 or later. (Yeah. It will run on really old JVM)

### Installing

Currently, there is no pre-package installation. Nor a released jar file. This will come in the future.

## Running the tests

Currently, there are none. This was developed before the advent of Unit Tests. As it is I am not sure how you unit test an IRC server but it is something that can be worked on in the future.

### Break down into end to end tests

## Deployment

Extract the release file. Edit the server.xml for your configuration

./bin/ircd.sh start

**Note**: A script used to start IRC server will be created in a bit.

## Built With

* [Ant](https://ant.apache.org)

## Authors

[Charles Miller](https://sourceforge.net/u/carlfish/profile/) - Initial work on the IRC server
Paul Franz - Took over development 2003-05-29

## License

This project is licensed under the GPLv2 License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

Thank you Charles Miller for creating this simple IRC server.
