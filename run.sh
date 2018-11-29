#!/bin/bash

javac src/assignment/*.java -d bin
java -cp lib/postgresql-42.2.5.jar:bin assignment.Program
