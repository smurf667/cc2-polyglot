;;;; turns an infix expression given via individual
;;;; tokens into a reverse polish notation expression
;;;;
;;;; this implementation follows the outline given in
;;;; https://en.wikipedia.org/wiki/Shunting-yard_algorithm#The_algorithm_in_detail
;;;;
;;;; disclaimer: the author is totally inexperienced in Lisp -
;;;;             this can probably be done much more elegantly

;;; returns whether the given token is an operator or not
(defun isOperator (token)
  (or
    (string= token "+")
    (string= token "-")
    (string= token "*")
    (string= token "/")
  )
)


;;; compares two operators and returns tue if the first
;;; operator has higher precedence than the second
(defun operatorGreaterOrEqual (me other)
  (if (or (string= me "*") (string= me "/"))
    t
    (if (or (string= me "+") (string= me "-"))
      (or (string= other "+") (string= other "-"))
    )
  )
)

;;; checks if there is a an operator with greater
;;; or equal precedence on the operator stack
(defun operatorPrecedence (operator opstack)
  (if (not opstack)
    nil
    (and
      (not (string= operator "("))
      (operatorGreaterOrEqual (car opstack) operator)
    )
  )
)

;;; parsing function taking
;;; 1) the remaining tokens to parse
;;; 2) the current result set (in reverse order, as push is used)
;;; 3) the operator stack
;;; returns the complete expression in reverse polish notation
(defun parse (tokens result opstack)
  (cond
    ;; we have remaining tokens... process the first one
    (tokens
      (setq token (car tokens))
      ;; look at the token
      (cond
        ;; is it an operator?
        ((isOperator token)
          ;; handle the operator logic part (see also Wikipedia page)
          (loop while (operatorPrecedence token opstack)
            do (push (pop opstack) result)
          )
          (push token opstack)
          ;; parse the rest
          (parse (cdr tokens) result opstack)
        )
        ;; is it a opening parenthesis?
        ((string= token "(")
          ;; push it on the stack and parse the rest
          (push token opstack)
          (parse (cdr tokens) result opstack)
        )
        ((string= token ")")
          ;; pop the operator stack as long as there is no
          ;; opening parenthesis
          (loop while (not (string= (car opstack) "("))
            do (push (pop opstack) result)
          )
          (pop opstack)
          (parse (cdr tokens) result opstack)
        )
        ;; ...else: this is a regular token (e.g. a number)
        ;; just push it on the result stack and parse the rest
        (t
          (push token result)
          (parse (cdr tokens) result opstack)
        )
      ))
    ;; no more tokens, pop the operator stack
    (t
      (loop while opstack
        do (push (pop opstack) result)
      )
      ;; we used push to put things into the result, so it
      ;; is in reverse order, return the reversed order...
      (reverse result))
  )
)

;;; the main entry point, takes the infix expression and
;;; calls the parsing with an empty result list and empty
;;; operator stack
(defun process (tokens)
  (parse tokens '() '())
)
