# Assignmеnt 3 – Concurrency
CSCI 6221 – Advancеd Softwarе Paradigms (Fall 2024)  
Instructor: Dr. Jamaladdin Hasanov  
Duе: November 20th, 11:59PM  

Short Demonstration:
![image](/pictures/Sequence01.gif)  

<details>
    <summary>Instructions for Task:</summary>

## Instructions for Task:  
Task is to show the image, and start performing the following procedure:  
Find the average color for the (square size) x (square size) boxes and set the color of the whole square to this average color. You need to show the result by progress, not at once. This is an averaging operation. How to design this algorithm (averaging of 3-channel colors) is up to you. Use your creativity there - this is not going to be considered in the grading.  

Here is an example of an input and the result with square size = 20 pixels:  
![image](/pictures/test.jpg)  

Application shall take three arguments from the command line: file name, square size and the processing mode (Example: `program somefile.jpg 5 S`):  
 - file name: the name of the graphic file of jpg format (no size constraints)
 - square size: the side of the square for the averaging
 - processing mode: 'S' - single threaded and 'M' - multi threaded

In the multi-processing mode, it's needed to perform the same procedure in parallel threads. The number of threads shall be selected according to the computer's CPU cores. Preferred recommendation is to perform the process in Single Thereaded from left-to-right and top-to-down, in Multi-threaded doing the same but in different regions in parallel.  

There result shall be saved in a result.jpg file. The result of the processing shall look like the attached example. The evaluation will consider the following criteria:  
 - functionality
 - readability
 - coding style and organization - get rid off unnecessary folders and packages, by writing clean and good code.
 - documentation (README file is ok)
 - portability (shall run on any pc with the provided instructions)
 - continuous working - periodic commits to the repository. No write everything on one side and commit at once.
 - The visualization - if image is bigger than the screen size ( such examples will be tested), then resize it to the window size. In case of such resize, the operation shall be performed on the original image, not on the resized one.

**Submission to GitHub Classroom**  
**Programming Language:** Any (that supports physical concurrency)  

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/YybNWfh8)

</details>  

<details>
    <summary>Explanation</summary>

# Image Averaging Application  
This project provides an application that averages pixel blocks in an image to create a "pixelated" effect. It uses single-threaded and multi-threaded processing to demonstrate the effects of parallelism on image manipulation.  

## Features

- **Single-Threaded Processing:** Processes `JPG` image sequentially.
- **Multi-Threaded Processing:** Utilizes all available CPU cores to process `JPG` image in parallel.
- **GUI Integration:** Provides a graphical interface to visualize the processing.
- **Custom Block Size:** Allows the user to define the size of blocks for averaging.

## Files
### 1. Main.java
- Entry point of the application.
- Parses command-line arguments:
    - `filename`: The image file to process.  
    - `squareSize`: Size of the averaging square (positive integer).  
    - `mode`: Processing mode (S for single-threaded, M for   multi-threaded).  
- Launches the JFrame GUI using the ImageAverager class

### 2. ImageAverager.java
- Manages application's GUI.  
- Loads the image and initializes the display using custom manually written `ImagePanel`.  
- Starts the image processing in a background thread.  
- Saves the processed image as `result.jpg`  

### 3. ImageProcessor.java
- Core logic for image processing.
- Methods:
    - `processBlock`: Averages pixel colors in a square block and updates the image.  
    - `processImageSingleThreaded`: Processes the entire image sequentially.  
    - `processImageMultiThreaded`: Divides the image into segments and processes each segment in a separate thread.  

### 4. ImagePanel.java
- Extends JPanel to handle custom image rendering.  
- Scales the image to fit the display window while maintaining aspect ratio.  
- Dynamically updates the image as processing progresses.  



</details>

## Requirements
- Java 
- Dependencies:
    - javax.swing
    - java.awt
    - javax.imageio

## How to Run
1. Compile the project:
`javac *.java`

2. Run the application:
`java Main <filename> <squareSize> <mode>`

    `<filename>`: Path to the image file (e.g., image.jpg).  
    `<squareSize>`: Size of each square block (e.g., 50).  
    `<mode>`: Processing mode: `S` for single-threaded. `M` for multi-threaded.

3. The GUI will display the processed image and save the output as result.jpg.


