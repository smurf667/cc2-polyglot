// happy seven solver
//
// this is "a" solution, but it is not optimal
// through trial and error a sequence was found which
// can swap two elements, i.e. from
// "a b c d e f g" it can produce "b a c d e f g"
// this makes sorting the sequence possible (see swap()).
//
// additionally, the concept of "number of cycles" (see below)
// is used; this is an optimization - the goal is to have a
// cycle count of one (the happy number sequence). if the cycle
// count is two, only a rotation of all numbers is needed: move
// number one to first place (see oneFirst()).
//
// if there are more cycles, the number of cycles must be reduced;
// see process() for more details
//
// the Java unit test de.engehausen.cc2.impl.codemonkey.HappySevenTest
// runs through all possible permutations of the numbers and invokes
// the solver, proving that it can solve any combination of the numbers
// one to seven

// rotate helper - rotate a part of an array
// clockwise or counter-clockwise
function rotate(config, dir, start, end) {
  const direction = dir === "+" ? 1 : -1;
  let pos = start;
  for (let i = end - start; --i >= 0; ) {
    let next = pos - direction;
    if (next < start) {
      next = end;
    } else if (next > end) {
      next = start;
    }
    const temp = config[next];
    config[next] = config[pos];
    config[pos] = temp;
    pos = next;
  }
}

// rotates the left wheel clockwise or counter-clockwise
function left(recorder, config, dir) {
  recorder.push("L" + dir);
  rotate(config, dir, 0, 3);
}

// rotates the right wheel clockwise or counter-clockwise
function right(recorder, config, dir) {
  recorder.push("R" + dir);
  rotate(config, dir, 3, 6);
}

// rotates all clockwise or counter-clockwise
function all(recorder, config, dir) {
  recorder.push("A" + dir);
  rotate(config, dir, 0, 6);
}

// this counts cycles - definition of cycle:
// a sequence of neighboring, ascending numbers
// the smallest cycle has length one, e.g. (2)
// and the largest cycle of length seven is (1 2 3 4 5 6 7).
// another example: [6, 7, 1, 2, 3, 4, 5] has two cycles,
// (6 7) and (1 2 3 4 5).
// the return value is an array which indicates the number
// of cycle per length (zero-based index of array)
function groups(config) {
  const counters = [ 0, 0, 0, 0, 0, 0, 0 ];
  let last = config[0];
  let size = 0;
  for (let i = 1; i < 7; i++) {
    if (config[i] - config[i - 1] !== 1) {
      counters[size]++;
      size = 0;
    } else {
      size++;
    }
  }
  counters[size]++;
  return counters;
}

// swap the first two elements, i.e.
// (a b c d e f g) becomes (b a c d e f g)
// this sequence was found by looking for it manually - a bit of magic
function swap(recorder, config) {
  left(recorder, config, "-");
  right(recorder, config, "+");
  left(recorder, config, "-");
  all(recorder, config, "-");
  all(recorder, config, "-");
  right(recorder, config, "-");
  right(recorder, config, "-");
  all(recorder, config, "-");
}

// moves "1" to the first position in the array
// with the least possible number of operations
function oneFirst(recorder, config) {
  let one = config.indexOf(1);
  if (one < 3) {
    while (one-- > 0) {
      all(recorder, config, "-");
    }
  } else {
    while (one++ < 7) {
      all(recorder, config, "+");
    }
  }
}

// helper to count the number of cycles, see below
function sum(total, next) {
  return total + next;
}

// orders any permutation of the numbers one to seven
// into the happy sequence 1 2 3 4 5 6 7
//
// first, the number of cycles are counted, if less than
// three, the solution is close: cycle count one means
// the sequence is as desired, cycle count two means that
// the number one needs to be rotated to first place
// for a larger cycle count, one is moved to first place,
// then the break in the cycle starting at one is found,
// moved to first place and numbers from the two cycles are 
// swapped until a cycle forms
function process(config) {
  const result = [];
  while (true) {
    const cycles = groups(config);
    const count = cycles.reduce(sum);
    if (count < 3) {
      success = true;
      if (count === 1) {
        break;
      } else {
        // fix by shortest rotation sequence
        oneFirst(result, config);
        break;
      }
    } else {
      // move 1 into first place
      oneFirst(result, config);
      // find first break, rotate to front...
      while (config[1] - config[0] === 1) {
        all(result, config, "-");
      }
      // ...and swap until a cycle is formed
      do {
        swap(result, config);
        all(result, config, "-");
      } while (config[1] - config[0] !== 1);
    }
  }
  return result;
}
