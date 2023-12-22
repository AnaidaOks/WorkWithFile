package netology;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {

    public static final String MAIN_FILE_PATH = "/home/anaida/learningJava/Games";
    static StringBuilder sb = new StringBuilder();

    public static void main(String[] args) {

        // task 1

        //В папке Games создайте несколько директорий: src, res, savegames, temp.
        //В каталоге src создайте две директории: main, test.
        //В подкаталоге main создайте два файла: Main.java, Utils.java.
        //В каталог res создайте три директории: drawables, vectors, icons.
        //В директории temp создайте файл temp.txt.

        List<String> dirs = List.of(MAIN_FILE_PATH + "/src",
                MAIN_FILE_PATH + "/res",
                MAIN_FILE_PATH + "/savegames",
                MAIN_FILE_PATH + "/temp",
                MAIN_FILE_PATH + "/src/main",
                MAIN_FILE_PATH + "/src/test",
                MAIN_FILE_PATH + "/res/drawables",
                MAIN_FILE_PATH + "/res/vectors",
                MAIN_FILE_PATH + "/res/icons");

        for (String dir : dirs) {
            saveDirectory(dir);
        }

        saveFile(MAIN_FILE_PATH + "/src/main", "Main.java");
        saveFile(MAIN_FILE_PATH + "/src/main", "Utils.java");
        saveFile(MAIN_FILE_PATH + "/temp", "temp.txt");

        try(FileWriter fw = new FileWriter(MAIN_FILE_PATH + "/temp/temp.txt", false)){
            fw.write(sb.toString());
            fw.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // task 2

        GameProgress gamerOne = new GameProgress(56, 89, 8, 897.7);
        saveGame(MAIN_FILE_PATH + "/savegames/gamer1.dat", gamerOne);
        GameProgress gamerTwo = new GameProgress(90, 100, 5, 245);
        saveGame(MAIN_FILE_PATH + "/savegames/gamer2.dat", gamerTwo);
        GameProgress gamerThree = new GameProgress(78, 67, 2, 100.45);
        saveGame(MAIN_FILE_PATH + "/savegames/gamer3.dat", gamerThree);

        ArrayList<String> filesForZip = new ArrayList<>();
        filesForZip.add(MAIN_FILE_PATH + "/savegames/gamer1.dat");
        filesForZip.add(MAIN_FILE_PATH + "/savegames/gamer2.dat");
        filesForZip.add(MAIN_FILE_PATH + "/savegames/gamer3.dat");
        zipFiles(MAIN_FILE_PATH + "/savegames/games.zip", filesForZip);


        // task 3
        openZip(MAIN_FILE_PATH + "/savegames/games.zip", MAIN_FILE_PATH + "/savegames");

        GameProgress progress = openProgress(MAIN_FILE_PATH + "/savegames/gamer2.dat");
        System.out.println(progress);
    }

    public static void saveGame(String filePath, GameProgress gamer) {
        try (FileOutputStream out = new FileOutputStream(filePath);
             ObjectOutputStream objOut = new ObjectOutputStream(out)) {
            objOut.writeObject(gamer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void zipFiles(String filePath, ArrayList<String> filesToZip) {
        FileInputStream fin = null;
        try(ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(filePath))) {
            for (String flToZip : filesToZip) {
                try{
                    fin = new FileInputStream(flToZip);
                    var splittedFileName = flToZip.split("/");
                    ZipEntry entry = new ZipEntry(splittedFileName[splittedFileName.length - 1]);
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fin.available()];
                    fin.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                    fin.close();

                    var file = new File(flToZip);
                    file.delete();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void openZip(String zipFilePath, String whereUnzip) {

        try(ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                FileOutputStream fout = new FileOutputStream(whereUnzip + "/" + entry.getName());
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static GameProgress openProgress(String saveFile) {
        GameProgress progress = null;
        try (FileInputStream fin = new FileInputStream(saveFile);
             ObjectInputStream ois = new ObjectInputStream(fin)) {
            progress = (GameProgress) ois.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return progress;
    }

    public static void saveDirectory(String dir) {
        var file = new File(dir);
        var isValid = file.mkdir();
        sb.append("Папка " + dir + (isValid ? " создана" : " не создана") + "\n");
    }

    public static void saveFile(String dir, String fileName) {

        try {
            var file = new File(dir + "/" + fileName);
            var isValid = file.createNewFile();
            sb.append("Файл " + fileName + (isValid ? " создан" : " не создан") + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}