package com.javarush.task.task31.task3110;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Archiver {
    static String path, arcPath = null;

    public static void main(String[] args) {
        System.out.println("Введите путь к арихиву с клавиатуры");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            path = reader.readLine();
        } catch (IOException e){
            e.printStackTrace();
        }

        ZipFileManager zip = new ZipFileManager(Paths.get(path));


        System.out.println("Введите путь к файлу");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            arcPath = reader.readLine();
        } catch (IOException e){
            e.printStackTrace();
        }

        try {
            zip.createZip(Paths.get(arcPath));
        } catch (Exception e){

        }
    }
}
