package ca.jrvs.apps.grep;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;

public class JavaGrepImp implements JavaGrep {

    final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

    private String regex;
    private String rootPath;
    private String outFile;


    @Override
    public void process() throws IOException {
       List<String>matchedLines = new ArrayList<>();

       for(File file : listFiles(rootPath)) {
           for (String line : readLines(file)) {
               if(containsPattern(line)){
                   matchedLines.add(line);
               }
           }
       }
       writeToFile(matchedLines);

    }

    @Override
    public List<File> listFiles(String rootDir) {

        List<File>matchedFiles = new ArrayList<>();
        File dir = new File(rootDir);
        File[] files = dir.listFiles();
        for(File file : files) {
            if(file.isDirectory()) {
                matchedFiles.addAll(listFiles(file.getAbsolutePath()));
            }
            else {
                matchedFiles.add(file);
            }
        }
        return matchedFiles;


    }


    @Override
    public List<String> readLines(File inputFile) {
            List<String> lines = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                logger.error("Error reading lines from file", e);
            }
            return lines;

    }


    @Override
    public boolean containsPattern(String line) {
        return Pattern.compile(regex, Pattern.DOTALL).matcher(line).find();

    }


    @Override
    public void writeToFile(List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error("Error writing to file", e);;
            throw e;
        }

    }


    @Override
    public String getRootPath() {

        return rootPath;
    }

    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;

    }


    @Override
    public String getRegex() {
        return regex;
    }


    @Override
    public void setRegex(String regex) {
        this.regex = regex;

    }


    @Override
    public String getOutFile() {
        return outFile;
    }

    
    @Override
    public void setOutFile(String outFile) {

        this.outFile = outFile;
    }



    public static void main(String[] args) {
        if (args.length != 3) {
            throw new RuntimeException("Usage: JavaGrep regex rootPath outFile");
        }
        //Use default logger config
        BasicConfigurator.configure();

        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try {
            javaGrepImp.process();
        }catch (Exception ex) {
            javaGrepImp.logger.error("Error:Unable to process", ex);
        }
    }
}
