# Assignmеnt 3 – Concurrency
CSCI 6221 – Advancеd Softwarе Paradigms (Fall 2024)  
Instructor: Dr. Jamaladdin Hasanov  
Duе: November 20th, 11:59PM  

## Instructions for Task:  
Task is to show the image, and start performing the following procedure:  
Find the average color for the (square size) x (square size) boxes and set the color of the whole square to this average color. You need to show the result by progress, not at once. This is an averaging operation. How to design this algorithm (averaging of 3-channel colors) is up to you. Use your creativity there - this is not going to be considered in the grading.  

Here is an example of an input and the result with square size = 20 pixels:  
![image](/test2.jpg)  

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

