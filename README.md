# Solitaire-Settlers-of-Catan-ComputerGame
Solitaire Settlers of Catan ComputerGame

Categories: Board Games

License: GNU General Public License version 2.0 (GPLv2)

Brought to you by: sdetoni (original author)

Original source: (https://sourceforge.net/projects/solitairecatan/)[https://sourceforge.net/projects/solitairecatan/]

Solitaire Settlers of Catan computer game is a Java rendition of the popular board game "The Settlers of Catan" or "Die Siedler Von Catan" by Klaus Teuber. This project allows you to play the board game against a computer AI.

## Project Samples

<img width="684" height="172" alt="image" src="https://github.com/user-attachments/assets/942adf83-00dc-4a22-9f82-ab79861ac411" />

## Requirements

- **Java 25** (JDK 25 or newer)

## Build and run

The project uses the [Maven Wrapper](https://maven.apache.org/wrapper/) (`mvnw`), so you do not need Maven installed separately.

Run the game:

```bash
./mvnw exec:java
```

Or use the helper script (builds and starts the packaged JAR):

```bash
./run.sh
```

Build a runnable fat JAR:

```bash
./mvnw clean package
```

The executable JAR is written to `target/PPSee-jar-with-dependencies.jar`. You can also run it directly:

```bash
java -jar target/PPSee-jar-with-dependencies.jar
```

