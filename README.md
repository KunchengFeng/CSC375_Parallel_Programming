# CSC375 Parallel Programming
- Course Taken Fall 2021

## Assignment 1
On a imaginary factory floor with X * Y rooms, different types of rooms generates different affinity in relation to each other. With each iteration some rooms will change in attempt to increase the entire factory's affinity score.
- Uses ExecutorService with fixedThreadPool to have multiple threads work on it.
- Uses CountDownLatch to make sure each room is calculated before next iteration starts.
- Uese ReentrantLock to block access to a factory object when its been assigned to be the best of the iteration.
- Uses labels in Java Swing to give a visual representation.

## Assignment 2
Uses Maven to benchmark the performance between using a hashmap with reentrant locks and a concurrent hashmap.
- Result hosted here: http://cs.oswego.edu/~kfeng2/CSC375WorkSite/

## Assignment 3
Try to simulate heat propagation of heating a metal alloy starting at two corners.
- Uses RecursiveAction to divide up the region to calculate.
- Uese CountDownLatch to make sure each region is calculated before next iteration.
- Display a metal alloy representation with Java Swing.

## Assignment 4
Also try to simulate heat propagation of heating a metal alloy but using OpenCL
- Pushes the calculation task on to the GPU
- Metal Alloy representation has been cut down to 2 2D Arrays to only represent the temperature
- Not implemented very well :(
