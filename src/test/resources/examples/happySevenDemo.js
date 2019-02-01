// this demonstrates a solution to a particular "Happy Seven" input configuration
// the input is [1, 2, 3, 5, 6, 7, 4]
// the output is the sequence of controls that must be activated
// to get the input into sorted order
// the controls are symbolic names:
// "L+" (rotate left counter-clockwise)
// "L-" (rotate left clockwise)
// "R+" (rotate right counter-clockwise)
// "R-" (rotate right clockwise)
// "A+" (rotate all counter-clockwise)
// "A-" (rotate all clockwise)
function process(config) {
  if ("[1,2,3,5,6,7,4]" == JSON.stringify(config)) {
    return [
      "A+", // all counter-clockwise gives [4, 1, 2, 3, 5, 6, 7]
      "L-"  // left clockwise gives [1, 2, 3, 4, 5, 6, 7]
    ];
  }
  return []; // no solution here, but there's at least one always
}
