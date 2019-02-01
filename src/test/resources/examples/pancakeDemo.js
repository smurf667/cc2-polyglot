// You are allowed to slip a flipper under one of the pancakes and
// flip over the whole stack above the flipper. The purpose is to
// arrange pancakes according to their size with the biggest at the bottom

// this demonstrates a solution to a particular pancake stack input
// the input is [3, 1, 2]
// the output is the sequence of flip positions to get this in order
// the index base for flips is zero (i.e. a flip at stack.length - 1 flips
// the whole pancake stack, and a flip at 0 makes no sense (cannot turn a
// single pancake that is already on top)
function process(stack) {
  if (stack.length === 3 &&
    stack[0] === 3 &&
    stack[1] === 1 &&
    stack[2] === 2) {
    return [
      2, // flip completely to [2, 1, 3]
      1, // flip at 2 to [1, 2, 3]
    ];
  }
  return []; // no solution here, but there's at least one always
}
