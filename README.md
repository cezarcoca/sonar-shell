# sonar-shell
SonarQube SHELL

**How to build**

Clone a copy of the main SonarQube Shell git repository by running:
```shell
git clone https://github.com/cezarcoca/sonar-shell.git
```
Enter the sonar-shell directory
```shell
cd sonar-shell
```
Run the build script:
```shell
mvn clean package
```

**Usage**

To launch SonarQube Shell use the below command
```shell
java -jar sonar-shell-1.0-RC.jar
```

**Shell commands**

<<<<<<< HEAD
For assistance press or type `help` then hit ENTER. Type `help <command>` to read more about a specific command.
=======
For assistance press or type `help` then hit ENTER. Type `git <command>` to read more about a specific command.
>>>>>>> 36d417ff59035f13d0a2dad66c793e5062070c1f
These are the common SonarQube shell commands used in various situations:

`connect`

Connects to the SonarQube server. If this is not specified, attempts to connect to localhost, port 9000 using the HTTP protocol.

```shell
connect --host sonarqube.com --port 443 --protocol https
```

`disconnect`

Disconnects from the SonarQube server

`export`

Exports the issues reported by the SonarQube to a file using the maintainability profile provided as parameter

```shell
export --projectKeys "closure:library,org.apache.tika:tika,c-family:nginx" --profile maintainability.json --path issues.json
```

The profile file format is presented below:

```js
{
  "category": "maintainability",
  "axes": [
    {
      "name": "complexity",
      "rules": ["javascript:FunctionComplexity", "squid:MethodCyclomaticComplexity", "squid:S1067", "cpp:FunctionComplexity"]
    },
    {
      "name": "readability",
      "rules": ["javascript:NestedIfDepth", "squid:S00115", "squid:S134", "c:S134"]
    }
  ]
}
```
<<<<<<< HEAD
=======
 
>>>>>>> 36d417ff59035f13d0a2dad66c793e5062070c1f
