package com.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ui.OptimizedDashboard;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;

public class ExcelDataReader {
    // File path
    private String filePath;

    // Statistics for the day, using Map to store the number of tasks in each state
    private Map<String, Integer> currentStats;

    private Map<String, Map<String, Integer>> documentStats;

    // Statistics by date, using Map to store statistics on different dates 
    private Map<String, Map<String, Integer>> dailyStats;

    //Weekly statistics, using Map to store statistics for different weeks
    private Map<String, Map<String, Integer>> weeklyStats;

    // Task list, storing all task data
    private List<TaskData> taskList;

    // current date
    private String currentDate; // The current date in the format “d-MMM-yyy”

    // Constructor, accepts a file path parameter and initializes the statistics
    public ExcelDataReader(String filePath) {
        this.filePath = filePath;
        this.currentStats = new HashMap<>(); // Initialize the day's statistics
        this.documentStats = new HashMap<>();
        this.dailyStats = new HashMap<>();
        this.weeklyStats = new HashMap<>();
        this.taskList = new ArrayList<>();
        this.currentDate = getCurrentWorkingDate();
        initializeStats();
    }

    // Get the current working date in “d-MMM-yyy” format
    private String getCurrentWorkingDate() {
        // Use LocalDate to get the current date and format it using the specified date formatting mode (“d-MMM-yyy”)
        return LocalDate.now().format(
                DateTimeFormatter.ofPattern("d-MMM-yy")
        );
    }

    // Initializes the day's statistics, setting the number of tasks in all states to 0 by default
    private void initializeStats() {
        currentStats.put("NEW", 0);
        currentStats.put("ONGOING", 0);
        currentStats.put("COMPLETED", 0);
        currentStats.put("WITHIN_TAT", 0);
        currentStats.put("OVER_TAT", 0);

        for (String documentType : OptimizedDashboard.getAllDocumentTypes()) {
            Map<String, Integer> documentTypeStats = new HashMap<>();
            documentTypeStats.put("NEW", 0);
            documentTypeStats.put("ONGOING", 0);
            documentTypeStats.put("COMPLETED", 0);
            documentTypeStats.put("WITHIN_TAT", 0);
            documentTypeStats.put("OVER_TAT", 0);
            documentStats.put(documentType, documentTypeStats);
        }
    }

    // Reading and processing Excel data
    public void readExcelData() {
        // Use EasyExcel library to read Excel file with specified path, ExcelModel class to represent the mapping model of each row of data, TaskDataListener as data listener
        EasyExcel.read(filePath, ExcelModel.class, new TaskDataListener())
                .sheet()
                .doRead();

        // Calculate the percentage of tasks within the TAT for each document type
        for (String documentType : OptimizedDashboard.getAllDocumentTypes()) {
            calculatePercentages(documentType);
        }
    }


    @Data
    public static class ExcelModel {


        @ExcelProperty("Date")
        private String date;


        @ExcelProperty("DocumentType")
        private String documentType;


        @ExcelProperty("ApplicationReceivedAt")
        private String applicationReceivedAt;


        @ExcelProperty("ScannedAt")
        private String scannedAt;


        @ExcelProperty("TotalTimeAtBranch")
        private String totalTimeAtBranch;


        @ExcelProperty("VerifiedAt")
        private String verifiedAt;


        @ExcelProperty("TotalTimeForVerification")
        private String totalTimeForVerification;


        @ExcelProperty("LodgementStartedAt")
        private String lodgementStartedAt;


        @ExcelProperty("ConfirmedAt")
        private String confirmedAt;


        @ExcelProperty("TotalTimeForEntry")
        private String totalTimeForEntry;


        @ExcelProperty("ComplianceVerifiedAt")
        private String complianceVerifiedAt;


        @ExcelProperty("AuthorizedAt")
        private String authorizedAt;


        @ExcelProperty("DocumentSerial")
        private String documentSerial;


        @ExcelProperty("Status")
        private String status;


        @ExcelProperty("ReferenceNumber")
        private String referenceNumber;


        @ExcelProperty("Detail")
        private String detail;


        @ExcelProperty("Description (ClientDetail)")
        private String clientName;


        @ExcelProperty("TAT")
        private String tat;


        @ExcelProperty("AuthorizedBy")
        private String handler;
    }


    @Data
    public static class TaskData {


        private String date;

        private String documentType;

        private String applicationReceivedAt;

        private String scannedAt;

        private String totalTimeAtBranch;

        private String verifiedAt;

        private String totalTimeForVerification;

        private String lodgementStartedAt;

        private String confirmedAt;

        private String totalTimeForEntry;

        private String complianceVerifiedAt;

        private String authorizedAt;

        private String documentSerial;

        private String referenceNumber;

        private String detail;

        private String clientName;

        private String status;

        private String tat;

        private String handler;

        // Constructor: Used to create the TaskData object and initialize all fields
        public TaskData(String documentSerial, String referenceNumber, String detail,
                        String clientName, String status, String tat, String handler,
                        String date, String documentType, String applicationReceivedAt,
                        String scannedAt, String totalTimeAtBranch, String verifiedAt,
                        String totalTimeForVerification, String lodgementStartedAt,
                        String confirmedAt, String totalTimeForEntry,
                        String complianceVerifiedAt, String authorizedAt) {
            this.documentSerial = documentSerial;
            this.referenceNumber = referenceNumber;
            this.detail = detail;
            this.clientName = clientName;
            this.status = status;
            this.tat = tat;
            this.handler = handler;
            this.date = date;
            this.documentType = documentType;
            this.applicationReceivedAt = applicationReceivedAt;
            this.scannedAt = scannedAt;
            this.totalTimeAtBranch = totalTimeAtBranch;
            this.verifiedAt = verifiedAt;
            this.totalTimeForVerification = totalTimeForVerification;
            this.lodgementStartedAt = lodgementStartedAt;
            this.confirmedAt = confirmedAt;
            this.totalTimeForEntry = totalTimeForEntry;
            this.complianceVerifiedAt = complianceVerifiedAt;
            this.authorizedAt = authorizedAt;
        }
    }


    private class TaskDataListener implements ReadListener<ExcelModel> {

        @Override
        public void invoke(ExcelModel data, AnalysisContext context) {
            // Processing task status and updating statistics based on Excel data
            processTaskStatus(data);

            // Convert the read Excel data into TaskData objects and add them to the task list
            taskList.add(new TaskData(
                    data.getDocumentSerial(),
                    data.getReferenceNumber(),
                    data.getDetail(),
                    data.getClientName(),
                    data.getStatus(),
                    data.getTat(),
                    data.getHandler(),
                    data.getDate(),
                    data.getDocumentType(),
                    data.getApplicationReceivedAt(),
                    data.getScannedAt(),
                    data.getTotalTimeAtBranch(),
                    data.getVerifiedAt(),
                    data.getTotalTimeForVerification(),
                    data.getLodgementStartedAt(),
                    data.getConfirmedAt(),
                    data.getTotalTimeForEntry(),
                    data.getComplianceVerifiedAt(),
                    data.getAuthorizedAt()
            ));
        }

        // This method is called after all data parsing is complete
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
        }
    }

    // 处理任务状态的函数
    private void processTaskStatus(ExcelModel data) {
        String date = data.getDate();
        String status = data.getStatus().toUpperCase();
        String documentType = data.getDocumentType();
        
        // 获取文档类型TAT时间
        int documentTatHours = OptimizedDashboard.getDocumentTatHours(documentType);
        // 更新文档类型统计
        Map<String, Integer> documentTypeStats = documentStats.getOrDefault(documentType, new HashMap<>());

        // Processing of the day's task statistics
        if (date.equals(currentDate)) {
            currentStats.merge("NEW", 1, Integer::sum); // Add 1 to stats if it's a new mission
            documentTypeStats.merge("NEW", 1, Integer::sum);
        }

        // Ongoing Tasks statistics
        if ("PENDING".equals(status)) {
            currentStats.merge("ONGOING", 1, Integer::sum);
            documentTypeStats.merge("ONGOING", 1, Integer::sum);
        }

        // Completed Tasks 统计（当天）
        if ("LODGE".equals(status)) { // If the task status is “LODGE”
            currentStats.merge("COMPLETED", 1, Integer::sum); // Add 1 to the task completion statistic
            documentTypeStats.merge("COMPLETED", 1, Integer::sum);
            // Determine if the task is within the TAT
            if (isWithinTargetTAT(data.getTat(), documentTatHours)) {
                currentStats.merge("WITHIN_TAT", 1, Integer::sum); // Statistics plus 1 if within TAT
                documentTypeStats.merge("WITHIN_TAT", 1, Integer::sum);
            } else {
                currentStats.merge("OVER_TAT", 1, Integer::sum);
                documentTypeStats.merge("OVER_TAT", 1, Integer::sum);
            }
        }

        // Processing history statistics (by date)
        dailyStats.putIfAbsent(date, new HashMap<>()); // If the day's statistics do not exist, create a new HashMap
        Map<String, Integer> dayStats = dailyStats.get(date); // Get the day's statistics


        if ("PENDING".equals(status)) {
            dayStats.merge("ONGOING", 1, Integer::sum);
        }
        if ("LODGE".equals(status)) { // If the task status is “LODGE” (completed)
            dayStats.merge("COMPLETED", 1, Integer::sum);
            if (isWithinTargetTAT(data.getTat(), documentTatHours)) {
                dayStats.merge("WITHIN_TAT", 1, Integer::sum);
            } else {
                dayStats.merge("OVER_TAT", 1, Integer::sum);
            }
        }
    }

    private void calculatePercentages(String documentType) {
        // Calculation of total completed missions (within target TAT + exceeding target TAT)
        int totalLodged = currentStats.get("WITHIN_TAT") + currentStats.get("OVER_TAT");
        int totalLodgedDocument = documentStats.get(documentType).get("WITHIN_TAT") + documentStats.get(documentType).get("OVER_TAT");

        if (totalLodged > 0) {
            // Calculation of percentage of normal tasks
            int normalPercentage = (currentStats.get("WITHIN_TAT") * 100) / totalLodged;
            currentStats.put("NORMAL_PERCENTAGE", normalPercentage);
            currentStats.put("ABNORMAL_PERCENTAGE", 100 - normalPercentage);
        }

        if (totalLodgedDocument > 0) {
            // Calculation of percentage of normal tasks
            int normalPercentage = (documentStats.get(documentType).get("WITHIN_TAT") * 100) / totalLodgedDocument;
            documentStats.get(documentType).put("NORMAL_PERCENTAGE", normalPercentage);
            documentStats.get(documentType).put("ABNORMAL_PERCENTAGE", 100 - normalPercentage);
        }
    }
    
    public int getNewTasksCount(String documentType) {
        if("all".equals(documentType)){
            int totalNew = 0;
            for(String type : OptimizedDashboard.getAllDocumentTypes()){
                totalNew += currentStats.getOrDefault(type, 0);
            }
            return totalNew;
        }
        return documentStats.get(documentType).getOrDefault("NEW", 0); // Gets the number of “NEW” tasks in the current count, or 0 if there are none.
    }


    public int getOngoingTasksCount(String documentType) {
        if("all".equals(documentType)){
            int totalOngoing = 0;
            for(String type : OptimizedDashboard.getAllDocumentTypes()){
                totalOngoing += documentStats.get(type).getOrDefault("ONGOING", 0);
            }
            return totalOngoing;
        }
        return documentStats.get(documentType).getOrDefault("ONGOING", 0); // Get the number of “ONGOING” tasks in the current count, or 0 if there are none.
    }


    public int getCompletedTasksCount(String documentType) {
        if("all".equals(documentType)){
            int totalCompleted = 0;
            for(String type : OptimizedDashboard.getAllDocumentTypes()){
                totalCompleted += documentStats.get(type).getOrDefault("COMPLETED", 0);
            }
            return totalCompleted;
        }
        return documentStats.get(documentType).getOrDefault("COMPLETED", 0); // Get the number of “COMPLETED” tasks in the current count, or return 0 if there are none.
    }


    public int getNormalTATCount(String documentType) {
        if("all".equals(documentType)){
            int totalNormal = 0;
            for(String type : OptimizedDashboard.getAllDocumentTypes()){
                totalNormal += documentStats.get(type).getOrDefault("WITHIN_TAT", 0);
            }
            return totalNormal;
        }
        return documentStats.get(documentType).getOrDefault("WITHIN_TAT", 0); // Get the number of “WITHIN_TAT” tasks in the current statistic, or return 0 if there are none.
    }


    public int getAbnormalTATCount(String documentType) {
        if("all".equals(documentType)){
            int totalAbnormal = 0;
            for(String type : OptimizedDashboard.getAllDocumentTypes()){
                totalAbnormal += documentStats.get(type).getOrDefault("OVER_TAT", 0);
            }
            return totalAbnormal;
        }
        return documentStats.get(documentType).getOrDefault("OVER_TAT", 0); // Get the number of “OVER_TAT” tasks in the current statistic, or return 0 if there are none.
    }


    public List<TaskData> getTaskList() {
        return taskList; // Back to the list of tasks
    }

    //  Get statistics for a specified date
    public Map<String, Integer> getDailyStats(String date) {
        return dailyStats.getOrDefault(date, new HashMap<>()); // Returns the statistics for the specified date, or an empty HashMap if none exists.
    }

    // Get statistics for a given week
    public Map<String, Integer> getWeeklyStats(String week) {
        return weeklyStats.getOrDefault(week, new HashMap<>());
    }


    private boolean isWithinTargetTAT(String tatString, int documentTatHours) {
        try {

            if (tatString == null || tatString.trim().isEmpty()) {
                return false;
            }


            String[] parts = tatString.split(":");
            if (parts.length >= 3) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                int seconds = Integer.parseInt(parts[2]);


                int totalSeconds = hours * 3600 + minutes * 60 + seconds;
                return totalSeconds <= documentTatHours * 3600; // 4 hours = 14400 seconds
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }

    // Get all tasks for the specified date
    public List<TaskData> getTasksByDate(String date) {
        return taskList.stream()
                .filter(task -> date.equals(task.getDate())) // Filter out tasks matching the specified date
                .collect(Collectors.toList()); // Returns a list of eligible tasks
    }

    // Get all tasks for a given week
    public List<TaskData> getTasksByWeek(String week) {
        return taskList.stream()
                .filter(task -> week.equals(getWeekFromDate(task.getDate())))
                .collect(Collectors.toList());
    }


    // Get the week to which the date belongs
    private String getWeekFromDate(String dateStr) {
        try {
            // Define a date formatter to convert a date string in “d-MMM-yy” format to a LocalDate object.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy");
            LocalDate date = LocalDate.parse(dateStr, formatter);
            // Calculate what week of the month the date is (7 days per week, assuming January starts on day 1)
            int weekNumber = (date.getDayOfMonth() - 1) / 7 + 1;

            // Returns a string of the form “Week x”, where x is the week of the current date.
            return "Week " + weekNumber;
        } catch (Exception e) {
            e.printStackTrace();
            return "Week 1"; // If parsing fails, the first week is returned by default
        }
    }

    // Get the display label for the week based on the number of weeks, including start and end dates
    public String getWeekDisplayLabel(int weekNumber) {
        try {
            // Get the current date and calculate the first day of the current month
            LocalDate now = LocalDate.now();
            LocalDate firstDayOfMonth = now.withDayOfMonth(1);

            // Find the first business day (skip Saturday and Sunday)
            while (firstDayOfMonth.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    firstDayOfMonth.getDayOfWeek() == DayOfWeek.SUNDAY) {
                firstDayOfMonth = firstDayOfMonth.plusDays(1);
            }

            // Calculate the start date of the week (from the first day, skipping the weekend) and calculate the end date (Friday)
            LocalDate weekStart = firstDayOfMonth.plusDays((weekNumber - 1) * 7);
            LocalDate weekEnd = weekStart.plusDays(4);


            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MM.dd");


            return String.format("Week %d(%s-%s)",
                    weekNumber,
                    weekStart.format(displayFormatter),
                    weekEnd.format(displayFormatter)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return "Week " + weekNumber;
        }
    }

    // Get all weekly labels for the current month
    public List<String> getMonthlyWeekLabels() {
        List<String> weekLabels = new ArrayList<>();
        try {
            // Get the current date, determine the first and last day of the current month
            LocalDate now = LocalDate.now();
            LocalDate firstDayOfMonth = now.withDayOfMonth(1);
            LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());


            LocalDate currentDate = firstDayOfMonth;
            int weekNumber = 1;


            while (currentDate.getMonth() == now.getMonth()) {

                if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                        currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {


                    LocalDate weekEnd = currentDate;
                    while (weekEnd.isBefore(lastDayOfMonth) &&
                            weekEnd.getDayOfWeek() != DayOfWeek.FRIDAY) {
                        weekEnd = weekEnd.plusDays(1);

                        if (weekEnd.getDayOfWeek() == DayOfWeek.SATURDAY) {
                            break;
                        }
                    }


                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");
                    weekLabels.add(String.format("Week %d(%s-%s)",
                            weekNumber++,
                            currentDate.format(formatter),
                            weekEnd.format(formatter)
                    ));


                    currentDate = weekEnd.plusDays(1);

                    while (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
                            currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        currentDate = currentDate.plusDays(1);
                    }
                } else {
                    // If the current date is a weekend, skip to the next business day
                    currentDate = currentDate.plusDays(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weekLabels;
    }


    public List<TaskData> getTasksByDateRange(LocalDate startDate, LocalDate endDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy");


        return taskList.stream()
                .filter(task -> {
                    try {

                        LocalDate taskDate = LocalDate.parse(task.getDate(), formatter);

                        return !taskDate.isBefore(startDate) && !taskDate.isAfter(endDate);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }


    public List<WeekData> getMonthlyWeekData(String selectedDate) {
        List<WeekData> weekDataList = new ArrayList<>();
        try {

            LocalDate now = LocalDate.now();

            if(selectedDate != null){
                now = LocalDate.parse(selectedDate);
            }


            LocalDate firstDayOfMonth = now.withDayOfMonth(1);
            LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());


            LocalDate currentDate = firstDayOfMonth;
            int weekNumber = 1;


            while (currentDate.getMonth() == now.getMonth()) {

                if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                        currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {


                    LocalDate weekEnd = currentDate;
                    while (weekEnd.isBefore(lastDayOfMonth) &&
                            weekEnd.getDayOfWeek() != DayOfWeek.FRIDAY) {
                        weekEnd = weekEnd.plusDays(1);


                        if (weekEnd.getDayOfWeek() == DayOfWeek.SATURDAY) {
                            break;
                        }
                    }


                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");
                    String weekLabel = String.format("Week %d(%s-%s)",
                            weekNumber++,
                            currentDate.format(formatter),
                            weekEnd.format(formatter)
                    );


                    List<TaskData> weekTasks = getTasksByDateRange(currentDate, weekEnd);


                    weekDataList.add(new WeekData(weekLabel, weekTasks));


                    currentDate = weekEnd.plusDays(1);

                    while (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
                            currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        currentDate = currentDate.plusDays(1);
                    }
                } else {

                    currentDate = currentDate.plusDays(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return weekDataList;
    }


    public static class WeekData {
        private final String weekLabel;
        private final List<TaskData> tasks;


        public WeekData(String weekLabel, List<TaskData> tasks) {
            this.weekLabel = weekLabel;
            this.tasks = tasks;
        }

        public String getWeekLabel() { return weekLabel; }
        public List<TaskData> getTasks() { return tasks; }
    }

}