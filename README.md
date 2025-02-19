# Cassino
This is a game made with Scala and ScalaFX, which emulates the (Finnish version) card game [Cassino](https://en.wikipedia.org/wiki/Cassino_(card_game)).
It can be played in either local multiplayer, vs AI, or a combination of both.
Created as a project for the course Programming studio 2.

## Dependencies
* Tested to work on OpenJDK17 (and doesn't work on some newer ones)
* The following libraries are required on Linux, and should be in the LD_LIBRARY_PATH
  environment variable
    * gtk3
    * libGL
    * libXxf86vm
    * libXxtst
    * glib

A flake.nix is supplied with the repository for use with direnv/devenv to start
a devshell with the above libraries, Scala, and Sbt with OpenJDK17. Requires Nix.

## Running the project
The project can be ran through sbt with 
```
sbt run
```

