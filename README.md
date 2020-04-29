# Automated-CFG-Generation
Prototype Project to generate CFG from Java source code

# Generating a CFG

## Installing Requirements
Requires that python3 is installed.

To install python3 dependencies:

```pip install -r requirements.txt```

## Using parse.py
To view the options and descriptions of options, run

```python3 parse.py help```

# Running Tests

## Setup

Requires that Java 7 and MuJava https://cs.gmu.edu/~offutt/mujava/ is installed.

Use the mujava directory as the MuJava_HOME.

## Generating Test Requirements

Requires that the subjects under test and the compiled class files are in `Automated-CFG-Generation/mujava/src/`. First run the Makefile to compile the test requirement generator and run pre-generate-trs.sh, e.g. 

```make```

and 

```./pre-generate-trs.sh```

generate-trs.sh is configured to generate test requirements for each java file in the `java_files` variable defined in generate-trs.sh. It will also generate the test requirements for each coverage criteron in the `coverage_types` variable. Running this script will do the following for each java file: 
- Generate the JSON files representing the CFGs and store them in `Automated-CFG-Generation/mujava/json/`

- Dump a description of the generated CFG in `Automated-CFG-Generation/mujava/auto-graphs/`

- For each coverage criterion, derive a minimal set of test paths that satisfy the criterion and dump the result in `Automated-CFG-Generation/mujava/test-requirements/`

To write JUnit tests and run them against mutants of the subjects, see https://cs.gmu.edu/~offutt/mujava/
