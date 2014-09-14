# dependencyAnalysis
==================

The goal of this project is to build a tool to analyse java source|bytecode and store the detected dependencies in a graph database.

## What it currently can do
- Analye a directory or ear,jar,war file and all its containing classes
- The analyse only contains the name of the class, the package, the container it's from and all references to other classes via fields
- Store the result in a neo4j database

## What it can't do (yet)
- There is no consistent data model, classes are stored/loaded only by their name. There should be nodes for container and packages.
- Use concurrency (fork/join? akka?)
- Analyse dependency injection (find implementation for interface which are used in classes)
- Handle inner classes correct
