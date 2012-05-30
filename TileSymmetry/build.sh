#!/bin/bash

javac edu/smcvt/tilesymmetry/*.java
javac TileDriver.java
javadoc -author -linksource -noqualifier java.util:java.io TileDriver.java edu.smcvt.tilesymmetry

