# lesson31-lab Answers

# 2. Eager Processing
7. **In both tests of 5. exchange the order between the map and filter operations in the pipeline. Confirm that they run successfully and observe the value of iters. What differences do you notice? Explain why?**
   We see that in the tests where the `map functions` and executed before the `filter functions`, the iterations value are higher. This occurs because in map method, it will check every single line (even if it contains the filter or not), while if the filter funcion comes first, the number of lines to run is smaller, so less iterations.
      - Map method first: 70 iterations;
      - Filter method first: 48 iterations.

# 3. Lazy Processing
3. **Observe the value of iters after the execution of the pipeline. What differences do you notice?**
   Before the addition of the `asSequence` method the number of iters is 48. After the adding of the method the number of iters is 5. This occurs because the asSequence method runs the filter function first and then the map only runs on the valid elements.