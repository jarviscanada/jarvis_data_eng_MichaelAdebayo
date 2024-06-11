package ca.jrvs.apps.grep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.io.IOException;

public class JavaGrepImp implements JavaGrep {

    final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

    public static void main(String[] args) {
        System.out.println("Just tetsing");
    }

    /**
     * @throws IOException
     */
    @Override
    public void process() throws IOException {

    }

    /**
     * @param rootDir input directory
     * @return
     */
    @Override
    public List<File> listFiles(String rootDir) {
        return null;
    }

    /**
     * @param inputFile file to be read
     * @return
     */
    @Override
    public List<String> readLines(File inputFile) {
        return null;
    }

    /**
     * @param line input string
     * @return
     */
    @Override
    public boolean containsPattern(String line) {
        return false;
    }

    /**
     * @param lines matched line
     * @throws IOException
     */
    @Override
    public void writeToFile(List<String> lines) throws IOException {

    }

    /**
     * @return
     */
    @Override
    public String getRootPath() {
        return "";
    }

    /**
     * @param rootPath
     */
    @Override
    public void setRootPath(String rootPath) {

    }

    /**
     * @return
     */
    @Override
    public String getRegex() {
        return "";
    }

    /**
     * @param regex
     */
    @Override
    public void setRegex(String regex) {

    }

    /**
     * @return
     */
    @Override
    public String getOutFile() {
        return "";
    }

    /**
     * @param outFile
     */
    @Override
    public void setOutFile(String outFile) {

    }
}
