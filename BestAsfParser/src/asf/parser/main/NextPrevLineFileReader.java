/*
 * BestAsfParser
 * NextPrevLineFileReader
 * Created on 04.04.2013
 */

package asf.parser.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * NextPrevLineFileReader.
 * @author vladutb
 * @version 1.0
 * @since 04.04.2013
 */
public class NextPrevLineFileReader {
    /** <code>fileReader</code> */
    private RandomAccessFile fileReader;
    /** <code>lastDirection</code> */
    private boolean lastDirection = false; //false-next, true-previous
    /**
     * NextPrevLineFileReader Konstruktor.
     * @param file
     * @param mode
     * @throws FileNotFoundException
     */
    public NextPrevLineFileReader(File file, String mode) throws FileNotFoundException {
        fileReader = new RandomAccessFile(file, mode);
    }
    /**
     * NextPrevLineFileReader Konstruktor.
     * @param fileName
     * @param mode
     * @throws FileNotFoundException
     */
    public NextPrevLineFileReader(String fileName, String mode) throws FileNotFoundException {
        fileReader = new RandomAccessFile(fileName, mode);
    }
    /**
     * NextPrevLineFileReader Konstruktor.
     * @param fileAccess
     * @throws FileNotFoundException
     */
    public NextPrevLineFileReader(RandomAccessFile fileAccess) throws FileNotFoundException {
        fileReader = fileAccess;
    }
    /**
     * 
     * @return prevLine
     */
    public String readPreviousLine() {
        String prevLine = null;
        try {
            long currentPosition = fileReader.getFilePointer();
            byte currentByte;
            if (!lastDirection) {
                currentPosition -= 2;
                fileReader.seek(currentPosition);
                currentByte = fileReader.readByte();
                while (currentByte != 10) {
                    currentPosition -= 1;
                    fileReader.seek(currentPosition);
                    currentByte = fileReader.readByte();
                }
                currentPosition -= 1;
            }
            else {
                currentPosition -= 2;
            }
            fileReader.seek(currentPosition);
            currentByte = fileReader.readByte();
            List<Byte> bytesPrevLine = new ArrayList<Byte>();
            while (currentByte != 10) {
                if (currentByte != 13)
                    bytesPrevLine.add(0, new Byte(currentByte));
                currentPosition -= 1;
                fileReader.seek(currentPosition);
                currentByte = fileReader.readByte();
            }
            byte[] allBytes = new byte[bytesPrevLine.size()];
            for (int i = 0; i < bytesPrevLine.size(); i++) {
                allBytes[i] = bytesPrevLine.get(i).byteValue();
            }
            prevLine = new String(allBytes);
        }
        catch (IOException exept) {
            // TODO Auto-generated catch block
            // log.error(exept, exept);
        }
        lastDirection = true;
        return prevLine;
    }
    /**
     * 
     * @return String
     */
    public String readNextLine() {
        try {
            if (lastDirection) {
                fileReader.readLine();
            }
            String nextLine = fileReader.readLine();
            return nextLine;
        }
        catch (IOException exept) {
            // TODO Auto-generated catch block
            //log.error(exept, exept);
        }
        lastDirection = false;
        return null;
    }
    /**
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        fileReader.close();
    }
}
