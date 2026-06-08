# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Solitaire Settlers of Catan — a Java Swing desktop rendition of the board game where a human plays against computer AI opponents. Originally authored by Steven De Toni (2008, GPLv2); recently migrated to a Maven build on Java 25. All code lives in the single `Catan` package under `src/main/java/Catan/`.

## Commands

Requires **JDK 25+**. Maven is provided via the wrapper (`./mvnw`), so no separate install is needed.

```bash
./mvnw exec:java          # run the game directly from sources
./run.sh                  # package then launch the fat JAR
./mvnw clean package      # build target/PPSee-jar-with-dependencies.jar
java -jar target/PPSee-jar-with-dependencies.jar   # run the packaged JAR
```

Main class: `Catan.Main`. There is **no test suite** — verification is manual by running the GUI.

## Architecture

This is a Swing GUI with no networking, persistence, or backend. The big pieces:

- **`Main`** sets the look-and-feel, opens `CatanJFrame`, and calls `newGame()`.
- **`CatanJFrame`** is the top-level window. It owns the `GameBoardJPanel gameBoard` and lazily creates a `GameRules` instance per game (menu actions like "Start New Game" rebuild it).
- **`GameRules`** is the game engine / turn controller. It holds the list of `Player` objects, drives turn order, dice rolls (`DiceRollInfo`), the robber, trading, and win conditions. It implements `GameMouseNotifyInterf` so board clicks are routed back into rule logic.
- **`Player`** (~4500 lines, the largest file) represents one participant and contains the **computer AI heuristics**. Each `Player` is constructed with a color, the shared `gameBoard`, the `GameRules`, and its own `ResBuildPanel`. Human vs. AI behavior is selected by skill level and an AI play list.
- **`GameBoardJPanel`** holds the hex map data and renders it.

### Board geometry: the `CatanGraphBase` hierarchy

The board is modeled as geometric polygons. `CatanGraphBase extends java.awt.Polygon` is the abstract base for everything drawable/clickable on the map:

- **`Tile`** — a resource hex (wood/brick/sheep/wheat/rock/desert) with a number token.
- **`BuildPoint`** — a vertex where settlements/cities are placed.
- **`Road`** — an edge connecting two `BuildPoint`s (`buildJoins` links the two endpoints).

Adjacency between tiles, points, and roads is wired up through these objects, and hit-testing uses the inherited `Polygon` shape.

### Supporting code

- `ResourceCard`, `TraderItem`, `TradeJDialog` — resources, the bank/trade economy, and trading UI.
- `DevCardResourcePickerJDialog`, `StealPlyrResCardJDialog`, `RobOver7CardsJDialog`, `StealCardJPanel` — development cards and robber interactions.
- `Catan.Partical` — a small particle/explosion effect system used for visual flourishes.
- `Catan.MultiLineToolTip` — multi-line Swing tooltip helper.

## Working with the Swing GUI (important)

The UI was built with **NetBeans Matisse**. Most window/dialog/panel classes have a paired `*.form` file (XML GUI definition) alongside the `*.java`. In those `.java` files, the code between the `//GEN-BEGIN` / `//GEN-END` and `// <editor-fold ...> Generated Code` markers (e.g. the `initComponents()` method) is **machine-generated from the `.form` file** — editing it by hand is fragile and will be overwritten if the form is regenerated. Prefer editing the `.form` (or the NetBeans designer) for layout/component changes; put custom logic in the hand-written event handler methods and other regions outside the generated blocks.

The build depends on `org.swinglabs:swing-layout` (`org.jdesktop.layout`), which the generated Matisse code uses for layout.

## Resources

Images (`.png`/`.jpg`/`.gif`) and audio (`.wav`) live in `src/main/resources/Catan/Resource/` and are loaded from the classpath at the path `Catan/Resource/...`. The packaged JAR uses `Catan/Resource/splash.jpg` as its splash screen (configured in `pom.xml`).
