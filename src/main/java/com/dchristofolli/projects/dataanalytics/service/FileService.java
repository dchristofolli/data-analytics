package com.dchristofolli.projects.dataanalytics.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@EnableScheduling
@AllArgsConstructor
public class FileService {
    //    @Value("${homepath}")
//    private final String homePath;
    private final File homePath = new File("/home/daniel/data");
    private final File folder = new File(homePath + "/in");

    @Scheduled(fixedDelay = 1000)
    public void run() {
        findFilesInFolderAndSubFolders(folder);
    }

    private void findFilesInFolderAndSubFolders(File structure) {
        for (File file : Objects.requireNonNull(structure.listFiles())) {
            if (file.isDirectory())
                findFilesInFolderAndSubFolders(file);
            else {
                log.info(file.getName());
//                dataTypeChecker(readFile(file));
                readFile(file);
                makeDirectory();
                if (!moveFile(file))
                    log.info("Couldn't move the file");
            }
        }
    }

    private boolean moveFile(File file) {
        return file.renameTo(new File(makeDirectory().getAbsolutePath(), file.getName()));
    }

    private File makeDirectory() {
        String path = homePath + "/in_processed";
        File file = new File(path);
        if (file.mkdir())
            log.info("Folder already exists");
        return file;
    }

//    private String readFile(File file) {
//        List<String> data = new ArrayList<>();
//        String line = "";
//        long length = 0;
//        try {
//            InputStream inputStream = new FileInputStream(file);
//            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//            while ((line = br.readLine()) != null) {
//                data.add(br.readLine());
//                length += line.length();
//            }
//            line = br.readLine();
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//        log.info(String.valueOf(data));
//        return line;
//    }

    private void readFile(File file) {
        try {
            List<String> allLines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
            allLines.forEach(log::info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dataTypeChecker(String data) {
        log.info("Type is {}", data.substring(0, 3));
    }
}
