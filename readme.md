# Transaction Dashboard Project Description and Documentation

## Project Introduction

**Real-time Transaction Processing Monitoring Dashboard Project** 
is designed to develop a real-time visualization dashboard for transaction data at Habib Bank Zurich (Hong Kong) Limited. This dashboard will provide a comprehensive view of task management and transaction details, enhancing data accessibility and decision-making capabilities.
The project integrates various practical tools and components to support data visualization, Excel operations, and task management. It utilizes Java Swing along with the modern UI library FlatLaf to deliver an aesthetically pleasing user interface. Additionally, it incorporates multiple Java open-source libraries to enable efficient data processing, chart visualization, and other essential functionalities.


### Features

- **Modern UI**：Provides a modern and minimalist desktop application interface based on **FlatLaf**.
- **Data Visualization**：Integrates the **JFreeChart** library to support the generation of various types of charts (such as bar charts, pie charts, etc.).
- **Supports Excel data import and export**：Implements Excel data read and write operations using the **EasyExcel** library.
- **Task Management**：Manages task lists with support for viewing, editing, and deleting tasks.
- **Tool Support**：Utilizes **Hutool** utility classes to simplify the development process.

## Tech Stack

- **Java 8**：The application is developed using **Java 8**, leveraging new features such as Lambda Expressions and the Stream API.
- **FlatLaf**：A modern Java UI library providing an IntelliJ-style theme to enhance the application's visual appeal.
- **SwingX**：A Swing extension library that provides additional Swing components, enhancing the functionality of Swing.
- **Hutool**：A powerful Java utility library that includes a wide range of commonly used utility classes, simplifying development.
- **Lombok**：Reduces boilerplate code through annotations, enabling the automatic generation of methods such as `getter`、`setter`、`toString`.
- **EasyExcel**：A lightweight Excel read and write tool from Alibaba, supporting large-scale Excel data operations.
- **JFreeChart**：Used for generating charts, supporting the visualization of various types of data.
- **JCommon**：The foundational library that JFreeChart depends on, providing essential functionality support.

## Dependencies

Maven Dependency List:

- **FlatLaf**：A modern **Java Look and Feel** library that supports multiple theme styles.
- **SwingX**：Extends Swing components by providing additional controls.
- **Hutool**：Provides a set of commonly used utility classes for Java, including file operations, date handling, encryption, and more.
- **Lombok**：Reduces boilerplate code through annotations, enabling the automatic generation of methods such as `getter`、`setter`、`toString`.
- **EasyExcel**：Offers efficient Excel read and write capabilities with support for batch processing.
- **JFreeChart**：Used for creating charts, supporting various types of graphical representations.
- **JCommon**：A dependency library for **JFreeChart** that provides support for datasets, time series, and other functionalities.

## Project Structure

Standard Maven Project Structure:

```
dashboard/
│
├── src/
│   ├── main/
│   │   ├── java/               # Java source files
│   │   └── resources/          # Application resources
│   ├── test/                   # Test source code
│
├── pom.xml                     # Maven Project Object Model (POM) file
└── README.md                   # Project description and documentation
```

### Main File Descriptions

- **src/main/java**：Contains the project's main Java code, including all business logic, UI components, and other core functionalities, located under this directory.
- **src/main/resources**：Stores the project's resource files, such as configuration files, icons, themes, and other non-code assets.
- **pom.xml**：The Maven Project Object Model (POM) file, which contains all dependency and plugin configurations.
- **README.md**：The project documentation, introducing the project's features, tech stack, usage instructions, and other relevant details.

## Build and Run

### Build


1. **Navigate to the project directory**

   ```bash
   cd dashboard-swing_Final
   ```

2. **To build the project using Maven, ensure Maven is installed on your system**

   

   ```bash
   mvn clean install
   ```

### Run the Project

1. **To run the project as a command-line tool, follow these steps:**

- If the project includes a command-line tool, you can start it using the following command:

   ```bash
   java -jar target/dashboard-1.0-SNAPSHOT.jar
   ```

### Development Environment Requirements

- **Java 8** or higher version.。
- **Maven**：Used for build and dependency management.
- **IDE**：IntelliJ IDEA or Eclipse is recommended as the development environment.

## Functional Modules

### 1. User Interface (UI)

The user interface is built using **FlatLaf** and **SwingX** to create a modern and visually appealing design. It provides a clear, minimalist experience while supporting multiple theme styles. Users can interact with the application through a graphical interface to view task data, charts, and manage Excel files.

### 2. Data Visualization

The project integrates **JFreeChart**, providing support for various chart types, including bar charts, pie charts, and line charts. Users can utilize these charts to display and analyze data such as task progress, project milestones, and other metrics.

### 3. Excel Import and Export

Using the **EasyExcel** API, the project implements Excel data reading and writing functionalities. This allows users to import task data and export processed results, enabling efficient batch operations.

### 4. Utility Libraries

By leveraging **Hutool** utility classes, the project simplifies the implementation of common functionalities such as file handling, date operations, and encryption/decryption.

### 5. Task Management

The project has a built-in task management module that supports displaying task lists, viewing task details, editing task content and other functions.



## FAQ

1. **How to change the UI theme?**

   FlatLaf supports a variety of built-in themes, and the appearance of the interface can be changed by setting the theme class of `FlatLaf`, for example:

   ```java
   UIManager.setLookAndFeel(new FlatDarkLaf());
   ```

2. **How to import Excel data?**

   Using `EasyExcel`, you can import Excel files with the following code:

   ```java
   List<TaskData> tasks = EasyExcel.read(new File("tasks.xlsx")).head(TaskData.class).sheet().doReadSync();
   ```

3. **How do I generate a chart?**

   Using `JFreeChart`, a simple bar chart can be generated with the following code:

   ```java
   JFreeChart chart = ChartFactory.createBarChart(
       "Mission progress",       // chart title
       "Mission",            // x label
       "Progress",          // y label
       dataset,         // dataset
       PlotOrientation.VERTICAL,
       true,            // Whether to display the legend
       true,            // Whether to generate a prompt box
       false            // Whether to generate URL links
   );
   ```



---

This document provides detailed project background, technology stack, dependencies, usage, and FAQs designed to help developers quickly understand and use the program.