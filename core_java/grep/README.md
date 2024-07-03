# [Introduction](#introduction)
The purpose of this application is to mimic the Linux grep command that will allow for the use of searching
matching strings from files using expressions. It accomplishes this by recursively searching in a target directory
for specific files that match the regular expression and then returns the results. The application can be operated 
through a Docker image or locally using a JAR file.

## [Quick Start](#quick-start)
### Docker
Docker must be installed in order to operate the application, once installed
you can then follow these instructions to successfully run the application.
``` 
#Pull image from Docker
docker pull michaeladebayo/grep
#Run the container
docker run --rm -v `pwd`/data:/data -v `pwd`/out:/out jrvs/grep ${regex_pattern} ${src_dir} /out/${outfile}
```

### JAR File
If Docker is not available, it is possible to run the application using a JAR file
```
mvn clean compile package

java -cp target/grep-1.0-SNAPSHOT.jar ca.jrvs.apps.grep.JavaGrepImp .*Romeo.*Juliet.* ./data ./out/grep.txt

```

# [Implementation](#implementation)
## Pseudocode
```
matchedLines = []
for file in listFilesRecursively(rootDir)
  for line in readLines(file)
      if containsPattern(line)
        matchedLines.add(line)
writeToFile(matchedLines)
```

## Performance Issue

An issue that arose in the development of this application has to do with memory, since the
application collects and stores a list of all files in the directory, it can cause an issue
if there is large files to copy. A solution to this memory issue would be to incorporate Java
streams and Lambda functions that can better handle this situation.

# [Test](#test)
Testing for this application was done manually, results from the Linux command was used as a reference
to ensure the correct output was given. 

# [Deployment](#deployment)
The application is available on Docker hub:
[Docker Hub](https://hub.docker.com/r/michaeladebayo/grep)


# [Improvements](#improvements)
- Can allow for the use of options when searching, so specific searches can be found
- To help with performance use more Stream APIs
- Improve the logging and exception handling 
