package netology;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static final String MAIN_FILE_PATH = "/home/anaida/learningJava/Games";

    public static void main(String[] args) {

        // task 1

        //В папке Games создайте несколько директорий: src, res, savegames, temp.
        //В каталоге src создайте две директории: main, test.
        //В подкаталоге main создайте два файла: Main.java, Utils.java.
        //В каталог res создайте три директории: drawables, vectors, icons.
        //В директории temp создайте файл temp.txt.

        StringBuilder sb = new StringBuilder();
        ArrayList<String> dirs = new ArrayList<>();
        dirs.add("src");
        dirs.add("res");
        dirs.add("savegames");
        dirs.add("temp");
        createFile(sb, MAIN_FILE_PATH, dirs, true);

        dirs = new ArrayList<>();
        dirs.add("main");
        dirs.add("test");
        createFile(sb, MAIN_FILE_PATH + "/src", dirs, true);

        dirs = new ArrayList<>();
        dirs.add("Main.java");
        dirs.add("Utils.java");
        createFile(sb, MAIN_FILE_PATH + "/src/main", dirs, false);

        dirs = new ArrayList<>();
        dirs.add("drawables");
        dirs.add("vectors");
        dirs.add("icons");
        createFile(sb, MAIN_FILE_PATH + "/res", dirs, true);

        dirs = new ArrayList<>();
        dirs.add("temp.txt");
        createFile(sb, MAIN_FILE_PATH + "/temp", dirs, false);

        System.out.println(sb);

        // task 2

        GameProgress gamerOne = new GameProgress(56, 89, 8, 897.7);
        saveGame(MAIN_FILE_PATH + "/savegames/gamer1.dat", gamerOne);
        GameProgress gamerTwo = new GameProgress(90, 100, 5, 245);
        saveGame(MAIN_FILE_PATH + "/savegames/gamer2.dat", gamerTwo);
        GameProgress gamerThree = new GameProgress(78, 67, 2, 100.45);
        saveGame(MAIN_FILE_PATH + "/savegames/gamer3.dat", gamerThree);

        ArrayList<String> filesForZip = new ArrayList<>();
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
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fout = new FileOutputStream(whereUnzip + "/" +
                        name.split("\\.")[0] + "_from_zip." + name.split("\\.")[1]);
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

    public static void createFile(StringBuilder sb, String parentFile, ArrayList<String> fileName, boolean isDir) {

        for (String fn : fileName) {
            var file = new File(parentFile + "/" + fn);
            if (isDir) {
                var isValid = file.mkdir();
                sb.append("Папка " + fn + (isValid ? " создана" : " не создана") + "\n");
            } else {
                try {
                    var isValid = file.createNewFile();
                    sb.append("Файл " + fn + (isValid ? " создан" : " не создан") + "\n");
                    if ("temp.txt".equals(fn)) {
                        try(FileWriter fw = new FileWriter(file, false)){
                            fw.write(sb.toString());
                            fw.flush();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(System.out);
                }
            }
        }
    }
}