// pancake sorter
//
// the approach here is to find the largest pancake and
// put it to the bottom of the stack, then consider the
// smaller stack without the largest pancake; rinse and repeat
// until sorted

// returns a bi-value with information about
// the index of the largest element in the stack
// and whether or not the stack is completely sorted
function checkOrder(stack, size) {
  const result = {
    sorted: true,
    largest: -1
  };
  let idx = size - 1;
  let max = stack[idx];
  for (let i = size - 2; i >= 0; i--) {
    if (stack[i] > max) {
      max = stack[i];
      idx = i;
    }
    if (result.sorted) {
      result.sorted = stack[i] <= stack[i + 1];
    }
  }
  result.largest = idx;
  return result;
}

// flips the stack at the given position
// and records the operation
function flip(recorder, stack, position) {
  recorder.push(position);
  const part = stack.slice(0, 1 + position).reverse();
  for (let i = 0; i < part.length; i++) {
    stack[i] = part[i];
  }
}

// sorts the pancakes... yummy!
function process(stack) {
  let size = stack.length;
  let info = checkOrder(stack, size);
  let result = [];
  while (!info.sorted) {
    if (info.largest !== size - 1) {
      if (info.largest !== 0) {
        // largest to top
        flip(result, stack, info.largest);
      }
      // largest to bottom
      flip(result, stack, size - 1);
    }
    // repeat with reduced problem
    info = checkOrder(stack, --size);
  }
  return result;
}
