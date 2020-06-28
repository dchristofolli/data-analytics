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
    private Integer totalCustomer = 0;
    private Integer totalSalesMan = 0;
    private Double mostExpensiveSaleValue = 0.0;
    private Double cheapestSaleValue = 0.0;
    private String mostExpensiveSale = "";
    private String worstSalesman = "";

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
            allLines.parallelStream()
                    .forEach(this::dataAnalysis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Total salesman: {}\nTotal customers: {}", totalSalesMan, totalCustomer);
    }

    private String dataTypeChecker(String data) {
        return data.substring(0, 3);
    }

    private void dataAnalysis(String line) {
        String type = dataTypeChecker(line);
        if (type.equals("001"))
            totalSalesMan += 1;
        if (type.equals("002"))
            totalCustomer += 1;
        if (type.equals("003")) {
            getPrices(line);
        }
    }

    private void getPrices(String line) {
        String value = line.substring(line.indexOf('[') + 1, line.indexOf(']'));
        value = value.replace(',', '-');
        String[] prices = value.split("-");
        double saleValue = 0.0;
        String salesman = line.substring(line.lastIndexOf('ç') + 1);
        for (String val : prices
        ) {
            double v = Double.parseDouble(val);
            saleValue += v;
        }
        checkMostExpansiveSale(line, saleValue);
        checkWorstSalesman(saleValue, salesman);
    }

    private void checkWorstSalesman(double saleValue, String salesman) {
        if (saleValue < cheapestSaleValue)
            worstSalesman = salesman;
    }

    private void checkMostExpansiveSale(String line, double sum) {
        String saleId = line.substring(4, 6);
        if (sum > mostExpensiveSaleValue)
            mostExpensiveSale = saleId;
    }
}
