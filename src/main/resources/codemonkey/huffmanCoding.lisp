;;;; takes an input string and outputs the huffman code words
;;;; and encoded "binary" string as required by the challenge
;;;;
;;;; this implementation follows the basic technique as given at
;;;; https://en.wikipedia.org/wiki/Huffman_coding#Basic_technique
;;;;
;;;; disclaimer: the author is totally inexperienced in Lisp -
;;;;             this can probably be done much more elegantly

;;; counts the occurences of each character in the given string
(defun occurences (str)
  ;; start with an empty association list
  (setq counters ())
  ;; split into characters
  (dolist (char (coerce str 'list))
    ;; find the "character to count" pair (case SENSITIVE!)
    (setq pair (assoc char counters :test #'char=))
    ;; increase the count if found, or create a new pair
    (if pair
      (rplacd pair (+ 1 (cdr pair)))
      (setq counters (acons char 1 counters))
    )
  )
; the setq is an ABCL weirdness...
  ;; sort the counters - rarely occuring characters first
  (setq counters (sort counters #'< :key #'cdr))
  (return-from occurences counters)
)

;;; builds up a binary tree from a list of sub-trees
;;; the list has the sub-trees with smallest "weight" first
(defun codetree (semitree)
  (if (> 2 (list-length semitree))
    ;; we have arrived at the binary tree, return it
    (return-from codetree (car semitree))
  )
  ;; remove the first sub-tree from the list (smallest weight)
  (setq left (pop semitree))
  ;; remove the next sub-tree from the list (next-smallest weight)
  (setq right (pop semitree))
  ;; combine them into a new sub-tree (combine their weights, with
  ;; a new parent node for them) and push back to the list
  (push (cons (list left right) (+ (cdr left) (cdr right))) semitree)
; the setq is an ABCL weirdness...
  ;; sort the list so that sub-trees with smallest weight come first
  (setq semitree (sort semitree #'< :key #'cdr))
  ;; repeat building the tree
  (codetree semitree)
)

;;; builds the code words from the code word tree
;;; this walks the tree from the root and assigns "0" for a left
;;; branch and "1" for a right branch - the combination of these
;;; "0"s and "1"s make up the binary code word when arriving at a
;;; leaf (a character - as only leafs have characters assigned)
(defun codewords (tree result prefix)
  (if (string= "STANDARD-CHAR" (type-of (car tree)))
    (if (string= "" prefix)
      (acons (car tree) "0" result)
      (acons (car tree) prefix result)
    )
    (append
      (codewords (car (car tree)) result (concatenate 'string prefix "0"))
      (codewords (car (cdr (car tree))) result (concatenate 'string prefix "1"))
    )
  )
)

;;; encodes the given string based on the code words for an encoding
;;; this splits the string into characters, looks up the "binary" string
;;; for a character and concatenates that to the result string 
(defun encode (str codes)
  (setq encoded (reduce #'(lambda (current next)
    (concatenate 'string current next))
    (coerce str 'list)
    :key #'(lambda (c) (cdr (assoc c codes)))
    :initial-value ""))
  (cons codes encoded)
)

;;; main entry point as defined by the challenge
(defun process (str)
  (encode str (codewords (codetree (occurences str)) () ""))
)

