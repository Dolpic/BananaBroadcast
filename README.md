# BananaBroadcast
## About
BananaBroadcast is a broadcasting software design to manage songs, jingles and others sounds file a radio may have to work with.

The project is built using [Maven](https://maven.apache.org/)

## Building the project
The project is a classical Maven project.

To build it you first need to have Maven installed on your machine.
Then run "mvn package -DskipTests" in the project's root directory. (Currently test are under development and need to be skipped using -DskipTests).

The .jar with dependencies in the target folder is the fully packed application.

## Running the application

On startup, the application requires a working MySQL server.
The server's credentials can be specified in the auto-generated config.properties.

To simply test the app, one can use a simple MySQL server, [USBWebserver](https://www.usbwebserver.net/webserver/), a lightweight and portable web server.

## Usage

## Current limitations and further development

Current the only supported audio format are WAV and MP3.


---
## Code Architecture

### Non-GUI classes

#### AudioPlayer
Provide simple fonctions to load / play / pause / stop an audio file.  
Enable the selection of the output peripheral amount the available ones.

Provide also functions to register callbacks on the main events.

#### Recorder
**This class is not implemented yet**  
Provide function to register an audio stream from a peripheral to a given file.

#### To be completed

### GUI classes
To be completed
 