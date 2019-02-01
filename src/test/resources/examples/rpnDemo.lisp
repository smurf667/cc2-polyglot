(defun process (expression)
  "Puts a simple binary expression into reverse polish notation.
  This is done for demonstration purposes only.
  Returns nil if the expression cannot be transformed."
  (if (= 3 (list-length expression))
    ; the expression has three parts
    (list
      (car expression) ; first, the left side of the operator
      (caddr expression) ; next, the right side of the operator
      (cadr expression) ; finally, the operator
    )
    ; else - cannot transform (will return nil)
  )
)
